/*
 * $Id$
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgColumn;
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
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void diffClusters(
        final PrintWriter writer,
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        for (PgTable newTable : newSchema.getTables().values()) {
            final PgTable oldTable = oldSchema.getTable(newTable.getName());

            if (oldTable == null) {
                continue;
            }

            final String oldCluster = oldTable.getClusterIndexName();
            final String newCluster = newTable.getClusterIndexName();

            if (
                ((oldCluster == null) && (newCluster != null))
                    || ((oldCluster != null) && (newCluster != null)
                    && (newCluster.compareTo(oldCluster) != 0))) {
                writer.println();
                writer.print("ALTER TABLE ");
                writer.print(newTable.getName());
                writer.print(" CLUSTER ON ");
                writer.print(newCluster);
                writer.println(';');
            } else if (
                (oldCluster != null)
                    && (newCluster == null)
                    && newTable.containsIndex(oldCluster)) {
                writer.println();
                writer.print("ALTER TABLE ");
                writer.print(newTable.getName());
                writer.println(" SET WITHOUT CLUSTER;");
            }
        }
    }

    /**
     * Creates diff of tables.
     *
     * @param writer writer the output should be written to
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void diffTables(
        final PrintWriter writer,
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        dropTables(writer, oldSchema, newSchema);
        createTables(writer, oldSchema, newSchema);

        for (PgTable newTable : newSchema.getTables().values()) {
            if (!oldSchema.containsTable(newTable.getName())) {
                continue;
            }

            final PgTable oldTable = oldSchema.getTable(newTable.getName());
            updateTableFields(writer, oldTable, newTable);
            checkInherits(writer, oldTable, newTable);
            addAlterStats(writer, oldTable, newTable);
        }
    }

    /**
     * Generate the needed alter table xxx set statistics when needed.
     *
     * @param writer writer the output should be written to
     * @param oldTable original table
     * @param newTable new table
     */
    private static void addAlterStats(
        final PrintWriter writer,
        final PgTable oldTable,
        final PgTable newTable) {
        final Map<String, Integer> stats = new HashMap<String, Integer>();
        final Map<String, PgColumn> oldTableCols = oldTable.getColumns();

        for (PgColumn newColumn : newTable.getColumns().values()) {
            final PgColumn oldColumn = oldTableCols.get(newColumn.getName());

            if (oldColumn != null) {
                final Integer oldStat = oldColumn.getStatistics();
                final Integer newStat = newColumn.getStatistics();
                Integer newStatValue = null;

                if (
                    (newStat != null)
                        && ((oldStat == null) || !newStat.equals(oldStat))) {
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
            writer.print(newTable.getName());
            writer.print(" ALTER COLUMN ");
            writer.print(entry.getKey());
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
     * @param oldTable original table
     * @param newTable new table
     */
    private static void addCreateTableColumns(
        final List<String> commands,
        final PgTable oldTable,
        final PgTable newTable) {
        for (PgColumn column : newTable.getOrderedColumns()) {
            if (!oldTable.containsColumn(column.getName())) {
                commands.add("\tADD COLUMN " + column.getFullDefinition());
            }
        }
    }

    /**
     * Adds commands for removal of columns to the list of commands.
     *
     * @param commands list of commands
     * @param oldTable original table
     * @param newTable new table
     */
    private static void addDropTableColumns(
        final List<String> commands,
        final PgTable oldTable,
        final PgTable newTable) {
        for (PgColumn column : oldTable.getColumns().values()) {
            if (!newTable.containsColumn(column.getName())) {
                commands.add("\tDROP COLUMN " + column.getName());
            }
        }
    }

    /**
     * Adds commands for modification of columns to the list of
     * commands.
     *
     * @param writer writer the output should be written to
     * @param commands list of commands
     * @param oldTable original table
     * @param newTable new table
     */
    private static void addModifyTableColumns(
        final PrintWriter writer,
        final List<String> commands,
        final PgTable oldTable,
        final PgTable newTable) {
        for (PgColumn newColumn : newTable.getColumns().values()) {
            if (!oldTable.containsColumn(newColumn.getName())) {
                continue;
            }

            final PgColumn oldColumn = oldTable.getColumn(newColumn.getName());

            if (!oldColumn.getType().equals(newColumn.getType())) {
                commands.add(
                        "\tALTER COLUMN " + newColumn.getName() + " TYPE "
                        + newColumn.getType());
            }

            final String oldDefault =
                (oldColumn.getDefaultValue() == null) ? ""
                                                      : oldColumn
                .getDefaultValue();
            final String newDefault =
                (newColumn.getDefaultValue() == null) ? ""
                                                      : newColumn
                .getDefaultValue();

            if (!oldDefault.equals(newDefault)) {
                if (newDefault.length() == 0) {
                    commands.add(
                            "\tALTER COLUMN " + newColumn.getName()
                            + " DROP DEFAULT");
                } else {
                    commands.add(
                            "\tALTER COLUMN " + newColumn.getName()
                            + " SET DEFAULT " + newDefault);
                }
            }

            if (oldColumn.getNullValue() != newColumn.getNullValue()) {
                if (newColumn.getNullValue()) {
                    commands.add(
                            "\tALTER COLUMN " + newColumn.getName()
                            + " DROP NOT NULL");
                } else {
                    commands.add(
                            "\tALTER COLUMN " + newColumn.getName()
                            + " SET NOT NULL");
                }
            }
        }
    }

    /**
     * Checks whether there is a discrepancy in INHERITS for original
     * and new table.
     *
     * @param writer writer the output should be written to
     * @param oldTable original table
     * @param newTable new table
     */
    private static void checkInherits(
        final PrintWriter writer,
        final PgTable oldTable,
        final PgTable newTable) {
        final String oldInherits = oldTable.getInherits();
        final String newInherits = newTable.getInherits();

        if ((oldInherits == null) && (newInherits != null)) {
            writer.println();
            writer.println(
                    "Modified INHERITS on TABLE " + newTable.getName()
                    + ": original table doesn't use INHERITS but new table "
                    + "uses INHERITS " + newTable.getInherits());
        } else if ((oldInherits != null) && (newInherits == null)) {
            writer.println();
            writer.println(
                    "Modified INHERITS on TABLE " + newTable.getName()
                    + ": original table uses INHERITS "
                    + oldTable.getInherits()
                    + " but new table doesn't use INHERITS");
        } else if (
            (oldInherits != null)
                && (newInherits != null)
                && !oldInherits.equals(newInherits)) {
            writer.println();
            writer.println(
                    "Modified INHERITS on TABLE " + newTable.getName()
                    + ": original table uses INHERITS "
                    + oldTable.getInherits() + " but new table uses INHERITS "
                    + newTable.getInherits());
        }
    }

    /**
     * Outputs commands for creation of new tables.
     *
     * @param writer writer the output should be written to
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    private static void createTables(
        final PrintWriter writer,
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        for (PgTable table : newSchema.getTables().values()) {
            if (!oldSchema.containsTable(table.getName())) {
                writer.println();
                writer.println(table.getTableSQL());
            }
        }
    }

    /**
     * Outputs commands for dropping tables.
     *
     * @param writer writer the output should be written to
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    private static void dropTables(
        final PrintWriter writer,
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        for (PgTable table : oldSchema.getTables().values()) {
            if (!newSchema.containsTable(table.getName())) {
                writer.println();
                writer.println("DROP TABLE " + table.getName() + ";");
            }
        }
    }

    /**
     * Outputs commands for addition, removal and modifications of
     * table fields.
     *
     * @param writer writer the output should be written to
     * @param oldTable original table
     * @param newTable new table
     */
    private static void updateTableFields(
        final PrintWriter writer,
        final PgTable oldTable,
        final PgTable newTable) {
        final List<String> commands = new ArrayList<String>();
        addDropTableColumns(commands, oldTable, newTable);
        addCreateTableColumns(commands, oldTable, newTable);
        addModifyTableColumns(writer, commands, oldTable, newTable);

        if (commands.size() > 0) {
            writer.println();
            writer.println("ALTER TABLE " + newTable.getName());

            for (int i = 0; i < commands.size(); i++) {
                writer.print(commands.get(i));
                writer.println(((i + 1) < commands.size()) ? "," : ";");
            }
        }
    }
}
