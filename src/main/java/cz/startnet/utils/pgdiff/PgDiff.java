/*
 * $CVSHeader$
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
 * @version $CVSHeader$
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
     * @param oldFile name of file containing dump of the original schema
     * @param newFile name of file containing dump of the new schema
     */
    public static void createDiff(
        final PrintWriter writer,
        final String oldFile,
        final String newFile) {
        diffSchemas(
                writer,
                PgDumpLoader.loadSchema(oldFile),
                PgDumpLoader.loadSchema(newFile));
    }

    /**
     * Creates diff on the two schemas.
     *
     * @param writer writer the output should be written to
     * @param oldInputStream input stream of file containing dump of the
     *        original schema
     * @param newInputStream input stream of file containing dump of the new
     *        schema
     */
    public static void createDiff(
        final PrintWriter writer,
        final InputStream oldInputStream,
        final InputStream newInputStream) {
        diffSchemas(
                writer,
                PgDumpLoader.loadSchema(oldInputStream),
                PgDumpLoader.loadSchema(newInputStream));
    }

    /**
     * Creates diff from comparison of two schemas.
     *
     * @param writer writer the output should be written to
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    private static void diffSchemas(
        final PrintWriter writer,
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        PgDiffTables.diffTables(writer, oldSchema, newSchema);
        PgDiffSequences.diffSequences(writer, oldSchema, newSchema);
        PgDiffConstraints.diffConstraints(writer, oldSchema, newSchema, true);
        PgDiffConstraints.diffConstraints(writer, oldSchema, newSchema, false);
        PgDiffIndexes.diffIndexes(writer, oldSchema, newSchema);
        PgDiffTables.diffClusters(writer, oldSchema, newSchema);
    }
}
