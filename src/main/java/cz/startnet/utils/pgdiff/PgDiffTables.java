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
 */
public class PgDiffTables {

    /**
     * Creates a new instance of PgDiffTables.
     */
    private PgDiffTables() {
    }

    /**
     * Outputs commands for creation of clusters.
     *
     * @param writer writer the output should be written to
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void dropClusters(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema) {
        for (final PgTable newTable : newSchema.getTables()) {
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

            if (oldCluster != null && newCluster == null
                    && newTable.containsIndex(oldCluster)) {
                writer.println();
                writer.print("ALTER TABLE ");
                writer.print(PgDiffUtils.getQuotedName(newTable.getName()));
                writer.println(" SET WITHOUT CLUSTER;");
            }
        }
    }

    /**
     * Outputs commands for dropping of clusters.
     *
     * @param writer writer the output should be written to
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void createClusters(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema) {
        for (final PgTable newTable : newSchema.getTables()) {
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

            if ((oldCluster == null && newCluster != null)
                    || (oldCluster != null && newCluster != null
                    && newCluster.compareTo(oldCluster) != 0)) {
                writer.println();
                writer.print("ALTER TABLE ");
                writer.print(PgDiffUtils.getQuotedName(newTable.getName()));
                writer.print(" CLUSTER ON ");
                writer.print(PgDiffUtils.getQuotedName(newCluster));
                writer.println(';');
            }
        }
    }

    /**
     * Outputs commands for altering tables.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void alterTables(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema) {
        for (final PgTable newTable : newSchema.getTables()) {
            if (oldSchema == null
                    || !oldSchema.containsTable(newTable.getName())) {
                continue;
            }

            final PgTable oldTable = oldSchema.getTable(newTable.getName());
            updateTableColumns(writer, arguments, oldTable, newTable);
            checkWithOIDS(writer, oldTable, newTable);
            checkInherits(writer, oldTable, newTable);
            checkTablespace(writer, oldTable, newTable);
            addAlterStatistics(writer, oldTable, newTable);
            addAlterStorage(writer, oldTable, newTable);
        }
    }

    /**
     * Generate the needed alter table xxx set statistics when needed.
     *
     * @param writer writer the output should be written to
     * @param oldTable original table
     * @param newTable new table
     */
    private static void addAlterStatistics(final PrintWriter writer,
            final PgTable oldTable, final PgTable newTable) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final Map<String, Integer> stats = new HashMap<String, Integer>();

        for (final PgColumn newColumn : newTable.getColumns()) {
            final PgColumn oldColumn = oldTable.getColumn(newColumn.getName());

            if (oldColumn != null) {
                final Integer oldStat = oldColumn.getStatistics();
                final Integer newStat = newColumn.getStatistics();
                Integer newStatValue = null;

                if (newStat != null && (oldStat == null
                        || !newStat.equals(oldStat))) {
                    newStatValue = newStat;
                } else if (oldStat != null && newStat == null) {
                    newStatValue = Integer.valueOf(-1);
                }

                if (newStatValue != null) {
                    stats.put(newColumn.getName(), newStatValue);
                }
            }
        }

