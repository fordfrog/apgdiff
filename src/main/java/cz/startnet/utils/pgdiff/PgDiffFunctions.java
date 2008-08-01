/*
 * $Id$
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgFunction;
import cz.startnet.utils.pgdiff.schema.PgSchema;

import java.io.PrintWriter;

/**
 * Diffs functions.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgDiffFunctions {

    /**
     * Creates a new instance of PgDiffFunctions.
     */
    private PgDiffFunctions() {
        super();
    }

    /**
     * Outputs commands for differences in functions.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void diffFunctions(
        final PrintWriter writer,
        final PgDiffArguments arguments,
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        // Drop functions that exist no more
        if (oldSchema != null) {
            for (PgFunction oldFunction : oldSchema.getFunctions()) {
                if (!newSchema.containsFunction(oldFunction.getDeclaration())) {
                    writer.println();
                    writer.println(oldFunction.getDropSQL());
                }
            }
        }

        // Add new functions and replace modified functions
        for (PgFunction newFunction : newSchema.getFunctions()) {
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
}
