/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgColumnUtils;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;
import java.io.PrintWriter;
import java.text.MessageFormat;
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
     * Outputs statements for creation of clusters.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void dropClusters(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
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
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("ALTER TABLE ");
                writer.print(PgDiffUtils.getQuotedName(newTable.getName()));
                writer.println(" SET WITHOUT CLUSTER;");
            }
        }
    }

    /**
     * Outputs statements for dropping of clusters.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void createClusters(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
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
                searchPathHelper.outputSearchPath(writer);
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
     * Outputs statements for altering tables.
     *
     * @param writer           writer the output should be written to
     * @param arguments        object containing arguments settings
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void alterTables(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema, final SearchPathHelper searchPathHelper) {
        for (final PgTable newTable : newSchema.getTables()) {
            if (oldSchema == null
                    || !oldSchema.containsTable(newTable.getName())) {
                continue;
            }

            final PgTable oldTable = oldSchema.getTable(newTable.getName());
            updateTableColumns(
                    writer, arguments, oldTable, newTable, searchPathHelper);
            checkWithOIDS(writer, oldTable, newTable, searchPathHelper);
            checkInherits(writer, oldTable, newTable, searchPathHelper);
            checkTablespace(writer, oldTable, newTable, searchPathHelper);
            addAlterStatistics(writer, oldTable, newTable, searchPathHelper);
            addAlterStorage(writer, oldTable, newTable, searchPathHelper);
            alterComments(writer, oldTable, newTable, searchPathHelper);
        }
    }

    /**
     * Generate the needed alter table xxx set statistics when needed.
     *
     * @param writer           writer the output should be written to
     * @param oldTable         original table
     * @param newTable         new table
     * @param searchPathHelper search path helper
     */
    private static void addAlterStatistics(final PrintWriter writer,
            final PgTable oldTable, final PgTable newTable,
            final SearchPathHelper searchPathHelper) {
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
            searchPathHelper.outputSearchPath(writer);
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
     * @param writer           writer the output should be written to
     * @param oldTable         original table
     * @param newTable         new table
     * @param searchPathHelper search path helper
     */
    private static void addAlterStorage(final PrintWriter writer,
            final PgTable oldTable, final PgTable newTable,
            final SearchPathHelper searchPathHelper) {
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
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(MessageFormat.format(Resources.getString(
                        "WarningUnableToDetermineStorageType"),
                        newTable.getName() + '.' + newColumn.getName()));

                continue;
            }

            if (newStorage == null || newStorage.equalsIgnoreCase(oldStorage)) {
                continue;
            }

            searchPathHelper.outputSearchPath(writer);
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
     * Adds statements for creation of new columns to the list of statements.
     *
     * @param statements          list of statements
     * @param arguments           object containing arguments settings
     * @param oldTable            original table
     * @param newTable            new table
     * @param dropDefaultsColumns list for storing columns for which default
     *                            value should be dropped
     */
    private static void addCreateTableColumns(final List<String> statements,
            final PgDiffArguments arguments, final PgTable oldTable,
            final PgTable newTable, final List<PgColumn> dropDefaultsColumns) {
        for (final PgColumn column : newTable.getColumns()) {
            if (!oldTable.containsColumn(column.getName())) {
                statements.add("\tADD COLUMN "
                        + column.getFullDefinition(arguments.isAddDefaults()));

                if (arguments.isAddDefaults() && !column.getNullValue()
                        && (column.getDefaultValue() == null
                        || column.getDefaultValue().isEmpty())) {
                    dropDefaultsColumns.add(column);
                }
            }
        }
    }

    /**
     * Adds statements for removal of columns to the list of statements.
     *
     * @param statements list of statements
     * @param oldTable   original table
     * @param newTable   new table
     */
    private static void addDropTableColumns(final List<String> statements,
            final PgTable oldTable, final PgTable newTable) {
        for (final PgColumn column : oldTable.getColumns()) {
            if (!newTable.containsColumn(column.getName())) {
                statements.add("\tDROP COLUMN "
                        + PgDiffUtils.getQuotedName(column.getName()));
            }
        }
    }

    /**
     * Adds statements for modification of columns to the list of statements.
     *
     * @param statements          list of statements
     * @param arguments           object containing arguments settings
     * @param oldTable            original table
     * @param newTable            new table
     * @param dropDefaultsColumns list for storing columns for which default
     *                            value should be dropped
     */
    private static void addModifyTableColumns(final List<String> statements,
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
                statements.add("\tALTER COLUMN " + newColumnName + " TYPE "
                        + newColumn.getType() + " /* "
                        + MessageFormat.format(
                        Resources.getString("TypeParameterChange"),
                        newTable.getName(), oldColumn.getType(),
                        newColumn.getType()) + " */");
            }

            final String oldDefault = (oldColumn.getDefaultValue() == null) ? ""
                    : oldColumn.getDefaultValue();
            final String newDefault = (newColumn.getDefaultValue() == null) ? ""
                    : newColumn.getDefaultValue();

            if (!oldDefault.equals(newDefault)) {
                if (newDefault.length() == 0) {
                    statements.add("\tALTER COLUMN " + newColumnName
                            + " DROP DEFAULT");
                } else {
                    statements.add("\tALTER COLUMN " + newColumnName
                            + " SET DEFAULT " + newDefault);
                }
            }

            if (oldColumn.getNullValue() != newColumn.getNullValue()) {
                if (newColumn.getNullValue()) {
                    statements.add("\tALTER COLUMN " + newColumnName
                            + " DROP NOT NULL");
                } else {
                    if (arguments.isAddDefaults()) {
                        final String defaultValue =
                                PgColumnUtils.getDefaultValue(
                                newColumn.getType());

                        if (defaultValue != null) {
                            statements.add("\tALTER COLUMN " + newColumnName
                                    + " SET DEFAULT " + defaultValue);
                            dropDefaultsColumns.add(newColumn);
                        }
                    }

                    statements.add("\tALTER COLUMN " + newColumnName
                            + " SET NOT NULL");
                }
            }
        }
    }

    /**
     * Checks whether there is a discrepancy in INHERITS for original and new
     * table.
     *
     * @param writer           writer the output should be written to
     * @param oldTable         original table
     * @param newTable         new table
     * @param searchPathHelper search path helper
     */
    private static void checkInherits(final PrintWriter writer,
            final PgTable oldTable, final PgTable newTable,
            final SearchPathHelper searchPathHelper) {
        for (final String tableName : oldTable.getInherits()) {
            if (!newTable.getInherits().contains(tableName)) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println("ALTER TABLE "
                        + PgDiffUtils.getQuotedName(newTable.getName()));
                writer.println("\tNO INHERIT "
                        + PgDiffUtils.getQuotedName(tableName) + ';');
            }
        }

        for (final String tableName : newTable.getInherits()) {
            if (!oldTable.getInherits().contains(tableName)) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println("ALTER TABLE "
                        + PgDiffUtils.getQuotedName(newTable.getName()));
                writer.println("\tINHERIT "
                        + PgDiffUtils.getQuotedName(tableName) + ';');
            }
        }
    }

    /**
     * Checks whether OIDS are dropped from the new table. There is no way to
     * add OIDS to existing table so we do not create SQL statement for addition
     * of OIDS but we issue warning.
     *
     * @param writer           writer the output should be written to
     * @param oldTable         original table
     * @param newTable         new table
     * @param searchPathHelper search path helper
     */
    private static void checkWithOIDS(final PrintWriter writer,
            final PgTable oldTable, final PgTable newTable,
            final SearchPathHelper searchPathHelper) {
        if (oldTable.getWith() == null && newTable.getWith() == null
                || oldTable.getWith() != null
                && oldTable.getWith().equals(newTable.getWith())) {
            return;
        }

        searchPathHelper.outputSearchPath(writer);
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
     * @param writer           writer
     * @param oldTable         old table
     * @param newTable         new table
     * @param searchPathHelper search path helper
     */
    private static void checkTablespace(final PrintWriter writer,
            final PgTable oldTable, final PgTable newTable,
            final SearchPathHelper searchPathHelper) {
        if (oldTable.getTablespace() == null && newTable.getTablespace() == null
                || oldTable.getTablespace() != null
                && oldTable.getTablespace().equals(newTable.getTablespace())) {
            return;
        }

        searchPathHelper.outputSearchPath(writer);
        writer.println();
        writer.println("ALTER TABLE "
                + PgDiffUtils.getQuotedName(newTable.getName()));
        writer.println("\tTABLESPACE " + newTable.getTablespace() + ';');
    }

    /**
     * Outputs statements for creation of new tables.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void createTables(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        for (final PgTable table : newSchema.getTables()) {
            if (oldSchema == null
                    || !oldSchema.containsTable(table.getName())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(table.getCreationSQL());
            }
        }
    }

    /**
     * Outputs statements for dropping tables.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void dropTables(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        if (oldSchema == null) {
            return;
        }

        for (final PgTable table : oldSchema.getTables()) {
            if (!newSchema.containsTable(table.getName())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(table.getDropSQL());
            }
        }
    }

    /**
     * Outputs statements for addition, removal and modifications of table
     * columns.
     *
     * @param writer           writer the output should be written to
     * @param arguments        object containing arguments settings
     * @param oldTable         original table
     * @param newTable         new table
     * @param searchPathHelper search path helper
     */
    private static void updateTableColumns(final PrintWriter writer,
            final PgDiffArguments arguments, final PgTable oldTable,
            final PgTable newTable, final SearchPathHelper searchPathHelper) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<String> statements = new ArrayList<String>();
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgColumn> dropDefaultsColumns = new ArrayList<PgColumn>();
        addDropTableColumns(statements, oldTable, newTable);
        addCreateTableColumns(
                statements, arguments, oldTable, newTable, dropDefaultsColumns);
        addModifyTableColumns(
                statements, arguments, oldTable, newTable, dropDefaultsColumns);

        if (!statements.isEmpty()) {
            final String quotedTableName =
                    PgDiffUtils.getQuotedName(newTable.getName());
            searchPathHelper.outputSearchPath(writer);
            writer.println();
            writer.println("ALTER TABLE " + quotedTableName);

            for (int i = 0; i < statements.size(); i++) {
                writer.print(statements.get(i));
                writer.println((i + 1) < statements.size() ? "," : ";");
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

    /**
     * Outputs statements for tables and columns for which comments have
     * changed.
     *
     * @param writer           writer
     * @param oldTable         old table
     * @param newTable         new table
     * @param searchPathHelper search path helper
     */
    private static void alterComments(final PrintWriter writer,
            final PgTable oldTable, final PgTable newTable,
            final SearchPathHelper searchPathHelper) {
        if (oldTable.getComment() == null
                && newTable.getComment() != null
                || oldTable.getComment() != null
                && newTable.getComment() != null
                && !oldTable.getComment().equals(newTable.getComment())) {
            searchPathHelper.outputSearchPath(writer);
            writer.println();
            writer.print("COMMENT ON TABLE ");
            writer.print(PgDiffUtils.getQuotedName(newTable.getName()));
            writer.print(" IS ");
            writer.print(newTable.getComment());
            writer.println(';');
        } else if (oldTable.getComment() != null
                && newTable.getComment() == null) {
            searchPathHelper.outputSearchPath(writer);
            writer.println();
            writer.print("COMMENT ON TABLE ");
            writer.print(PgDiffUtils.getQuotedName(newTable.getName()));
            writer.println(" IS NULL;");
        }

        for (final PgColumn newColumn : newTable.getColumns()) {
            final PgColumn oldColumn = oldTable.getColumn(newColumn.getName());
            final String oldComment =
                    oldColumn == null ? null : oldColumn.getComment();
            final String newComment = newColumn.getComment();

            if (newComment != null && (oldComment == null ? newComment != null
                    : !oldComment.equals(newComment))) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("COMMENT ON COLUMN ");
                writer.print(PgDiffUtils.getQuotedName(newTable.getName()));
                writer.print('.');
                writer.print(PgDiffUtils.getQuotedName(newColumn.getName()));
                writer.print(" IS ");
                writer.print(newColumn.getComment());
                writer.println(';');
            } else if (oldComment != null && newComment == null) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("COMMENT ON COLUMN ");
                writer.print(PgDiffUtils.getQuotedName(newTable.getName()));
                writer.print('.');
                writer.print(PgDiffUtils.getQuotedName(newColumn.getName()));
                writer.println(" IS NULL;");
            }
        }
    }

    /**
     * Creates a new instance of PgDiffTables.
     */
    private PgDiffTables() {
    }
}
