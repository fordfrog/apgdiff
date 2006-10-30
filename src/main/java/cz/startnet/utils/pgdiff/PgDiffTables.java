/*
 * $CVSHeader$
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;

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
     * @param schema1 original schema
     * @param schema2 new schema
     */
    public static void diffTables(
        final PgSchema schema1,
        final PgSchema schema2) {
        dropTables(schema1, schema2);
        createTables(schema1, schema2);

        for (PgTable table : schema2.getTables().values()) {
            if (!schema1.getTables().containsKey(table.getName())) {
                continue;
            }

            updateTableFields(schema1.getTable(table.getName()), table);
            addAlterStats(schema1.getTable(table.getName()), table);
        }
    }

    /**
     * Generate the needed alter table xxx set statistics when needed
     * ....
     *
     * @param table1 original table
     * @param table2 new table
     */
    private static void addAlterStats(
        final PgTable table1,
        final PgTable table2) {
        final Map<String, Integer> list = new HashMap<String, Integer>();
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
                    list.put(column.getName(), statValue);
                }
            }
        }

        final StringBuilder sbSQL = new StringBuilder();

        for (String colName : list.keySet()) {
            sbSQL.setLength(0);
            sbSQL.append("\nALTER TABLE ONLY ");
            sbSQL.append(table2.getName());
            sbSQL.append(" ALTER COLUMN");
            sbSQL.append(colName);
            sbSQL.append(" SET STATISTICS ");
            sbSQL.append(list.get(colName).intValue());
            sbSQL.append(" ;");
            System.out.println(sbSQL.toString());
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
            if (!table1.getColumns().containsKey(column.getName())) {
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
            if (!table2.getColumns().containsKey(column.getName())) {
                commands.add("\tDROP COLUMN " + column.getName());
            }
        }
    }

    /**
     * Adds commands for modification of columns to the list of
     * commands.
     *
     * @param commands list of commands
     * @param table1 original table
     * @param table2 new table
     */
    private static void addModifyTableColumns(
        final List<String> commands,
        final PgTable table1,
        final PgTable table2) {
        for (PgColumn column : table2.getColumns().values()) {
            if (!table1.getColumns().containsKey(column.getName())) {
                continue;
            }

            final PgColumn column1 = table1.getColumn(column.getName());

            if (!column1.getType().contentEquals(column.getType())) {
                commands.add(
                        "\tALTER COLUMN " + column.getName() + " TYPE "
                        + column.getType());
            }

            final String default1 =
                (column1.getDefaultValue() == null) ? ""
                                                    : column1.getDefaultValue();
            final String default2 =
                (column.getDefaultValue() == null) ? "" : column.getDefaultValue();

            if (!default1.contentEquals(default2)) {
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

            if (!constraint.contentEquals(constraint1)) {
                System.out.println(
                        "\nMODIFIED CONSTRAINT ON COLUMN " + column.getName()
                        + " IN TABLE " + table2.getName());
                System.out.println("ORIGINAL: " + constraint1);
                System.out.println("NEW: " + constraint);
            }
        }
    }

    /**
     * Outputs commands for creation of new tables.
     *
     * @param schema1 original schema
     * @param schema2 new schema
     */
    private static void createTables(
        final PgSchema schema1,
        final PgSchema schema2) {
        for (PgTable table : schema2.getTables().values()) {
            if (!schema1.getTables().containsKey(table.getName())) {
                System.out.println("\n" + table.getTableSQL());
            }
        }
    }

    /**
     * Outputs commands for dropping tables.
     *
     * @param schema1 original schema
     * @param schema2 new schema
     */
    private static void dropTables(
        final PgSchema schema1,
        final PgSchema schema2) {
        for (PgTable table : schema1.getTables().values()) {
            if (!schema2.getTables().containsKey(table.getName())) {
                System.out.println("\nDROP TABLE " + table.getName() + ";");
            }
        }
    }

    /**
     * Outputs commands for addition, removal and modifications of
     * table fields.
     *
     * @param table1 original table
     * @param table2 new table
     */
    private static void updateTableFields(
        final PgTable table1,
        final PgTable table2) {
        final List<String> commands = new ArrayList<String>();
        addDropTableColumns(commands, table1, table2);
        addCreateTableColumns(commands, table1, table2);
        addModifyTableColumns(commands, table1, table2);

        if (commands.size() > 0) {
            System.out.println("\nALTER TABLE " + table2.getName());

            for (int i = 0; i < commands.size(); i++) {
                System.out.print(commands.get(i));
                System.out.println(((i + 1) < commands.size()) ? "," : ";");
            }
        }
    }
}
