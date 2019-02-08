/**
 * Copyright 2018 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgRelation;
import cz.startnet.utils.pgdiff.schema.PgRule;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgType;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Diffs rules.
 *
 * @author jalissonmello
 */
public class PgDiffRules {    

    /**
     * Outputs statements for creation of new triggers.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void createRules(final PrintWriter writer,
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

            // Add new rules
            for (final PgRule rule : getNewRules(oldRelation, newRelation)) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(rule.getCreationSQL());
            }
        }
    }

    /**
     * Outputs statements for dropping rules.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void dropRules(final PrintWriter writer,
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

            // Drop rules that no more exist or are modified
            for (final PgRule rule :
                    
                    dropRules(oldRelation, newRelation)) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(rule.getDropSQL());
            }
        }
    }
    
    /**
     * Returns list of rules that should be dropped.
     *
     * @param oldRelation original relation
     * @param newRelation new relation
     *
     * @return list of rules that should be dropped
     */
    private static List<PgRule> dropRules(final PgRelation oldRelation,
            final PgRelation newRelation) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgRule> list = new ArrayList<PgRule>();

        if (newRelation != null && oldRelation != null) {
            final List<PgRule> newRules = newRelation.getRules();

            for (final PgRule oldRule : oldRelation.getRules()) {
                if (!newRules.contains(oldRule)) {
                    list.add(oldRule);
                }
            }
        }

        return list;
    }
    
    /**
     * Returns list of rules that should be added.
     *
     * @param oldRelation original relation
     * @param newRelation new relation
     *
     * @return list of rules that should be added
     */
    private static List<PgRule> getNewRules(final PgRelation oldRelation,
            final PgRelation newRelation) {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgRule> list = new ArrayList<PgRule>();

        if (newRelation != null) {
            if (oldRelation == null) {
                list.addAll(newRelation.getRules());
            } else {
                for (final PgRule newRule : newRelation.getRules()) {
                    if (!oldRelation.getRules().contains(newRule)) {
                        list.add(newRule);
                    }
                }
            }
        }

        return list;
    }

    
    /**
     * Creates a new instance of PgDiffRules.
     */
    private PgDiffRules() {
    }
}
