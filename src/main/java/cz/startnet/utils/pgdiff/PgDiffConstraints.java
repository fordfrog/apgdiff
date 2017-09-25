/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgConstraint;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Diffs constraints.
 *
 * @author fordfrog
 */
public class PgDiffConstraints {

    /**
     * Outputs statements for creation of new constraints.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param primaryKey       determines whether primary keys should be
     *                         processed or any other constraints should be
     *                         processed
     * @param searchPathHelper search path helper
     */
    public static void createConstraints(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final boolean primaryKey, final SearchPathHelper searchPathHelper) {
        for (final PgTable newTable : newSchema.getTables()) {
            final PgTable oldTable;

            if (oldSchema == null) {
                oldTable = null;
            } else {
                oldTable = oldSchema.getTable(newTable.getName());
            }

            // Add new constraints
            for (final PgConstraint constraint :
                    getNewConstraints(oldTable, newTable, primaryKey)) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(constraint.getCreationSQL());
            }
        }
    }

    /**
     * Outputs statements for dropping non-existent or modified constraints.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param primaryKey       determines whether primary keys should be
     *                         processed or any other constraints should be
     *                         processed
     * @param searchPathHelper search path helper
     */
    public static void dropConstraints(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final boolean primaryKey, final SearchPathHelper searchPathHelper) {
        for (final PgTable newTable : newSchema.getTables()) {
            final PgTable oldTable;

            if (oldSchema == null) {
                oldTable = null;
            } else {
                oldTable = oldSchema.getTable(newTable.getName());
            }

            // Drop constraints that no more exist or are modified
            for (final PgConstraint constraint :
                    getDropConstraints(oldTable, newTable, primaryKey)) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(constraint.getDropSQL());
            }
        }
    }

    /**
     * Returns list of constraints that should be dropped.
     *
     * @param oldTable   original table or null
     * @param newTable   new table or null
     * @param primaryKey determines whether primary keys should be processed or
     *                   any other constraints should be processed
     *
     * @return list of constraints that should be dropped
     *
     * @todo Constraints that are depending on a removed field should not be
     * added to drop because they are already removed.
     */
    private static List<PgConstraint> getDropConstraints(final PgTable oldTable,
            final PgTable newTable, final boolean primaryKey) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgConstraint> list = new ArrayList<PgConstraint>();

        if (newTable != null && oldTable != null) {
            for (final PgConstraint constraint : oldTable.getConstraints()) {
                if (constraint.isPrimaryKeyConstraint() == primaryKey
                        && (!newTable.containsConstraint(constraint.getName())
                        || !newTable.getConstraint(constraint.getName()).equals(
                        constraint))) {
                    list.add(constraint);
                }
            }
        }

        return list;
    }

    /**
     * Returns list of constraints that should be added.
     *
     * @param oldTable   original table
     * @param newTable   new table
     * @param primaryKey determines whether primary keys should be processed or
     *                   any other constraints should be processed
     *
     * @return list of constraints that should be added
     */
    private static List<PgConstraint> getNewConstraints(final PgTable oldTable,
            final PgTable newTable, final boolean primaryKey) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgConstraint> list = new ArrayList<PgConstraint>();

        if (newTable != null) {
            if (oldTable == null) {
                for (final PgConstraint constraint :
                        newTable.getConstraints()) {
                    if (constraint.isPrimaryKeyConstraint() == primaryKey) {
                        list.add(constraint);
                    }
                }
            } else {
                for (final PgConstraint constraint :
                        newTable.getConstraints()) {
                    if ((constraint.isPrimaryKeyConstraint() == primaryKey)
                            && (!oldTable.containsConstraint(
                            constraint.getName())
                            || !oldTable.getConstraint(constraint.getName()).
                            equals(constraint))) {
                        list.add(constraint);
                    }
                }
            }
        }

        return list;
    }

    /**
     * Outputs statements for constraint comments that have changed.
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

        for (PgTable oldTable : oldSchema.getTables()) {
            final PgTable newTable = newSchema.getTable(oldTable.getName());

            if (newTable == null) {
                continue;
            }

            for (final PgConstraint oldConstraint : oldTable.getConstraints()) {
                final PgConstraint newConstraint =
                        newTable.getConstraint(oldConstraint.getName());

                if (newConstraint == null) {
                    continue;
                }

                if (oldConstraint.getComment() == null
                        && newConstraint.getComment() != null
                        || oldConstraint.getComment() != null
                        && newConstraint.getComment() != null
                        && !oldConstraint.getComment().equals(
                        newConstraint.getComment())) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.print("COMMENT ON ");

                    if (newConstraint.isPrimaryKeyConstraint()) {
                        writer.print("INDEX ");
                        writer.print(PgDiffUtils.getQuotedName(
                                newConstraint.getName()));
                    } else {
                        writer.print("CONSTRAINT ");
                        writer.print(PgDiffUtils.getQuotedName(
                                newConstraint.getName()));
                        writer.print(" ON ");
                        writer.print(PgDiffUtils.getQuotedName(
                                newConstraint.getTableName()));
                    }

                    writer.print(" IS ");
                    writer.print(newConstraint.getComment());
                    writer.println(';');
                } else if (oldConstraint.getComment() != null
                        && newConstraint.getComment() == null) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.print("COMMENT ON ");

                    if (newConstraint.isPrimaryKeyConstraint()) {
                        writer.print("INDEX ");
                        writer.print(PgDiffUtils.getQuotedName(
                                newConstraint.getName()));
                    } else {
                        writer.print("CONSTRAINT ");
                        writer.print(PgDiffUtils.getQuotedName(
                                newConstraint.getName()));
                        writer.print(" ON ");
                        writer.print(PgDiffUtils.getQuotedName(
                                newConstraint.getTableName()));
                    }

                    writer.println(" IS NULL;");
                }
            }
        }
    }

    /**
     * Creates a new instance of PgDiffConstraints.
     */
    private PgDiffConstraints() {
    }
}
