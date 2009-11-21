package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgFunction;
import cz.startnet.utils.pgdiff.schema.PgSchema;

import java.io.PrintWriter;

/**
 * Diffs functions.
 *
 * @author fordfrog
 */
public class PgDiffFunctions {

    /**
     * Creates a new instance of PgDiffFunctions.
     */
    private PgDiffFunctions() {
        super();
    }

    /**
     * Outputs commands for new or modified functions.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void createFunctions(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema) {
        // Add new functions and replace modified functions
        for (final PgFunction newFunction : newSchema.getFunctions()) {
            final PgFunction oldFunction;

            if (oldSchema == null) {
                oldFunction = null;
            } else {
                oldFunction = oldSchema.getFunction(
                        newFunction.getDeclaration());
            }

            if ((oldFunction == null) || !newFunction.equals(oldFunction,
                    arguments.isIgnoreFunctionWhitespace())) {
                writer.println();
                writer.println(newFunction.getCreationSQL());
            }
        }
    }

    /**
     * Outputs commands for dropping of functions that exist no more.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void dropFunctions(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema) {
        // Drop functions that exist no more
        if (oldSchema != null) {
            for (final PgFunction oldFunction : oldSchema.getFunctions()) {
                if (!newSchema.containsFunction(oldFunction.getDeclaration())) {
                    writer.println();
                    writer.println(oldFunction.getDropSQL());
                }
            }
        }
    }
}