        for (final Map.Entry<String, Integer> entry : stats.entrySet()) {
            writer.println();
            writer.print("ALTER TABLE ONLY ");
            writer.print(PgDiffUtils.getQuotedName(newTable.getName()));
            writer.print(" ALTER COLUMN ");
            writer.print(PgDiffUtils.getQuotedName(entry.getKey()));
            writer.print(" SET STATISTICS ");
            writer.print(entry.getValue());
            writer.println(';');
        }
    }

    /**
     * Generate the needed alter table xxx set storage when needed.
     *
     * @param writer writer the output should be written to
     * @param oldTable original table
     * @param newTable new table
     */
    private static void addAlterStorage(final PrintWriter writer,
            final PgTable oldTable, final PgTable newTable) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final Map<String, Integer> stats = new HashMap<String, Integer>();

        for (final PgColumn newColumn : newTable.getColumns()) {
            final PgColumn oldColumn = oldTable.getColumn(newColumn.getName());
            final String oldStorage = (oldColumn == null
                    || oldColumn.getStorage() == null
                    || oldColumn.getStorage().isEmpty()) ? null
                    : oldColumn.getStorage();
            final String newStorage = (newColumn.getStorage() == null
                    || newColumn.getStorage().isEmpty()) ? null
                    : newColumn.getStorage();

            if (newStorage == null && oldStorage != null) {
                writer.println();
                writer.println("WARNING: Column " + newTable.getName()
                        + '.' + newColumn.getName()
                        + " in new table has no STORAGE set but in old "
                        + "table storage was set. Unable to determine STORAGE "
                        + "type");

                continue;
            }

            if (newStorage == null || newStorage.equalsIgnoreCase(oldStorage)) {
                continue;
            }

            writer.println();
            writer.print("ALTER TABLE ONLY ");
            writer.print(PgDiffUtils.getQuotedName(newTable.getName()));
            writer.print(" ALTER COLUMN ");
            writer.print(PgDiffUtils.getQuotedName(newColumn.getName()));
            writer.print(" SET STORAGE ");
            writer.print(newStorage);
            writer.print(';');
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
    private static void addCreateTableColumns(final List<String> commands,
            final PgDiffArguments arguments, final PgTable oldTable,
            final PgTable newTable, final List<PgColumn> dropDefaultsColumns) {
        for (final PgColumn column : newTable.getColumns()) {
            if (!oldTable.containsColumn(column.getName())) {
                commands.add("\tADD COLUMN "
                        + column.getFullDefinition(arguments.isAddDefaults()));

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
     * @param oldTable original table
     * @param newTable new table
     */
    private static void addDropTableColumns(final List<String> commands,
            final PgTable oldTable, final PgTable newTable) {
        for (final PgColumn column : oldTable.getColumns()) {
            if (!newTable.containsColumn(column.getName())) {
                commands.add("\tDROP COLUMN "
                        + PgDiffUtils.getQuotedName(column.getName()));
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
    private static void addModifyTableColumns(final List<String> commands,
            final PgDiffArguments arguments, final PgTable oldTable,
            final PgTable newTable, final List<PgColumn> dropDefaultsColumns) {
        for (final PgColumn newColumn : newTable.getColumns()) {
            if (!oldTable.containsColumn(newColumn.getName())) {
                continue;
            }

            final PgColumn oldColumn = oldTable.getColumn(newColumn.getName());
            final String newColumnName =
                    PgDiffUtils.getQuotedName(newColumn.getName());

            if (!oldColumn.getType().equals(newColumn.getType())) {
                commands.add("\tALTER COLUMN " + newColumnName + " TYPE "
                        + newColumn.getType() + " /* TYPE change - table: "
                        + newTable.getName() + " original: "
                        + oldColumn.getType() + " new: " + newColumn.getType()
                        + " */");
            }

            final String oldDefault = (oldColumn.getDefaultValue() == null) ? ""
                    : oldColumn.getDefaultValue();
            final String newDefault = (newColumn.getDefaultValue() == null) ? ""
                    : newColumn.getDefaultValue();

            if (!oldDefault.equals(newDefault)) {
                if (newDefault.length() == 0) {
                    commands.add("\tALTER COLUMN " + newColumnName
                            + " DROP DEFAULT");
                } else {
                    commands.add("\tALTER COLUMN " + newColumnName
                            + " SET DEFAULT " + newDefault);
                }
            }

            if (oldColumn.getNullValue() != newColumn.getNullValue()) {
                if (newColumn.getNullValue()) {
                    commands.add("\tALTER COLUMN " + newColumnName
                            + " DROP NOT NULL");
                } else {
                    if (arguments.isAddDefaults()) {
                        final String defaultValue =
                                PgColumnUtils.getDefaultValue(
                                newColumn.getType());

                        if (defaultValue != null) {
                            commands.add("\tALTER COLUMN " + newColumnName
                                    + " SET DEFAULT " + defaultValue);
                            dropDefaultsColumns.add(newColumn);
                        }
                    }

                    commands.add("\tALTER COLUMN " + newColumnName
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
    private static void checkInherits(final PrintWriter writer,
            final PgTable oldTable, final PgTable newTable) {
        for (final String tableName : oldTable.getInherits()) {
            if (!newTable.getInherits().contains(tableName)) {
                writer.println();
                writer.println("ALTER TABLE "
                        + PgDiffUtils.getQuotedName(newTable.getName()));
                writer.println("\tNO INHERIT "
                        + PgDiffUtils.getQuotedName(tableName) + ';');
            }
        }

        for (final String tableName : newTable.getInherits()) {
            if (!oldTable.getInherits().contains(tableName)) {
                writer.println();
                writer.println("ALTER TABLE "
                        + PgDiffUtils.getQuotedName(newTable.getName()));
                writer.println("\tINHERIT "
                        + PgDiffUtils.getQuotedName(tableName) + ';');
            }
        }
    }

    /**
     * Checks whether OIDS are dropped from the new table. There is no
     * way to add OIDS to existing table so we do not create SQL command for
     * addition of OIDS but we issue warning.
     *
     * @param writer writer the output should be written to
     * @param oldTable original table
     * @param newTable new table
     */
    private static void checkWithOIDS(final PrintWriter writer,
            final PgTable oldTable, final PgTable newTable) {
        if (oldTable.getWith() == null && newTable.getWith() == null
                || oldTable.getWith() != null
                && oldTable.getWith().equals(newTable.getWith())) {
            return;
        }

        writer.println();
        writer.println("ALTER TABLE "
                + PgDiffUtils.getQuotedName(newTable.getName()));

        if (newTable.getWith() == null
                || "OIDS=false".equalsIgnoreCase(newTable.getWith())) {
            writer.println("\tSET WITHOUT OIDS;");
        } else if ("OIDS".equalsIgnoreCase(newTable.getWith())
                || "OIDS=true".equalsIgnoreCase(newTable.getWith())) {
            writer.println("\tSET WITH OIDS;");
        } else {
            writer.println("\tSET " + newTable.getWith() + ";");
        }
    }

    /**
     * Checks tablespace modification.
     *
     * @param writer writer
     * @param oldTable old table
     * @param newTable new table
     */
    private static void checkTablespace(final PrintWriter writer,
            final PgTable oldTable, final PgTable newTable) {
        if (oldTable.getTablespace() == null && newTable.getTablespace() == null
                || oldTable.getTablespace() != null
                && oldTable.getTablespace().equals(newTable.getTablespace())) {
            return;
        }

        writer.println();
        writer.println("ALTER TABLE "
                + PgDiffUtils.getQuotedName(newTable.getName()));
        writer.println("\tTABLESPACE " + newTable.getTablespace() + ';');
    }

    /**
     * Outputs commands for creation of new tables.
     *
     * @param writer writer the output should be written to
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void createTables(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema) {
        for (final PgTable table : newSchema.getTables()) {
            if (oldSchema == null
                    || !oldSchema.containsTable(table.getName())) {
                writer.println();
                writer.println(table.getCreationSQL());
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
    public static void dropTables(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema) {
        if (oldSchema != null) {
            for (final PgTable table : oldSchema.getTables()) {
                if (!newSchema.containsTable(table.getName())) {
                    writer.println();
                    writer.println(table.getDropSQL());
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
    private static void updateTableColumns(final PrintWriter writer,
            final PgDiffArguments arguments, final PgTable oldTable,
            final PgTable newTable) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<String> commands = new ArrayList<String>();
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgColumn> dropDefaultsColumns = new ArrayList<PgColumn>();
        addDropTableColumns(commands, oldTable, newTable);
        addCreateTableColumns(
                commands, arguments, oldTable, newTable, dropDefaultsColumns);
        addModifyTableColumns(
                commands, arguments, oldTable, newTable, dropDefaultsColumns);

        if (!commands.isEmpty()) {
            final String quotedTableName =
                    PgDiffUtils.getQuotedName(newTable.getName());
            writer.println();
            writer.println("ALTER TABLE " + quotedTableName);

            for (int i = 0; i < commands.size(); i++) {
                writer.print(commands.get(i));
                writer.println((i + 1) < commands.size() ? "," : ";");
            }

            if (!dropDefaultsColumns.isEmpty()) {
                writer.println();
                writer.println("ALTER TABLE " + quotedTableName);

                for (int i = 0; i < dropDefaultsColumns.size(); i++) {
                    writer.print("\tALTER COLUMN ");
                    writer.print(PgDiffUtils.getQuotedName(
                            dropDefaultsColumns.get(i).getName()));
                    writer.print(" DROP DEFAULT");
                    writer.println(
                            (i + 1) < dropDefaultsColumns.size() ? "," : ";");
                }
            }
        }
    }
}
