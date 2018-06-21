/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgIndex;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Diffs indexes.
 *
 * @author fordfrog
 */
public class PgDiffIndexes {

       /**
     * Outputs statements for creation of new indexes.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void createIndexes(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        for (final PgTable newTable : newSchema.getTables()) {
            final String newTableName = newTable.getName();

            // Add new indexes
            if (oldSchema == null) {
                for (PgIndex index : newTable.getIndexes()) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.println(index.getCreationSQL());
                }
            } else {
                for (PgIndex index : getNewIndexes(
                        oldSchema.getTable(newTableName), newTable)) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.println(index.getCreationSQL());
                }
            }
        }
}

    /**
     * Outputs statements for dropping indexes that exist no more.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void dropIndexes(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        for (final PgTable newTable : newSchema.getTables()) {
            final String newTableName = newTable.getName();
            final PgTable oldTable;

            if (oldSchema == null) {
                oldTable = null;
            } else {
                oldTable = oldSchema.getTable(newTableName);
            }

            // Drop indexes that do not exist in new schema or are modified
            for (final PgIndex index : getDropIndexes(oldTable, newTable)) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(index.getDropSQL());
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
     * to drop because they are already removed.
     */
    private static List<PgIndex> getDropIndexes(final PgTable oldTable,
            final PgTable newTable) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgIndex> list = new ArrayList<PgIndex>();

        if (newTable != null && oldTable != null) {
            for (final PgIndex index : oldTable.getIndexes()) {
                if (!newTable.containsIndex(index.getName())
                        || !newTable.getIndex(index.getName()).equals(index)) {
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
    private static List<PgIndex> getNewIndexes(final PgTable oldTable,
            final PgTable newTable) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgIndex> list = new ArrayList<PgIndex>();

        if (newTable != null) {
            if (oldTable == null) {
                for (final PgIndex index : newTable.getIndexes()) {
                    list.add(index);
                }
            } else {
                for (final PgIndex index : newTable.getIndexes()) {
                    if (!oldTable.containsIndex(index.getName())
                            || !oldTable.getIndex(index.getName()).
                            equals(index)) {
                        list.add(index);
                    }
                }
            }
        }

        return list;
    }

    /**
     * Outputs statements for index comments that have changed.
     *
     * @param writer           writer
     * @param oldSchema        old schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void alterComments(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        if (oldSchema == null) {
            return;
        }

        for (final PgIndex oldIndex : oldSchema.getIndexes()) {
            final PgIndex newIndex = newSchema.getIndex(oldIndex.getName());

            if (newIndex == null) {
                continue;
            }

            if (oldIndex.getComment() == null
                    && newIndex.getComment() != null
                    || oldIndex.getComment() != null
                    && newIndex.getComment() != null
                    && !oldIndex.getComment().equals(
                    newIndex.getComment())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("COMMENT ON INDEX ");
                writer.print(
                        PgDiffUtils.getQuotedName(newIndex.getName()));
                writer.print(" IS ");
                writer.print(newIndex.getComment());
                writer.println(';');
            } else if (oldIndex.getComment() != null
                    && newIndex.getComment() == null) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("COMMENT ON INDEX ");
                writer.print(
                        PgDiffUtils.getQuotedName(newIndex.getName()));
                writer.println(" IS NULL;");
            }
        }
    }

    /**
     * Creates a new instance of PgDiffIndexes.
     */
    private PgDiffIndexes() {
    }
}
