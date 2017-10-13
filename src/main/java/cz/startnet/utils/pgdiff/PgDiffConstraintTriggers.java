/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgRelation;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgConstraintTrigger;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Diffs constraint triggers.
 *
 * @author nromero
 */
public class PgDiffConstraintTriggers {

    /**
     * Outputs statements for creation of new constraint triggers.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void createConstraintTriggers(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper
            ) {
        for (final PgRelation newRelation : newSchema.getRels()) {
            final PgRelation oldRelation;

            if (oldSchema == null) {
                oldRelation = null;
            } else {
                oldRelation = oldSchema.getRelation(newRelation.getName());
            }

            // Add new triggers
            for (final PgConstraintTrigger trigger : getNewConstraintTriggers(oldRelation, newRelation)) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(trigger.getCreationSQL());
            }
        }
    }

    /**
     * Outputs statements for dropping constraint triggers.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper    
     */
    public static void dropConstraintTriggers(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        for (final PgRelation newRelation : newSchema.getRels()) {
            final PgRelation oldRelation;

            if (oldSchema == null) {
                oldRelation = null;
            } else {
                oldRelation = oldSchema.getRelation(newRelation.getName());
            }

            // Drop triggers that no more exist or are modified
            for (final PgConstraintTrigger trigger :
                    getDropConstraintTriggers(oldRelation, newRelation)) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(trigger.getDropSQL());
            }
        }
    }

    /**
     * Returns list of constraint triggers that should be dropped.
     *
     * @param oldRelation original relation
     * @param newRelation new relation
     *
     * @return list of triggers that should be dropped
     */
    private static List<PgConstraintTrigger> getDropConstraintTriggers(final PgRelation oldRelation,
            final PgRelation newRelation) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgConstraintTrigger> list = new ArrayList<PgConstraintTrigger>();

        if (newRelation != null && oldRelation != null) {
            final List<PgConstraintTrigger> newTriggers = newRelation.getConstraintTriggers();

            for (final PgConstraintTrigger oldTrigger : oldRelation.getConstraintTriggers()) {
                if (!newTriggers.contains(oldTrigger)) {
                    list.add(oldTrigger);
                }
            }
        }

        return list;
    }

    /**
     * Returns list of constraint triggers that should be added.
     *
     * @param oldRelation original relation
     * @param newRelation new relation
     *
     * @return list of constraint triggers that should be added
     */
    private static List<PgConstraintTrigger> getNewConstraintTriggers(final PgRelation oldRelation,
            final PgRelation newRelation) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgConstraintTrigger> list = new ArrayList<PgConstraintTrigger>();

        if (newRelation != null) {
            if (oldRelation == null) {
                list.addAll(newRelation.getConstraintTriggers());
            } else {
                for (final PgConstraintTrigger newTrigger : newRelation.getConstraintTriggers()) {
                    if (!oldRelation.getConstraintTriggers().contains(newTrigger)) {
                        list.add(newTrigger);
                    }
                }
            }
        }

        return list;
    }

    /**
     * Outputs statements for constraint trigger comments that have changed.
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

        for (PgRelation oldRelation : oldSchema.getRels()) {
            final PgRelation newRelation = newSchema.getRelation(oldRelation.getName());

            if (newRelation == null) {
                continue;
            }

            for (final PgConstraintTrigger oldTrigger : oldRelation.getConstraintTriggers()) {
                final PgConstraintTrigger newTrigger =
                        newRelation.getConstraintTrigger(oldTrigger.getName());

                if (newTrigger == null) {
                    continue;
                }

                if (oldTrigger.getComment() == null
                        && newTrigger.getComment() != null
                        || oldTrigger.getComment() != null
                        && newTrigger.getComment() != null
                        && !oldTrigger.getComment().equals(
                        newTrigger.getComment())) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.print("COMMENT ON TRIGGER ");
                    writer.print(
                            PgDiffUtils.getQuotedName(newTrigger.getName()));
                    writer.print(" ON ");
                    writer.print(PgDiffUtils.getQuotedName(
                            newTrigger.getRelationName()));
                    writer.print(" IS ");
                    writer.print(newTrigger.getComment());
                    writer.println(';');
                } else if (oldTrigger.getComment() != null
                        && newTrigger.getComment() == null) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.print("COMMENT ON TRIGGER ");
                    writer.print(
                            PgDiffUtils.getQuotedName(newTrigger.getName()));
                    writer.print(" ON ");
                    writer.print(PgDiffUtils.getQuotedName(
                            newTrigger.getRelationName()));
                    writer.println(" IS NULL;");
                }
            }
        }
    }

    /**
     * Creates a new instance of PgDiffTriggers.
     */
    private PgDiffConstraintTriggers() {
    }
}
