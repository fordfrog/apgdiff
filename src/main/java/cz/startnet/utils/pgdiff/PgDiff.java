/*
 * $CVSHeader$
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.loader.PgDumpLoader;
import cz.startnet.utils.pgdiff.schema.PgSchema;

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
     * @param file1 name of file containing dump of the original schema
     * @param file2 name of file containing dump of the new schema
     */
    public static void createDiff(
        final PrintWriter writer,
        final String file1,
        final String file2) {
        diffSchemas(
                writer,
                PgDumpLoader.loadSchema(file1),
                PgDumpLoader.loadSchema(file2));
    }

    /**
     * Creates diff from comparison of two schemas.
     *
     * @param writer writer the output should be written to
     * @param schema1 original schema
     * @param schema2 new schema
     */
    private static void diffSchemas(
        final PrintWriter writer,
        final PgSchema schema1,
        final PgSchema schema2) {
        PgDiffTables.diffTables(writer, schema1, schema2);
        PgDiffSequences.diffSequences(writer, schema1, schema2);
        PgDiffConstraints.diffConstraints(writer, schema1, schema2, true);
        PgDiffConstraints.diffConstraints(writer, schema1, schema2, false);
        PgDiffIndexes.diffIndexes(writer, schema1, schema2);
    }
}
