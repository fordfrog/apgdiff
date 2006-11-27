/*
 * $Id$
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgSequence;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


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
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void diffSequences(
        final PrintWriter writer,
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        // Drop sequences that do not exist in new schema
        for (PgSequence sequence : getDropSequences(oldSchema, newSchema)) {
            writer.println();
            writer.println("DROP SEQUENCE " + sequence.getName() + ";");
        }

        // Add new sequences
        for (PgSequence sequence : getNewSequences(oldSchema, newSchema)) {
            writer.println();
            writer.println("CREATE SEQUENCE " + sequence.getName());
            writer.println(sequence.getDefinition() + ";");
        }

        // Inform about modified sequences
        for (PgSequence sequence : getModifiedSequences(oldSchema, newSchema)) {
            writer.println();
            writer.println("MODIFIED SEQUENCE " + sequence.getName());
            writer.println(
                    "ORIGINAL: "
                    + oldSchema.getSequence(sequence.getName()).getDefinition());
            writer.println("NEW: " + sequence.getDefinition());
        }
    }

    /**
     * Returns list of sequences that should be dropped.
     *
     * @param oldSchema original schema
     * @param newSchema new schema
     *
     * @return list of sequences that should be dropped
     */
    private static List<PgSequence> getDropSequences(
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        final List<PgSequence> list = new ArrayList<PgSequence>();
        final Set<String> newNames = newSchema.getSequences().keySet();

        for (final PgSequence sequence : oldSchema.getSequences().values()) {
            if (!newNames.contains(sequence.getName())) {
                list.add(sequence);
            }
        }

        return list;
    }

    /**
     * Returns list of modified sequences.
     *
     * @param oldSchema original schema
     * @param newSchema new schema
     *
     * @return list of modified sequences
     */
    private static List<PgSequence> getModifiedSequences(
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        final List<PgSequence> list = new ArrayList<PgSequence>();
        final Set<String> oldNames = oldSchema.getSequences().keySet();

        for (final PgSequence sequence : newSchema.getSequences().values()) {
            if (
                oldNames.contains(sequence.getName())
                    && !oldSchema.getSequence(sequence.getName()).getDefinition()
                                     .equals(sequence.getDefinition())) {
                list.add(sequence);
            }
        }

        return list;
    }

    /**
     * Returns list of sequences that should be added.
     *
     * @param oldSchema original table
     * @param newSchema new table
     *
     * @return list of sequences that should be added
     */
    private static List<PgSequence> getNewSequences(
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        final List<PgSequence> list = new ArrayList<PgSequence>();
        final Set<String> oldNames = oldSchema.getSequences().keySet();

        for (final PgSequence sequence : newSchema.getSequences().values()) {
            if (!oldNames.contains(sequence.getName())) {
                list.add(sequence);
            }
        }

        return list;
    }
}
