/*
 * $Id$
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgView;

import java.io.PrintWriter;


/**
 * Diffs views.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgDiffViews {
    /**
     * Creates a new instance of PgDiffViews.
     */
    private PgDiffViews() {
        super();
    }

    /**
     * Outputs commands for creation of views.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void createViews(
        final PrintWriter writer,
        final PgDiffArguments arguments,
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        for (PgView newView : newSchema.getViews()) {
            if (
                (oldSchema == null)
                    || !oldSchema.containsView(newView.getName())
                    || isViewModified(
                            oldSchema.getView(newView.getName()),
                            newView)) {
                writer.println();
                writer.println(
                        newView.getCreationSQL(arguments.isQuoteNames()));
            }
        }
    }

    /**
     * Outputs commands for dropping views.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void dropViews(
        final PrintWriter writer,
        final PgDiffArguments arguments,
        final PgSchema oldSchema,
        final PgSchema newSchema) {
        if (oldSchema != null) {
            for (PgView oldView : oldSchema.getViews()) {
                final PgView newView = newSchema.getView(oldView.getName());

                if ((newView == null) || isViewModified(oldView, newView)) {
                    writer.println();
                    writer.println(
                            oldView.getDropSQL(arguments.isQuoteNames()));
                }
            }
        }
    }

    /**
     * Returns true if either column names or query of the view has
     * been modified.
     *
     * @param oldView old view
     * @param newView new view
     *
     * @return true if view has been modified, otherwise false
     */
    private static boolean isViewModified(
        final PgView oldView,
        final PgView newView) {
        final String oldViewColumnNames;

        if (oldView.getColumnNames() == null) {
            oldViewColumnNames = "";
        } else {
            oldViewColumnNames = oldView.getColumnNames();
        }

        final String newViewColumnNames;

        if (newView.getColumnNames() == null) {
            newViewColumnNames = "";
        } else {
            newViewColumnNames = newView.getColumnNames();
        }

        return (!oldViewColumnNames.equals(newViewColumnNames)
        || !oldView.getQuery().equals(newView.getQuery()));
    }
}
