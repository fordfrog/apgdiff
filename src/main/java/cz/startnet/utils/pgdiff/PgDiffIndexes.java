/*
 * $CVSHeader$
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgIndex;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;

import java.io.PrintWriter;

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
     * Creates a new instance of PgDiffIndexes.
     */
    private PgDiffIndexes() {
        super();
    }

    /**
     * Outputs commands for differences in indexes.
     *
     * @param writer writer the output should be written to
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void diffIndexes(
        final PrintWriter writer,
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        final Map<String, PgTable> oldTables = oldSchema.getTables();

        for (PgTable newTable : newSchema.getTables().values()) {
            final String newTableName = newTable.getName();

            // Drop indexes that do not exist in new schema or are modified
            for (PgIndex index : getDropIndexes(
                        oldTables.get(newTableName),
                        newTable)) {
                writer.println();
                writer.println("DROP INDEX " + index.getName() + ";");
            }

            // Add new constraints
            for (PgIndex index : getNewIndexes(
                        oldTables.get(newTableName),
                        newTable)) {
                writer.println();
                writer.println(
                        "CREATE INDEX " + index.getName() + " ON "
                        + newTable.getName() + " " + index.getDefinition()
                        + ";");
            }
        }
    }

    /**
     * Returns list of indexes that should be dropped.
     *
     * @param oldTable original table
     * @param newTable new table
     *
     * @return list of indexes that should be dropped
     *
     * @todo Indexes that are depending on a removed field should not be added
     *       to drop because they are already removed.
     */
    private static List<PgIndex> getDropIndexes(
        final PgTable oldTable,
        final PgTable newTable) {
        final List<PgIndex> list = new ArrayList<PgIndex>();

        if ((newTable != null) && (oldTable != null)) {
            final Set<String> newNames = newTable.getIndexes().keySet();

            for (final PgIndex index : oldTable.getIndexes().values()) {
                if (
                    !newNames.contains(index.getName())
                        || !newTable.getIndex(index.getName()).getDefinition().equals(
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
     * @param oldTable original table
     * @param newTable new table
     *
     * @return list of indexes that should be added
     */
    private static List<PgIndex> getNewIndexes(
        final PgTable oldTable,
        final PgTable newTable) {
        final List<PgIndex> list = new ArrayList<PgIndex>();

        if (newTable != null) {
            if (oldTable == null) {
                for (final PgIndex index : newTable.getIndexes().values()) {
                    list.add(index);
                }
            } else {
                final Set<String> oldNames = oldTable.getIndexes().keySet();

                for (final PgIndex index : newTable.getIndexes().values()) {
                    if (
                        !oldNames.contains(index.getName())
                            || !oldTable.getIndex(index.getName())
                                            .getDefinition().equals(
                                    index.getDefinition())) {
                        list.add(index);
                    }
                }
            }
        }

        return list;
    }
}
