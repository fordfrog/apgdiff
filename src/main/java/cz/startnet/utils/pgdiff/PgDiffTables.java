/*
 * $Id$
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgColumnUtils;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diffs tables.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgDiffTables {

    /**
     * Creates a new instance of PgDiffTables.
     */
    private PgDiffTables() {
        super();
    }

    /**
     * Generates and outputs CLUSTER specific DDL if appropriate.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void diffClusters(
            final PrintWriter writer,
            final PgDiffArguments arguments,
            final PgSchema oldSchema,
            final PgSchema newSchema) {
        for (PgTable newTable : newSchema.getTables()) {
            final PgTable oldTable;

            if (oldSchema == null) {
                oldTable = null;
            } else {
                oldTable = oldSchema.getTable(newTable.getName());
            }

            final String oldCluster;

            if (oldTable == null) {
                oldCluster = null;
            } else {
                oldCluster = oldTable.getClusterIndexName();
            }

            final String newCluster = newTable.getClusterIndexName();

            if (((oldCluster == null) && (newCluster != null)) ||
                    ((oldCluster != null) && (newCluster != null) &&
                    (newCluster.compareTo(oldCluster) != 0))) {
                writer.println();
                writer.print("ALTER TABLE ");
                writer.print(
                        PgDiffUtils.getQuotedName(
                        newTable.getName(),
                        arguments.isQuoteNames()));
                writer.print(" CLUSTER ON ");
                writer.print(
                        PgDiffUtils.getQuotedName(
                        newCluster,
                        arguments.isQuoteNames()));
                writer.println(';');
            } else if ((oldCluster != null) && (newCluster == null) && newTable.
                    containsIndex(oldCluster)) {
                writer.println();
                writer.print("ALTER TABLE ");
                writer.print(
                        PgDiffUtils.getQuotedName(
                        newTable.getName(),
                        arguments.isQuoteNames()));
                writer.println(" SET WITHOUT CLUSTER;");
            }
        }
    }

    /**
     * Creates diff of tables.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void diffTables(
            final PrintWriter writer,
            final PgDiffArguments arguments,
            final PgSchema oldSchema,
            final PgSchema newSchema) {
        dropTables(writer, arguments, oldSchema, newSchema);
        createTables(writer, arguments, oldSchema, newSchema);

        for (PgTable newTable : newSchema.getTables()) {
            if ((oldSchema == null) || !oldSchema.containsTable(
                    newTable.getName())) {
                continue;
            }

            final PgTable oldTable = oldSchema.getTable(newTable.getName());
            updateTableColumns(writer, arguments, oldTable, newTable);
            checkWithOIDS(writer, arguments, oldTable, newTable);
            checkInherits(writer, arguments, oldTable, newTable);
            addAlterStatistics(writer, arguments, oldTable, newTable);
        }
    }

    /**
     * Generate the needed alter table xxx set statistics when needed.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldTable original table
     * @param newTable new table
     */
    private static void addAlterStatistics(
            final PrintWriter writer,
            final PgDiffArguments arguments,
            final PgTable oldTable,
            final PgTable newTable) {
        final Map<String, Integer> stats = new HashMap<String, Integer>();

        for (PgColumn newColumn : newTable.getColumns()) {
            final PgColumn oldColumn = oldTable.getColumn(newColumn.getName());

            if (oldColumn != null) {
                final Integer oldStat = oldColumn.getStatistics();
                final Integer newStat = newColumn.getStatistics();
                Integer newStatValue = null;

                if ((newStat != null) && ((oldStat == null) || !newStat.equals(
                        oldStat))) {
                    newStatValue = newStat;
                } else if ((oldStat != null) && (newStat == null)) {
                    newStatValue = Integer.valueOf(-1);
                }

                if (newStatValue != null) {
                    stats.put(newColumn.getName(), newStatValue);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            writer.println();
            writer.print("ALTER TABLE ONLY ");
            writer.print(
                    PgDiffUtils.getQuotedName(
                    newTable.getName(),
                    arguments.isQuoteNames()));
            writer.print(" ALTER COLUMN ");
            writer.print(
                    PgDiffUtils.getQuotedName(
                    entry.getKey(),
                    arguments.isQuoteNames()));
            writer.print(" SET STATISTICS ");
            writer.print(entry.getValue());
            writer.println(';');
        }
    }

    /**
     * Adds commands for creation of new columns to the list of
     * commands.
     *
     * @param commands list of commands
     * @param arguments object containing arguments settings
     * @param oldTable original table
     * @param newTable new table
     * @param dropDefaultsColumns list for storing columns for which default
     *        value should be dropped
     */
    private static void addCreateTableColumns(
            final List<String> commands,
            final PgDiffArguments arguments,
            final PgTable oldTable,
            final PgTable newTable,
            final List<PgColumn> dropDefaultsColumns) {
        for (PgColumn column : newTable.getColumns()) {
            if (!oldTable.containsColumn(column.getName())) {
                commands.add(
                        "\tADD COLUMN " + column.getFullDefinition(
                        arguments.isQuoteNames(),
                        arguments.isAddDefaults()));

                if (arguments.isAddDefaults() && !column.getNullValue()) {
                    dropDefaultsColumns.add(column);
                }
            }
        }
    }

    /**
     * Adds commands for removal of columns to the list of commands.
     *
     * @param commands list of commands
     * @param arguments object containing arguments settings
     * @param oldTable original table
     * @param newTable new table
     */
    private static void addDropTableColumns(
            final List<String> commands,
            final PgDiffArguments arguments,
            final PgTable oldTable,
            final PgTable newTable) {
        for (PgColumn column : oldTable.getColumns()) {
            if (!newTable.containsColumn(column.getName())) {
                commands.add(
                        "\tDROP COLUMN " + PgDiffUtils.getQuotedName(
                        column.getName(),
                        arguments.isQuoteNames()));
            }
        }
    }

    /**
     * Adds commands for modification of columns to the list of
     * commands.
     *
     * @param commands list of commands
     * @param arguments object containing arguments settings
     * @param oldTable original table
     * @param newTable new table
     * @param dropDefaultsColumns list for storing columns for which default
     *        value should be dropped
     */
    private static void addModifyTableColumns(
            final List<String> commands,
            final PgDiffArguments arguments,
            final PgTable oldTable,
            final PgTable newTable,
            final List<PgColumn> dropDefaultsColumns) {
        for (PgColumn newColumn : newTable.getColumns()) {
            if (!oldTable.containsColumn(newColumn.getName())) {
                continue;
            }

            final PgColumn oldColumn = oldTable.getColumn(newColumn.getName());
            final String newColumnName =
                    PgDiffUtils.getQuotedName(
                    newColumn.getName(),
                    arguments.isQuoteNames());

            if (!oldColumn.getType().equals(newColumn.getType())) {
                commands.add(
                        "\tALTER COLUMN " + newColumnName + " TYPE " +
                        newColumn.getType() + " /* TYPE change - table: " +
                        newTable.getName() + " original: " +
                        oldColumn.getType() + " new: " + newColumn.getType() +
                        " */");
            }

            final String oldDefault =
                    (oldColumn.getDefaultValue() == null) ? ""
                    : oldColumn.getDefaultValue();
            final String newDefault =
                    (newColumn.getDefaultValue() == null) ? ""
                    : newColumn.getDefaultValue();

            if (!oldDefault.equals(newDefault)) {
                if (newDefault.length() == 0) {
                    commands.add(
                            "\tALTER COLUMN " + newColumnName + " DROP DEFAULT");
                } else {
                    commands.add(
                            "\tALTER COLUMN " + newColumnName + " SET DEFAULT " +
                            newDefault);
                }
            }

            if (oldColumn.getNullValue() != newColumn.getNullValue()) {
                if (newColumn.getNullValue()) {
                    commands.add(
                            "\tALTER COLUMN " + newColumnName + " DROP NOT NULL");
                } else {
                    if (arguments.isAddDefaults()) {
                        final String defaultValue =
                                PgColumnUtils.getDefaultValue(
                                newColumn.getType());

                        if (defaultValue != null) {
                            commands.add(
                                    "\tALTER COLUMN " + newColumnName +
                                    " SET DEFAULT " + defaultValue);
                            dropDefaultsColumns.add(newColumn);
                        }
                    }

                    commands.add(
                            "\tALTER COLUMN " + newColumnName + " SET NOT NULL");
                }
            }
        }
    }

    /**
     * Checks whether there is a discrepancy in INHERITS for original
     * and new table.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldTable original table
     * @param newTable new table
     */
    private static void checkInherits(
            final PrintWriter writer,
            final PgDiffArguments arguments,
            final PgTable oldTable,
            final PgTable newTable) {
        final String oldInherits = oldTable.getInherits();
        final String newInherits = newTable.getInherits();

        if ((oldInherits == null) && (newInherits != null)) {
            writer.println();
            writer.println(
                    "Modified INHERITS on TABLE " + PgDiffUtils.getQuotedName(
                    newTable.getName(),
                    arguments.isQuoteNames()) +
                    ": original table doesn't use INHERITS but new table " +
                    "uses INHERITS " + newTable.getInherits());
        } else if ((oldInherits != null) && (newInherits == null)) {
            writer.println();
            writer.println(
                    "Modified INHERITS on TABLE " + PgDiffUtils.getQuotedName(
                    newTable.getName(),
                    arguments.isQuoteNames()) +
                    ": original table uses INHERITS " + oldTable.getInherits() +
                    " but new table doesn't use INHERITS");
        } else if ((oldInherits != null) && (newInherits != null) &&
                !oldInherits.equals(newInherits)) {
            writer.println();
            writer.println(
                    "Modified INHERITS on TABLE " + PgDiffUtils.getQuotedName(
                    newTable.getName(),
                    arguments.isQuoteNames()) +
                    ": original table uses INHERITS " + oldTable.getInherits() +
                    " but new table uses INHERITS " + newTable.getInherits());
        }
    }

    /**
     * Checks whether OIDS are dropped from the new table. There is no
     * way to add OIDS to existing table so we do not create SQL command for
     * addition of OIDS but we issue warning.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldTable original table
     * @param newTable new table
     */
    private static void checkWithOIDS(
            final PrintWriter writer,
            final PgDiffArguments arguments,
            final PgTable oldTable,
            final PgTable newTable) {
        if (oldTable.isWithOIDS() && !newTable.isWithOIDS()) {
            writer.println();
            writer.println(
                    "ALTER TABLE " + PgDiffUtils.getQuotedName(
                    newTable.getName(),
                    arguments.isQuoteNames()));
            writer.println("\tSET WITHOUT OIDS;");
        } else if (!oldTable.isWithOIDS() && newTable.isWithOIDS()) {
            writer.println();
            writer.println(
                    "WARNING: Table " + PgDiffUtils.getQuotedName(
                    newTable.getName(),
                    arguments.isQuoteNames()) +
                    " adds WITH OIDS but there is no equivalent command " +
                    "for adding of OIDS in PostgreSQL");
        }
    }

    /**
     * Outputs commands for creation of new tables.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    private static void createTables(
            final PrintWriter writer,
            final PgDiffArguments arguments,
            final PgSchema oldSchema,
            final PgSchema newSchema) {
        for (PgTable table : newSchema.getTables()) {
            if ((oldSchema == null) || !oldSchema.containsTable(table.getName())) {
                writer.println();
                writer.println(table.getCreationSQL(arguments.isQuoteNames()));
            }
        }
    }

    /**
     * Outputs commands for dropping tables.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    private static void dropTables(
            final PrintWriter writer,
            final PgDiffArguments arguments,
            final PgSchema oldSchema,
            final PgSchema newSchema) {
        if (oldSchema != null) {
            for (PgTable table : oldSchema.getTables()) {
                if (!newSchema.containsTable(table.getName())) {
                    writer.println();
                    writer.println(table.getDropSQL(arguments.isQuoteNames()));
                }
            }
        }
    }

    /**
     * Outputs commands for addition, removal and modifications of
     * table columns.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldTable original table
     * @param newTable new table
     */
    private static void updateTableColumns(
            final PrintWriter writer,
            final PgDiffArguments arguments,
            final PgTable oldTable,
            final PgTable newTable) {
        final List<String> commands = new ArrayList<String>();
        final List<PgColumn> dropDefaultsColumns = new ArrayList<PgColumn>();
        addDropTableColumns(commands, arguments, oldTable, newTable);
        addCreateTableColumns(
                commands,
                arguments,
                oldTable,
                newTable,
                dropDefaultsColumns);
        addModifyTableColumns(
                commands,
                arguments,
                oldTable,
                newTable,
                dropDefaultsColumns);

        if (commands.size() > 0) {
            final String quotedTableName =
                    PgDiffUtils.getQuotedName(
                    newTable.getName(),
                    arguments.isQuoteNames());
            writer.println();
            writer.println("ALTER TABLE " + quotedTableName);

            for (int i = 0; i < commands.size(); i++) {
                writer.print(commands.get(i));
                writer.println(((i + 1) < commands.size()) ? "," : ";");
            }

            if (!dropDefaultsColumns.isEmpty()) {
                writer.println();
                writer.println("ALTER TABLE " + quotedTableName);

                for (int i = 0; i < dropDefaultsColumns.size(); i++) {
                    writer.print("\tALTER COLUMN ");
                    writer.print(
                            PgDiffUtils.getQuotedName(
                            dropDefaultsColumns.get(i).getName(),
                            arguments.isQuoteNames()));
                    writer.print(" DROP DEFAULT");
                    writer.println(
                            ((i + 1) < dropDefaultsColumns.size()) ? "," : ";");
                }
            }
        }
    }
}
