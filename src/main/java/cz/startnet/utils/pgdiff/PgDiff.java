/*
 * $Id$
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.loader.PgDumpLoader;
import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgSchema;

import java.io.InputStream;
import java.io.PrintWriter;

/**
 * Creates diff of two database schemas.
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
     * Creates diff on the two database schemas.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     */
    public static void createDiff(
        final PrintWriter writer,
        final PgDiffArguments arguments) {
        diffDatabaseSchemas(
            writer,
            arguments,
            PgDumpLoader.loadDatabaseSchema(arguments.getOldDumpFile(),
            arguments.getInCharsetName()),
            PgDumpLoader.loadDatabaseSchema(arguments.getNewDumpFile(),
            arguments.getInCharsetName()));
    }

    /**
     * Creates diff on the two database schemas.
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
        diffDatabaseSchemas(
            writer,
            arguments,
            PgDumpLoader.loadDatabaseSchema(oldInputStream, arguments.
            getInCharsetName()),
            PgDumpLoader.loadDatabaseSchema(newInputStream, arguments.
            getInCharsetName()));
    }

    /**
     * Creates new schemas (not the objects inside the schemas).
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldDatabase original database schema
     * @param newDatabase new database schema
     */
    private static void createNewSchemas(
        final PrintWriter writer,
        final PgDiffArguments arguments,
        final PgDatabase oldDatabase,
        final PgDatabase newDatabase) {
        for (PgSchema newSchema : newDatabase.getSchemas()) {
            if (oldDatabase.getSchema(newSchema.getName()) == null) {
                writer.println();
                writer.println(
                    newSchema.getCreationSQL(arguments.isQuoteNames()));
            }
        }
    }

    /**
     * Creates diff from comparison of two database schemas.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldDatabase original database schema
     * @param newDatabase new database schema
     */
    private static void diffDatabaseSchemas(
        final PrintWriter writer,
        final PgDiffArguments arguments,
        final PgDatabase oldDatabase,
        final PgDatabase newDatabase) {
        if (arguments.isAddTransaction()) {
            writer.println("START TRANSACTION;");
        }

        dropOldSchemas(writer, arguments, oldDatabase, newDatabase);
        createNewSchemas(writer, arguments, oldDatabase, newDatabase);
        updateSchemas(writer, arguments, oldDatabase, newDatabase);

        if (arguments.isAddTransaction()) {
            writer.println();
            writer.println("COMMIT TRANSACTION;");
        }
    }

    /**
     * Drops old schemas that do not exist anymore.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldDatabase original database schema
     * @param newDatabase new database schema
     */
    private static void dropOldSchemas(
        final PrintWriter writer,
        final PgDiffArguments arguments,
        final PgDatabase oldDatabase,
        final PgDatabase newDatabase) {
        for (PgSchema oldSchema : oldDatabase.getSchemas()) {
            if (newDatabase.getSchema(oldSchema.getName()) == null) {
                writer.println();
                writer.println(
                    "DROP SCHEMA " + PgDiffUtils.getQuotedName(
                    oldSchema.getName(),
                    arguments.isQuoteNames()) + " CASCADE;");
            }
        }
    }

    /**
     * Updates objects in schemas.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldDatabase original database schema
     * @param newDatabase new database schema
     */
    private static void updateSchemas(
        final PrintWriter writer,
        final PgDiffArguments arguments,
        final PgDatabase oldDatabase,
        final PgDatabase newDatabase) {
        final boolean setSearchPath =
            (newDatabase.getSchemas().size() > 1) || !newDatabase.getSchemas().
            get(0).getName().equals("public");

        for (PgSchema newSchema : newDatabase.getSchemas()) {
            if (setSearchPath) {
                writer.println();
                writer.println(
                    "SET search_path = " + PgDiffUtils.getQuotedName(
                    newSchema.getName(),
                    arguments.isQuoteNames()) + ", pg_catalog;");
            }

            final PgSchema oldSchema =
                oldDatabase.getSchema(newSchema.getName());
            PgDiffFunctions.diffFunctions(writer, arguments, oldSchema,
                newSchema);
            PgDiffViews.dropViews(writer, arguments, oldSchema, newSchema);
            PgDiffSequences.diffSequences(
                writer,
                arguments,
                oldSchema,
                newSchema);
            PgDiffTables.diffTables(writer, arguments, oldSchema, newSchema);
            PgDiffConstraints.diffConstraints(
                writer,
                arguments,
                oldSchema,
                newSchema,
                true);
            PgDiffConstraints.diffConstraints(
                writer,
                arguments,
                oldSchema,
                newSchema,
                false);
            PgDiffIndexes.diffIndexes(writer, arguments, oldSchema, newSchema);
            PgDiffTables.diffClusters(writer, arguments, oldSchema, newSchema);
            PgDiffTriggers.diffTriggers(
                writer,
                arguments,
                oldSchema,
                newSchema);
            PgDiffViews.createViews(writer, arguments, oldSchema, newSchema);
        }
    }
}
