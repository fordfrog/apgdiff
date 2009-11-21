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
     * Creates a new instance of PgDiffIndexes.
     */
    private PgDiffIndexes() {
        super();
    }

    /**
     * Outputs commands for creation of new indexes.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void createIndexes(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema) {
        for (final PgTable newTable : newSchema.getTables()) {
            final String newTableName = newTable.getName();
            final PgTable oldTable;

            if (oldSchema == null) {
                oldTable = null;
            } else {
                oldTable = oldSchema.getTable(newTableName);
            }

            // Add new indexes
            if (oldSchema == null) {
                for (PgIndex index : newTable.getIndexes()) {
                    writer.println();
                    writer.println(
                            index.getCreationSQL(arguments.isQuoteNames()));
                }
            } else {
                for (PgIndex index : getNewIndexes(
                        oldSchema.getTable(newTableName), newTable)) {
                    writer.println();
                    writer.println(
                            index.getCreationSQL(arguments.isQuoteNames()));
                }
            }
        }
    }

    /**
     * Outputs commands for dropping indexes that exist no more.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void dropIndexes(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema) {
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
                writer.println();
                writer.println(index.getDropSQL(arguments.isQuoteNames()));
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
    private static List<PgIndex> getDropIndexes(final PgTable oldTable,
            final PgTable newTable) {
        final List<PgIndex> list = new ArrayList<PgIndex>();

        if ((newTable != null) && (oldTable != null)) {
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
}
