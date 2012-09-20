/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgSequence;
import java.io.PrintWriter;

/**
 * Diffs sequences.
 *
 * @author fordfrog
 */
public class PgDiffSequences {

    /**
     * Outputs statements for creation of new sequences.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void createSequences(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        // Add new sequences
        for (final PgSequence sequence : newSchema.getSequences()) {
            if (oldSchema == null
                    || !oldSchema.containsSequence(sequence.getName())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(sequence.getCreationSQL());
            }
        }
    }

    /**
     * Outputs statements for altering of new sequences.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void alterCreatedSequences(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        // Alter created sequences
        for (final PgSequence sequence : newSchema.getSequences()) {
            if ((oldSchema == null
                    || !oldSchema.containsSequence(sequence.getName()))
                    && sequence.getOwnedBy() != null
                    && !sequence.getOwnedBy().isEmpty()) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(sequence.getOwnedBySQL());
            }
        }
    }

    /**
     * Outputs statements for dropping of sequences that do not exist anymore.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void dropSequences(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        if (oldSchema == null) {
            return;
        }

        // Drop sequences that do not exist in new schema
        for (final PgSequence sequence : oldSchema.getSequences()) {
            if (!newSchema.containsSequence(sequence.getName())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(sequence.getDropSQL());
            }
        }
    }

    /**
     * Outputs statement for modified sequences.
     *
     * @param writer           writer the output should be written to
     * @param arguments        object containing arguments settings
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void alterSequences(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema, final SearchPathHelper searchPathHelper) {
        if (oldSchema == null) {
            return;
        }

        final StringBuilder sbSQL = new StringBuilder(100);

        for (final PgSequence newSequence : newSchema.getSequences()) {
            final PgSequence oldSequence =
                    oldSchema.getSequence(newSequence.getName());

            if (oldSequence == null) {
                continue;
            }

            sbSQL.setLength(0);

            final String oldIncrement = oldSequence.getIncrement();
            final String newIncrement = newSequence.getIncrement();

            if (newIncrement != null
                    && !newIncrement.equals(oldIncrement)) {
                sbSQL.append("\n\tINCREMENT BY ");
                sbSQL.append(newIncrement);
            }

            final String oldMinValue = oldSequence.getMinValue();
            final String newMinValue = newSequence.getMinValue();

            if (newMinValue == null && oldMinValue != null) {
                sbSQL.append("\n\tNO MINVALUE");
            } else if (newMinValue != null
                    && !newMinValue.equals(oldMinValue)) {
                sbSQL.append("\n\tMINVALUE ");
                sbSQL.append(newMinValue);
            }

            final String oldMaxValue = oldSequence.getMaxValue();
            final String newMaxValue = newSequence.getMaxValue();

            if (newMaxValue == null && oldMaxValue != null) {
                sbSQL.append("\n\tNO MAXVALUE");
            } else if (newMaxValue != null
                    && !newMaxValue.equals(oldMaxValue)) {
                sbSQL.append("\n\tMAXVALUE ");
                sbSQL.append(newMaxValue);
            }

            if (!arguments.isIgnoreStartWith()) {
                final String oldStart = oldSequence.getStartWith();
                final String newStart = newSequence.getStartWith();

                if (newStart != null && !newStart.equals(oldStart)) {
                    sbSQL.append("\n\tRESTART WITH ");
                    sbSQL.append(newStart);
                }
            }

            final String oldCache = oldSequence.getCache();
            final String newCache = newSequence.getCache();

            if (newCache != null && !newCache.equals(oldCache)) {
                sbSQL.append("\n\tCACHE ");
                sbSQL.append(newCache);
            }

            final boolean oldCycle = oldSequence.isCycle();
            final boolean newCycle = newSequence.isCycle();

            if (oldCycle && !newCycle) {
                sbSQL.append("\n\tNO CYCLE");
            } else if (!oldCycle && newCycle) {
                sbSQL.append("\n\tCYCLE");
            }

            final String oldOwnedBy = oldSequence.getOwnedBy();
            final String newOwnedBy = newSequence.getOwnedBy();

            if (newOwnedBy != null && !newOwnedBy.equals(oldOwnedBy)) {
                sbSQL.append("\n\tOWNED BY ");
                sbSQL.append(newOwnedBy);
            }

            if (sbSQL.length() > 0) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("ALTER SEQUENCE "
                        + PgDiffUtils.getQuotedName(newSequence.getName()));
                writer.print(sbSQL.toString());
                writer.println(';');
            }

            if (oldSequence.getComment() == null
                    && newSequence.getComment() != null
                    || oldSequence.getComment() != null
                    && newSequence.getComment() != null
                    && !oldSequence.getComment().equals(
                    newSequence.getComment())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("COMMENT ON SEQUENCE ");
                writer.print(PgDiffUtils.getQuotedName(newSequence.getName()));
                writer.print(" IS ");
                writer.print(newSequence.getComment());
                writer.println(';');
            } else if (oldSequence.getComment() != null
                    && newSequence.getComment() == null) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("COMMENT ON SEQUENCE ");
                writer.print(newSequence.getName());
                writer.println(" IS NULL;");
            }
        }
    }

    /**
     * Creates a new instance of PgDiffSequences.
     */
    private PgDiffSequences() {
    }
}
