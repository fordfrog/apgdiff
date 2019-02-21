/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgProcedure;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import java.io.PrintWriter;

/**
 * Diffs procedures.
 *
 * @author jalissonmello
 */
public class PgDiffProcedures {

    /**
     * Outputs statements for new or modified procedures.
     *
     * @param writer           writer the output should be written to
     * @param arguments        object containing arguments settings
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void createProducedures(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema, final SearchPathHelper searchPathHelper) {
        // Add new procedures and replace modified procedures
        for (final PgProcedure newProcedure : newSchema.getProcedures()) {
            final PgProcedure oldProcedure;

            if (oldSchema == null) {
                oldProcedure = null;
            } else {
                oldProcedure = oldSchema.getProcedure(newProcedure.getSignature());
            }

            if ((oldProcedure == null) || !newProcedure.equals(
                    oldProcedure, arguments.isIgnoreFunctionWhitespace())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(newProcedure.getCreationSQL());
            }
        }
    }

    /**
     * Outputs statements for dropping of procedures that exist no more.
     *
     * @param writer           writer the output should be written to
     * @param arguments        object containing arguments settings
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void dropPocedures(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema, final SearchPathHelper searchPathHelper) {
        if (oldSchema == null) {
            return;
        }

        // Drop procedures that exist no more
        for (final PgProcedure oldProcedure : oldSchema.getProcedures()) {
            if (!newSchema.containsProcedure(oldProcedure.getSignature())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(oldProcedure.getDropSQL());
            }
        }
    }

    /**
     * Outputs statements for procedure comments that have changed.
     *
     * @param writer           writer
     * @param oldSchema        old schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void alterComments(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        if (oldSchema == null) {
            return;
        }

        for (final PgProcedure oldprocedure : oldSchema.getProcedures()) {
            final PgProcedure newProcedure =
                    newSchema.getProcedure(oldprocedure.getSignature());

            if (newProcedure == null) {
                continue;
            }

            if (oldprocedure.getComment() == null
                    && newProcedure.getComment() != null
                    || oldprocedure.getComment() != null
                    && newProcedure.getComment() != null
                    && !oldprocedure.getComment().equals(
                    newProcedure.getComment())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("COMMENT ON PROCEDURE ");
                writer.print(PgDiffUtils.getQuotedName(newProcedure.getName()));
                writer.print('(');

                boolean addComma = false;

                for (final PgProcedure.Argument argument :
                        newProcedure.getArguments()) {
                    if (addComma) {
                        writer.print(", ");
                    } else {
                        addComma = true;
                    }

                    writer.print(argument.getDeclaration(false));
                }

                writer.print(") IS ");
                writer.print(newProcedure.getComment());
                writer.println(';');
            } else if (oldprocedure.getComment() != null
                    && newProcedure.getComment() == null) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("COMMENT ON PROCEDURE ");
                writer.print(PgDiffUtils.getQuotedName(newProcedure.getName()));
                writer.print('(');

                boolean addComma = false;

                for (final PgProcedure.Argument argument :
                        newProcedure.getArguments()) {
                    if (addComma) {
                        writer.print(", ");
                    } else {
                        addComma = true;
                    }

                    writer.print(argument.getDeclaration(false));
                }

                writer.println(") IS NULL;");
            }
        }
    }

    /**
     * Creates a new instance of PgDiffProcedures.
     */
    private PgDiffProcedures() {
    }
}
