/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgView;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Diffs views.
 *
 * @author fordfrog
 */
public class PgDiffViews {

    /**
     * Outputs statements for creation of views.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void createViews(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        for (final PgView newView : newSchema.getViews()) {
            if (oldSchema == null
                    || !oldSchema.containsView(newView.getName())
                    || isViewModified(
                    oldSchema.getView(newView.getName()), newView)) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(newView.getCreationSQL());
            }
        }
    }

    /**
     * Outputs statements for dropping views.
     *
     * @param writer           writer the output should be written to
     * @param oldSchema        original schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void dropViews(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        if (oldSchema == null) {
            return;
        }

        for (final PgView oldView : oldSchema.getViews()) {
            final PgView newView = newSchema.getView(oldView.getName());

            if (newView == null || isViewModified(oldView, newView)) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(oldView.getDropSQL());
            }
        }
    }

    /**
     * Returns true if either column names or query of the view has been
     * modified.
     *
     * @param oldView old view
     * @param newView new view
     *
     * @return true if view has been modified, otherwise false
     */
    private static boolean isViewModified(final PgView oldView,
            final PgView newView) {
        if (!oldView.getQuery().trim().equals(newView.getQuery().trim()))
            return true;

        if (oldView.isMaterialized() != newView.isMaterialized())
            return true;

        final List<String> oldViewColumnNames =
                oldView.getDeclaredColumnNames();
        final List<String> newViewColumnNames =
                newView.getDeclaredColumnNames();

        if (oldViewColumnNames != null && newViewColumnNames != null) {
            return !oldViewColumnNames.equals(newViewColumnNames);
        } else {
            // At least one of the two is null. Are both?
            return oldViewColumnNames != newViewColumnNames;
        }
    }

    /**
     * Outputs statements for altering view default values.
     *
     * @param writer           writer
     * @param oldSchema        old schema
     * @param newSchema        new schema
     * @param searchPathHelper search path helper
     */
    public static void alterViews(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        if (oldSchema == null) {
            return;
        }

        for (final PgView oldView : oldSchema.getViews()) {
            final PgView newView = newSchema.getView(oldView.getName());

            if (newView == null) {
                continue;
            }

            diffDefaultValues(writer, oldView, newView, searchPathHelper);

            if (oldView.getComment() == null
                    && newView.getComment() != null
                    || oldView.getComment() != null
                    && newView.getComment() != null
                    && !oldView.getComment().equals(
                    newView.getComment())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("COMMENT ON VIEW ");
                writer.print(
                        PgDiffUtils.getQuotedName(newView.getName()));
                writer.print(" IS ");
                writer.print(newView.getComment());
                writer.println(';');
            } else if (oldView.getComment() != null
                    && newView.getComment() == null) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("COMMENT ON VIEW ");
                writer.print(PgDiffUtils.getQuotedName(newView.getName()));
                writer.println(" IS NULL;");
            }

            final List<String> columnNames =
                    new ArrayList<String>(newView.getColumns().size());

            for (final PgColumn col : newView.getColumns()) {
                columnNames.add(col.getName());
            }

            for (final PgColumn col : oldView.getColumns()) {
                if (!columnNames.contains(col.getName())) {
                    columnNames.add(col.getName());
                }
            }

            for (final String columnName : columnNames) {
                String oldComment = null;
                String newComment = null;
                PgColumn oldCol = oldView.getColumn(columnName);
                PgColumn newCol = newView.getColumn(columnName);

                if (oldCol != null)
                    oldComment = oldCol.getComment();
                if (newCol != null)
                    newComment = newCol.getComment();

                if (oldComment == null && newComment != null
                        || oldComment != null && newComment != null
                        && !oldComment.equals(newComment)) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.print("COMMENT ON COLUMN ");
                    writer.print(PgDiffUtils.getQuotedName(newView.getName()));
                    writer.print('.');
                    writer.print(PgDiffUtils.getQuotedName(newCol.getName()));
                    writer.print(" IS ");
                    writer.print(newCol.getComment());
                    writer.println(';');
                } else if (oldComment != null
                        && newComment == null) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.print("COMMENT ON COLUMN ");
                    writer.print(PgDiffUtils.getQuotedName(newView.getName()));
                    writer.print('.');
                    writer.print(PgDiffUtils.getQuotedName(oldCol.getName()));
                    writer.println(" IS NULL;");
                }
            }
        }
    }

    /**
     * Diffs default values in views.
     *
     * @param writer           writer
     * @param oldView          old view
     * @param newView          new view
     * @param searchPathHelper search path helper
     */
    private static void diffDefaultValues(final PrintWriter writer,
            final PgView oldView, final PgView newView,
            final SearchPathHelper searchPathHelper) {

        // modify defaults that are in old view
        for (final PgColumn oldCol : oldView.getColumns()) {
            if (oldCol.getDefaultValue() == null)
                continue;

            PgColumn newCol = newView.getColumn(oldCol.getName());

            if (newCol != null && newCol.getDefaultValue() != null) {
                if (!oldCol.getDefaultValue().equals(
                        newCol.getDefaultValue())) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.print("ALTER TABLE ");
                    writer.print(
                            PgDiffUtils.getQuotedName(newView.getName()));
                    writer.print(" ALTER COLUMN ");
                    writer.print(PgDiffUtils.getQuotedName(newCol.getName()));
                    writer.print(" SET DEFAULT ");
                    writer.print(newCol.getDefaultValue());
                    writer.println(';');
                }
            } else {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("ALTER TABLE ");
                writer.print(PgDiffUtils.getQuotedName(newView.getName()));
                writer.print(" ALTER COLUMN ");
                writer.print(PgDiffUtils.getQuotedName(oldCol.getName()));
                writer.println(" DROP DEFAULT;");
            }
        }

        // add new defaults
        for (final PgColumn newCol : newView.getColumns()) {
            PgColumn oldCol = oldView.getColumn(newCol.getName());

            if ((oldCol != null && oldCol.getDefaultValue() != null)
                    || newCol.getDefaultValue() == null) {
                continue;
            }

            searchPathHelper.outputSearchPath(writer);
            writer.println();
            writer.print("ALTER TABLE ");
            writer.print(PgDiffUtils.getQuotedName(newView.getName()));
            writer.print(" ALTER COLUMN ");
            writer.print(PgDiffUtils.getQuotedName(newCol.getName()));
            writer.print(" SET DEFAULT ");
            writer.print(newCol.getDefaultValue());
            writer.println(';');
        }
    }

    /**
     * Creates a new instance of PgDiffViews.
     */
    private PgDiffViews() {
    }
}
