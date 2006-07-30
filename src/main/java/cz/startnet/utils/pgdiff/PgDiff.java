/*
 * $CVSHeader$
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.loader.PgDumpLoader;
import cz.startnet.utils.pgdiff.schema.PgSchema;

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
     * @param file1 name of file containing dump of the original schema
     * @param file2 name of file containing dump of the new schema
     */
    public static void createDiff(final String file1, final String file2) {
        diffSchemas(
                PgDumpLoader.loadSchema(file1),
                PgDumpLoader.loadSchema(file2));
    }

    /**
     * Creates diff from comparison of two schemas.
     *
     * @param schema1 original schema
     * @param schema2 new schema
     */
    private static void diffSchemas(
        final PgSchema schema1,
        final PgSchema schema2) {
        PgDiffTables.diffTables(schema1, schema2);
        PgDiffSequences.diffSequences(schema1, schema2);
        PgDiffConstraints.diffConstraints(schema1, schema2, true);
        PgDiffConstraints.diffConstraints(schema1, schema2, false);
        PgDiffIndexes.diffIndexes(schema1, schema2);
    }
}
