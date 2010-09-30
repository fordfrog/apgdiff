package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgView;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * Diffs views.
 *
 * @author fordfrog
 */
public class PgDiffViews {

    /**
     * Creates a new instance of PgDiffViews.
     */
    private PgDiffViews() {
    }

    /**
     * Outputs commands for creation of views.
     *
     * @param writer writer the output should be written to
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void createViews(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema) {
        for (final PgView newView : newSchema.getViews()) {
            if (oldSchema == null
                    || !oldSchema.containsView(newView.getName())
                    || isViewModified(
                    oldSchema.getView(newView.getName()), newView)) {
                writer.println();
                writer.println(newView.getCreationSQL());
            }
        }
    }

    /**
     * Outputs commands for dropping views.
     *
     * @param writer writer the output should be written to
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void dropViews(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema) {
        if (oldSchema != null) {
            for (final PgView oldView : oldSchema.getViews()) {
                final PgView newView = newSchema.getView(oldView.getName());

                if (newView == null || isViewModified(oldView, newView)) {
                    writer.println();
                    writer.println(oldView.getDropSQL());
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
    private static boolean isViewModified(final PgView oldView,
            final PgView newView) {
        final String[] oldViewColumnNames;

        if (oldView.getColumnNames() == null
                || oldView.getColumnNames().isEmpty()) {
            oldViewColumnNames = null;
        } else {
            oldViewColumnNames = oldView.getColumnNames().toArray(
                    new String[oldView.getColumnNames().size()]);
        }

        final String[] newViewColumnNames;

        if (newView.getColumnNames() == null
                || newView.getColumnNames().isEmpty()) {
            newViewColumnNames = null;
        } else {
            newViewColumnNames = newView.getColumnNames().toArray(
                    new String[newView.getColumnNames().size()]);
        }

        if (oldViewColumnNames == null && newViewColumnNames == null) {
            return !oldView.getQuery().trim().equals(newView.getQuery().trim());
        } else {
            return !Arrays.equals(oldViewColumnNames, newViewColumnNames);
        }
    }

    /**
     * Outputs commands for altering view default values.
     *
     * @param writer writer
     * @param oldSchema old schema
     * @param newSchema new schema
     */
    public static void alterViews(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema) {
        if (oldSchema != null) {
            for (final PgView oldView : oldSchema.getViews()) {
                final PgView newView = newSchema.getView(oldView.getName());

                if (oldView != null && newView != null) {
                    diffDefaultValues(writer, oldView, newView);
                }
            }
        }
    }

    /**
     * Diffs default values in views.
     *
     * @param writer writer
     * @param oldView old view
     * @param newView new view
     */
    private static void diffDefaultValues(final PrintWriter writer,
            final PgView oldView, final PgView newView) {
        final List<PgView.DefaultValue> oldValues =
                oldView.getDefaultValues();
        final List<PgView.DefaultValue> newValues =
                newView.getDefaultValues();

        // modify defaults that are in old view
        for (final PgView.DefaultValue oldValue : oldValues) {
            boolean found = false;

            for (final PgView.DefaultValue newValue : newValues) {
                if (oldValue.getColumnName().equals(newValue.getColumnName())
                        && !oldValue.getDefaultValue().equals(
                        newValue.getDefaultValue())) {
                    found = true;

                    writer.println();
                    writer.print("ALTER VIEW ");
                    writer.print(PgDiffUtils.getQuotedName(newView.getName()));
                    writer.print(" ALTER COLUMN ");
                    writer.print(PgDiffUtils.getQuotedName(
                            newValue.getColumnName()));
                    writer.print(" SET DEFAULT ");
                    writer.print(newValue.getDefaultValue());
                    writer.println(';');

                    break;
                }
            }

            if (!found) {
                writer.println();
                writer.print("ALTER VIEW ");
                writer.print(PgDiffUtils.getQuotedName(newView.getName()));
                writer.print(" ALTER COLUMN ");
                writer.print(PgDiffUtils.getQuotedName(
                        oldValue.getColumnName()));
                writer.print(" DROP DEFAULT;");
            }
        }

        // add new defaults
        for (final PgView.DefaultValue newValue : newValues) {
            boolean found = false;

            for (final PgView.DefaultValue oldValue : oldValues) {
                if (newValue.getColumnName().equals(oldValue.getColumnName())) {
                    found = true;
                    break;
                }
            }

            if (found) {
                continue;
            }

            writer.println();
            writer.print("ALTER VIEW ");
            writer.print(PgDiffUtils.getQuotedName(newView.getName()));
            writer.print(" ALTER COLUMN ");
            writer.print(PgDiffUtils.getQuotedName(newValue.getColumnName()));
            writer.print(" SET DEFAULT ");
            writer.print(newValue.getDefaultValue());
            writer.print(';');
        }
    }
}
