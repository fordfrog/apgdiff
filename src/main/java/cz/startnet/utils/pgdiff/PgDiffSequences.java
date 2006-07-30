/*
 * $CVSHeader$
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgSequence;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Diffs sequences.
 *
 * @author fordfrog
 * @version $CVSHeader$
 */
public class PgDiffSequences {
    /**
     * Creates a new instance of PgDiffSequences
     */
    private PgDiffSequences() {
        super();
    }

    /**
     * Outputs commands for differences in sequences.
     *
     * @param schema1 original schema
     * @param schema2 new schema
     */
    public static void diffSequences(
        final PgSchema schema1,
        final PgSchema schema2) {
        // Drop sequences that do not exist in new schema
        for (PgSequence sequence : getDropSequences(schema1, schema2)) {
            System.out.println("\nDROP SEQUENCE " + sequence.getName() + ";");
        }

        // Add new sequences
        for (PgSequence sequence : getNewSequences(schema1, schema2)) {
            System.out.println("\nCREATE SEQUENCE " + sequence.getName());
            System.out.println(sequence.getDefinition() + ";");
        }

        // Inform about modified sequences
        for (PgSequence sequence : getModifiedSequences(schema1, schema2)) {
            System.out.println("\nMODIFIED SEQUENCE " + sequence.getName());
            System.out.println(
                    "ORIGINAL: "
                    + schema1.getSequence(sequence.getName()).getDefinition());
            System.out.println("NEW: " + sequence.getDefinition());
        }
    }

    /**
     * Returns list of sequences that should be dropped.
     *
     * @param schema1 original schema
     * @param schema2 new schema
     *
     * @return list of sequences that should be dropped
     */
    private static List<PgSequence> getDropSequences(
        final PgSchema schema1,
        final PgSchema schema2) {
        final List<PgSequence> list = new ArrayList<PgSequence>();
        final Set<String> names2 = schema2.getSequences().keySet();

        for (final PgSequence sequence : schema1.getSequences().values()) {
            if (!names2.contains(sequence.getName())) {
                list.add(sequence);
            }
        }

        return list;
    }

    /**
     * Returns list of modified sequences.
     *
     * @param schema1 original schema
     * @param schema2 new schema
     *
     * @return list of modified sequences
     */
    private static List<PgSequence> getModifiedSequences(
        final PgSchema schema1,
        final PgSchema schema2) {
        final List<PgSequence> list = new ArrayList<PgSequence>();
        final Set<String> names1 = schema1.getSequences().keySet();

        for (final PgSequence sequence : schema2.getSequences().values()) {
            if (
                names1.contains(sequence.getName())
                    && !schema1.getSequence(sequence.getName()).getDefinition().contentEquals(
                            sequence.getDefinition())) {
                list.add(sequence);
            }
        }

        return list;
    }

    /**
     * Returns list of sequences that should be added.
     *
     * @param schema1 original table
     * @param schema2 new table
     *
     * @return list of sequences that should be added
     */
    private static List<PgSequence> getNewSequences(
        final PgSchema schema1,
        final PgSchema schema2) {
        final List<PgSequence> list = new ArrayList<PgSequence>();
        final Set<String> names1 = schema1.getSequences().keySet();

        for (final PgSequence sequence : schema2.getSequences().values()) {
            if (!names1.contains(sequence.getName())) {
                list.add(sequence);
            }
        }

        return list;
    }
}
