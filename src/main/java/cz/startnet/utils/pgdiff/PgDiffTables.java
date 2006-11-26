/*
 * $CVSHeader$
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
 * @version $CVSHeader$
 */
public class PgDiffTables {
    /**
     * Creates a new instance of PgDiffTables.
     */
    private PgDiffTables() {
        super();
    }

    /**
     * Creates diff of tables.
     *
     * @param writer writer the output should be written to
     * @param schema1 original schema
     * @param schema2 new schema
     */
    public static void diffTables(
        final PrintWriter writer,
        final PgSchema schema1,
        final PgSchema schema2) {
        dropTables(writer, schema1, schema2);
        createTables(writer, schema1, schema2);

        for (PgTable table : schema2.getTables().values()) {
            if (!schema1.containsTable(table.getName())) {
                continue;
            }

            updateTableFields(writer, schema1.getTable(table.getName()), table);
            addAlterStats(writer, schema1.getTable(table.getName()), table);
        }
    }

    /**
     * Generate the needed alter table xxx set statistics when needed.
     *
     * @param writer writer the output should be written to
     * @param table1 original table
     * @param table2 new table
     */
    private static void addAlterStats(
        final PrintWriter writer,
        final PgTable table1,
        final PgTable table2) {
        final Map<String, Integer> stats = new HashMap<String, Integer>();
        final Map<String, PgColumn> table1Cols = table1.getColumns();

        for (PgColumn column : table2.getColumns().values()) {
            final PgColumn col = table1Cols.get(column.getName());

            if (col != null) {
                final Integer oldStat = col.getStatistics();
                final Integer newStat = column.getStatistics();
                Integer statValue = null;

                if (oldStat == null) {
                    if (newStat != null) {
                        statValue = newStat;
                    }
                } else {
                    if (newStat == null) {
                        statValue = Integer.valueOf(-1);
                    } else {
                        if (newStat.compareTo(oldStat) != 0) {
                            statValue = newStat;
                        }
                    }
                }

                if (statValue != null) {
                    stats.put(column.getName(), statValue);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            writer.println();
            writer.print("ALTER TABLE ONLY ");
            writer.print(table2.getName());
            writer.print(" ALTER COLUMN");
            writer.print(entry.getKey());
            writer.print(" SET STATISTICS ");
            writer.print(entry.getValue());
            writer.print(" ;");
        }
    }

    /**
     * Adds commands for creation of new columns to the list of
     * commands.
     *
     * @param commands list of commands
     * @param table1 original table
     * @param table2 new table
     */
    private static void addCreateTableColumns(
        final List<String> commands,
        final PgTable table1,
        final PgTable table2) {
        for (PgColumn column : table2.getOrderedColumns()) {
            if (!table1.containsColumn(column.getName())) {
                commands.add("\tADD COLUMN " + column.getFullDefinition());
            }
        }
    }

    /**
     * Adds commands for removal of columns to the list of commands.
     *
     * @param commands list of commands
     * @param table1 original table
     * @param table2 new table
     */
    private static void addDropTableColumns(
        final List<String> commands,
        final PgTable table1,
        final PgTable table2) {
        for (PgColumn column : table1.getColumns().values()) {
            if (!table2.containsColumn(column.getName())) {
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
     * @param table1 original table
     * @param table2 new table
     */
    private static void addModifyTableColumns(
        final PrintWriter writer,
        final List<String> commands,
        final PgTable table1,
        final PgTable table2) {
        for (PgColumn column : table2.getColumns().values()) {
            if (!table1.containsColumn(column.getName())) {
                continue;
            }

            final PgColumn column1 = table1.getColumn(column.getName());

            if (!column1.getType().equals(column.getType())) {
                commands.add(
                        "\tALTER COLUMN " + column.getName() + " TYPE "
                        + column.getType());
            }

            final String default1 =
                (column1.getDefaultValue() == null) ? ""
                                                    : column1.getDefaultValue();
            final String default2 =
                (column.getDefaultValue() == null) ? "" : column.getDefaultValue();

            if (!default1.equals(default2)) {
                if (default2.length() == 0) {
                    commands.add(
                            "\tALTER COLUMN " + column.getName()
                            + " DROP DEFAULT");
                } else {
                    commands.add(
                            "\tALTER COLUMN " + column.getName()
                            + " SET DEFAULT " + default2);
                }
            }

            if (column1.getNullValue() != column.getNullValue()) {
                if (column.getNullValue()) {
                    commands.add(
                            "\tALTER COLUMN " + column.getName()
                            + " DROP NOT NULL");
                } else {
                    commands.add(
                            "\tALTER COLUMN " + column.getName()
                            + " SET NOT NULL");
                }
            }

            final String constraint =
                (column.getConstraint() == null) ? "" : column.getConstraint();
            final String constraint1 =
                (column1.getConstraint() == null) ? "" : column1.getConstraint();

            if (!constraint.equals(constraint1)) {
                writer.println();
                writer.println(
                        "MODIFIED CONSTRAINT ON COLUMN " + column.getName()
                        + " IN TABLE " + table2.getName());
                writer.println("ORIGINAL: " + constraint1);
                writer.println("NEW: " + constraint);
            }
        }
    }

    /**
     * Outputs commands for creation of new tables.
     *
     * @param writer writer the output should be written to
     * @param schema1 original schema
     * @param schema2 new schema
     */
    private static void createTables(
        final PrintWriter writer,
        final PgSchema schema1,
        final PgSchema schema2) {
        for (PgTable table : schema2.getTables().values()) {
            if (!schema1.containsTable(table.getName())) {
                writer.println();
                writer.println(table.getTableSQL());
            }
        }
    }

    /**
     * Outputs commands for dropping tables.
     *
     * @param writer writer the output should be written to
     * @param schema1 original schema
     * @param schema2 new schema
     */
    private static void dropTables(
        final PrintWriter writer,
        final PgSchema schema1,
        final PgSchema schema2) {
        for (PgTable table : schema1.getTables().values()) {
            if (!schema2.containsTable(table.getName())) {
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
     * @param table1 original table
     * @param table2 new table
     */
    private static void updateTableFields(
        final PrintWriter writer,
        final PgTable table1,
        final PgTable table2) {
        final List<String> commands = new ArrayList<String>();
        addDropTableColumns(commands, table1, table2);
        addCreateTableColumns(commands, table1, table2);
        addModifyTableColumns(writer, commands, table1, table2);

        if (commands.size() > 0) {
            writer.println();
            writer.println("ALTER TABLE " + table2.getName());

            for (int i = 0; i < commands.size(); i++) {
                writer.print(commands.get(i));
                writer.println(((i + 1) < commands.size()) ? "," : ";");
            }
        }
    }
}
