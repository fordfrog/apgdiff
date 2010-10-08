/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
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
 */
public class PgDiff {

    /**
     * Creates a new instance of PgDiff.
     */
    private PgDiff() {
    }

    /**
     * Creates diff on the two database schemas.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     */
    public static void createDiff(final PrintWriter writer,
            final PgDiffArguments arguments) {
        diffDatabaseSchemas(writer, arguments,
                PgDumpLoader.loadDatabaseSchema(arguments.getOldDumpFile(),
                arguments.getInCharsetName(),
                arguments.isOutputIgnoredStatements()),
                PgDumpLoader.loadDatabaseSchema(arguments.getNewDumpFile(),
                arguments.getInCharsetName(),
                arguments.isOutputIgnoredStatements()));
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
    public static void createDiff(final PrintWriter writer,
            final PgDiffArguments arguments, final InputStream oldInputStream,
            final InputStream newInputStream) {
        diffDatabaseSchemas(writer, arguments,
                PgDumpLoader.loadDatabaseSchema(oldInputStream,
                arguments.getInCharsetName(),
                arguments.isOutputIgnoredStatements()),
                PgDumpLoader.loadDatabaseSchema(newInputStream,
                arguments.getInCharsetName(),
                arguments.isOutputIgnoredStatements()));
    }

    /**
     * Creates new schemas (not the objects inside the schemas).
     *
     * @param writer writer the output should be written to
     * @param oldDatabase original database schema
     * @param newDatabase new database schema
     */
    private static void createNewSchemas(final PrintWriter writer,
            final PgDatabase oldDatabase, final PgDatabase newDatabase) {
        for (final PgSchema newSchema : newDatabase.getSchemas()) {
            if (oldDatabase.getSchema(newSchema.getName()) == null) {
                writer.println();
                writer.println(newSchema.getCreationSQL());
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
    private static void diffDatabaseSchemas(final PrintWriter writer,
            final PgDiffArguments arguments, final PgDatabase oldDatabase,
            final PgDatabase newDatabase) {
        if (arguments.isAddTransaction()) {
            writer.println("START TRANSACTION;");
        }

        if (oldDatabase.getComment() == null
                && newDatabase.getComment() != null
                || oldDatabase.getComment() != null
                && newDatabase.getComment() != null
                && !oldDatabase.getComment().equals(newDatabase.getComment())) {
            writer.println();
            writer.print("COMMENT ON DATABASE current_database() IS ");
            writer.print(newDatabase.getComment());
            writer.println(';');
        } else if (oldDatabase.getComment() != null
                && newDatabase.getComment() == null) {
            writer.println();
            writer.println("COMMENT ON DATABASE current_database() IS NULL;");
        }

        dropOldSchemas(writer, oldDatabase, newDatabase);
        createNewSchemas(writer, oldDatabase, newDatabase);
        updateSchemas(writer, arguments, oldDatabase, newDatabase);

        if (arguments.isAddTransaction()) {
            writer.println();
            writer.println("COMMIT TRANSACTION;");
        }

        if (arguments.isOutputIgnoredStatements()) {
            if (!oldDatabase.getIgnoredStatements().isEmpty()) {
                writer.println();
                writer.println("/* Original database ignored statements");

                for (final String statement :
                        oldDatabase.getIgnoredStatements()) {
                    writer.println();
                    writer.println(statement);
                }

                writer.println("*/");
            }

            if (!newDatabase.getIgnoredStatements().isEmpty()) {
                writer.println();
                writer.println("/* New database ignored statements");

                for (final String statement :
                        newDatabase.getIgnoredStatements()) {
                    writer.println();
                    writer.println(statement);
                }

                writer.println("*/");
            }
        }
    }

    /**
     * Drops old schemas that do not exist anymore.
     *
     * @param writer writer the output should be written to
     * @param oldDatabase original database schema
     * @param newDatabase new database schema
     */
    private static void dropOldSchemas(final PrintWriter writer,
            final PgDatabase oldDatabase, final PgDatabase newDatabase) {
        for (final PgSchema oldSchema : oldDatabase.getSchemas()) {
            if (newDatabase.getSchema(oldSchema.getName()) == null) {
                writer.println();
                writer.println("DROP SCHEMA "
                        + PgDiffUtils.getQuotedName(oldSchema.getName())
                        + " CASCADE;");
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
    private static void updateSchemas(final PrintWriter writer,
            final PgDiffArguments arguments, final PgDatabase oldDatabase,
            final PgDatabase newDatabase) {
        final boolean setSearchPath = newDatabase.getSchemas().size() > 1
                || !newDatabase.getSchemas().get(0).getName().equals("public");

        for (final PgSchema newSchema : newDatabase.getSchemas()) {
            if (setSearchPath) {
                writer.println();
                writer.println("SET search_path = "
                        + PgDiffUtils.getQuotedName(newSchema.getName(), true)
                        + ", pg_catalog;");
            }

            final PgSchema oldSchema =
                    oldDatabase.getSchema(newSchema.getName());

            if (oldSchema != null && newSchema != null) {
                if (oldSchema.getComment() == null
                        && newSchema.getComment() != null
                        || oldSchema.getComment() != null
                        && newSchema.getComment() != null
                        && !oldSchema.getComment().equals(
                        newSchema.getComment())) {
                    writer.println();
                    writer.print("COMMENT ON SCHEMA ");
                    writer.print(
                            PgDiffUtils.getQuotedName(newSchema.getName()));
                    writer.print(" IS ");
                    writer.print(newSchema.getComment());
                    writer.println(';');
                } else if (oldSchema.getComment() != null
                        && newSchema.getComment() == null) {
                    writer.println();
                    writer.print("COMMENT ON SCHEMA ");
                    writer.print(
                            PgDiffUtils.getQuotedName(newSchema.getName()));
                    writer.println(" IS NULL;");
                }
            }

            PgDiffTriggers.dropTriggers(writer, oldSchema, newSchema);
            PgDiffFunctions.dropFunctions(
                    writer, arguments, oldSchema, newSchema);
            PgDiffFunctions.createFunctions(
                    writer, arguments, oldSchema, newSchema);
            PgDiffViews.dropViews(writer, oldSchema, newSchema);
            PgDiffConstraints.dropConstraints(
                    writer, oldSchema, newSchema, true);
            PgDiffConstraints.dropConstraints(
                    writer, oldSchema, newSchema, false);
            PgDiffIndexes.dropIndexes(writer, oldSchema, newSchema);
            PgDiffTables.dropClusters(writer, oldSchema, newSchema);
            PgDiffTables.dropTables(writer, oldSchema, newSchema);
            PgDiffSequences.dropSequences(writer, oldSchema, newSchema);

            PgDiffSequences.createSequences(writer, oldSchema, newSchema);
            PgDiffSequences.alterSequences(
                    writer, arguments, oldSchema, newSchema);
            PgDiffTables.createTables(writer, oldSchema, newSchema);
            PgDiffTables.alterTables(writer, arguments, oldSchema, newSchema);
            PgDiffConstraints.createConstraints(
                    writer, oldSchema, newSchema, true);
            PgDiffConstraints.createConstraints(
                    writer, oldSchema, newSchema, false);
            PgDiffIndexes.createIndexes(writer, oldSchema, newSchema);
            PgDiffTables.createClusters(writer, oldSchema, newSchema);
            PgDiffTriggers.createTriggers(writer, oldSchema, newSchema);
            PgDiffViews.createViews(writer, oldSchema, newSchema);
            PgDiffViews.alterViews(writer, oldSchema, newSchema);

            PgDiffFunctions.alterComments(writer, oldSchema, newSchema);
            PgDiffConstraints.alterComments(writer, oldSchema, newSchema);
            PgDiffIndexes.alterComments(writer, oldSchema, newSchema);
            PgDiffTriggers.alterComments(writer, oldSchema, newSchema);
        }
    }
}
