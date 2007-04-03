/*
 * $Id$
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgSequence;

import java.io.PrintWriter;


/**
 * Diffs sequences.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgDiffSequences {
    /**
     * Creates a new instance of PgDiffSequences.
     */
    private PgDiffSequences() {
        super();
    }

    /**
     * Outputs commands for differences in sequences.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void diffSequences(
        final PrintWriter writer,
        final PgDiffArguments arguments,
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        // Drop sequences that do not exist in new schema
        for (PgSequence sequence : oldSchema.getSequences()) {
            if (!newSchema.containsSequence(sequence.getName())) {
                writer.println();
                writer.println(sequence.getDropSQL());
            }
        }

        // Add new sequences
        for (PgSequence sequence : newSchema.getSequences()) {
            if (!oldSchema.containsSequence(sequence.getName())) {
                writer.println();
                writer.println(sequence.getCreationSQL());
            }
        }

        // Alter modified sequences
        addModifiedSequences(writer, arguments, oldSchema, newSchema);
    }

    /**
     * Returns list of modified sequences.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    private static void addModifiedSequences(
        final PrintWriter writer,
        final PgDiffArguments arguments,
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        final StringBuilder sbSQL = new StringBuilder();

        for (final PgSequence newSequence : newSchema.getSequences()) {
            if (oldSchema.containsSequence(newSequence.getName())) {
                final PgSequence oldSequence =
                    oldSchema.getSequence(newSequence.getName());
                sbSQL.setLength(0);

                final String oldIncrement = oldSequence.getIncrement();
                final String newIncrement = newSequence.getIncrement();

                if (
                    (newIncrement != null)
                        && !newIncrement.equals(oldIncrement)) {
                    sbSQL.append("\n\tINCREMENT BY ");
                    sbSQL.append(newIncrement);
                }

                final String oldMinValue = oldSequence.getMinValue();
                final String newMinValue = newSequence.getMinValue();

                if ((newMinValue == null) && (oldMinValue != null)) {
                    sbSQL.append("\n\tNO MINVALUE");
                } else if (
                    (newMinValue != null)
                        && !newMinValue.equals(oldMinValue)) {
                    sbSQL.append("\n\tMINVALUE ");
                    sbSQL.append(newMinValue);
                }

                final String oldMaxValue = oldSequence.getMaxValue();
                final String newMaxValue = newSequence.getMaxValue();

                if ((newMaxValue == null) && (oldMaxValue != null)) {
                    sbSQL.append("\n\tNO MAXVALUE");
                } else if (
                    (newMaxValue != null)
                        && !newMaxValue.equals(oldMaxValue)) {
                    sbSQL.append("\n\tMAXVALUE ");
                    sbSQL.append(newMaxValue);
                }

                if (!arguments.isIgnoreStartWith()) {
                    final String oldStart = oldSequence.getStartWith();
                    final String newStart = newSequence.getStartWith();

                    if ((newStart != null) && !newStart.equals(oldStart)) {
                        sbSQL.append("\n\tRESTART WITH ");
                        sbSQL.append(newStart);
                    }
                }

                final String oldCache = oldSequence.getCache();
                final String newCache = newSequence.getCache();

                if ((newCache != null) && !newCache.equals(oldCache)) {
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

                if (sbSQL.length() > 0) {
                    writer.println();
                    writer.print("ALTER SEQUENCE " + newSequence.getName());
                    writer.print(sbSQL.toString());
                    writer.println(';');
                }
            }
        }
    }
}
