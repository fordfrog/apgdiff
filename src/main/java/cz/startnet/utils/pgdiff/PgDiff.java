/*
 * $Id$
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.loader.PgDumpLoader;
import cz.startnet.utils.pgdiff.schema.PgSchema;

import java.io.InputStream;
import java.io.PrintWriter;


/**
 * Creates diff of two schemas.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgDiff {
    /**
     * Creates a new instance of PgDiff.
     */
    private PgDiff() {
        super();
    }

    /**
     * Creates diff on the two schemas.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     */
    public static void createDiff(
        final PrintWriter writer,
        final PgDiffArguments arguments) {
        diffSchemas(
                writer,
                arguments,
                PgDumpLoader.loadSchema(arguments.getOldDumpFile()),
                PgDumpLoader.loadSchema(arguments.getNewDumpFile()));
    }

    /**
     * Creates diff on the two schemas.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldInputStream input stream of file containing dump of the
     *        original schema
     * @param newInputStream input stream of file containing dump of the new
     *        schema
     */
    public static void createDiff(
        final PrintWriter writer,
        final PgDiffArguments arguments,
        final InputStream oldInputStream,
        final InputStream newInputStream) {
        diffSchemas(
                writer,
                arguments,
                PgDumpLoader.loadSchema(oldInputStream),
                PgDumpLoader.loadSchema(newInputStream));
    }

    /**
     * Creates diff from comparison of two schemas.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    private static void diffSchemas(
        final PrintWriter writer,
        final PgDiffArguments arguments,
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        PgDiffFunctions.diffFunctions(writer, oldSchema, newSchema);
        PgDiffSequences.diffSequences(writer, arguments, oldSchema, newSchema);
        PgDiffTables.diffTables(writer, oldSchema, newSchema);
        PgDiffConstraints.diffConstraints(writer, oldSchema, newSchema, true);
        PgDiffConstraints.diffConstraints(writer, oldSchema, newSchema, false);
        PgDiffIndexes.diffIndexes(writer, oldSchema, newSchema);
        PgDiffTables.diffClusters(writer, oldSchema, newSchema);
        PgDiffTriggers.diffTriggers(writer, oldSchema, newSchema);
    }
}
