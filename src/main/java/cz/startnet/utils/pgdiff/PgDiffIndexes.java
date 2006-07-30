/*
 * $CVSHeader$
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgIndex;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Diffs indexes.
 *
 * @author fordfrog
 * @version $CVSHeader$
 */
public class PgDiffIndexes {
    /**
     * Creates a new instance of PgDiffIndexes
     */
    private PgDiffIndexes() {
        super();
    }

    /**
     * Outputs commands for differences in indexes.
     *
     * @param schema1 original schema
     * @param schema2 new schema
     */
    public static void diffIndexes(
        final PgSchema schema1,
        final PgSchema schema2) {
        final Map<String, PgTable> tables1 = schema1.getTables();

        for (PgTable table2 : schema2.getTables().values()) {
            final String tableName2 = table2.getName();

            // Drop indexes that do not exist in new schema or are modified
            for (PgIndex index : getDropIndexes(
                        tables1.get(tableName2),
                        table2)) {
                System.out.println("\nDROP INDEX " + index.getName() + ";");
            }

            // Add new constraints
            for (PgIndex index : getNewIndexes(tables1.get(tableName2), table2)) {
                System.out.println(
                        "\nCREATE INDEX " + index.getName() + " ON "
                        + table2.getName() + " " + index.getDefinition() + ";");
            }
        }
    }

    /**
     * Returns list of indexes that should be dropped.
     *
     * @param table1 original table
     * @param table2 new table
     *
     * @return list of indexes that should be dropped
     *
     * @todo Indexes that are dependent on a removed field should not be added
     *       to drop because they are already removed.
     */
    private static List<PgIndex> getDropIndexes(
        final PgTable table1,
        final PgTable table2) {
        final List<PgIndex> list = new ArrayList<PgIndex>();

        if ((table2 != null) && (table1 != null)) {
            final Set<String> names2 = table2.getIndexes().keySet();

            for (final PgIndex index : table1.getIndexes().values()) {
                if (
                    !names2.contains(index.getName())
                        || !table2.getIndex(index.getName()).getDefinition().contentEquals(
                                index.getDefinition())) {
                    list.add(index);
                }
            }
        }

        return list;
    }

    /**
     * Returns list of indexes that should be added.
     *
     * @param table1 original table
     * @param table2 new table
     *
     * @return list of indexes that should be added
     */
    private static List<PgIndex> getNewIndexes(
        final PgTable table1,
        final PgTable table2) {
        final List<PgIndex> list = new ArrayList<PgIndex>();

        if (table2 != null) {
            if (table1 == null) {
                for (final PgIndex index : table2.getIndexes().values()) {
                    list.add(index);
                }
            } else {
                final Set<String> names1 = table1.getIndexes().keySet();

                for (final PgIndex index : table2.getIndexes().values()) {
                    if (
                        !names1.contains(index.getName())
                            || !table1.getIndex(index.getName()).getDefinition()
                                          .contentEquals(index.getDefinition())) {
                        list.add(index);
                    }
                }
            }
        }

        return list;
    }
}
