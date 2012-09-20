/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgView;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
                    new ArrayList<String>(newView.getColumnComments().size());

            for (final PgView.ColumnComment columnComment :
                    newView.getColumnComments()) {
                columnNames.add(columnComment.getColumnName());
            }

            for (final PgView.ColumnComment columnComment :
                    oldView.getColumnComments()) {
                if (!columnNames.contains(columnComment.getColumnName())) {
                    columnNames.add(columnComment.getColumnName());
                }
            }

            for (final String columnName : columnNames) {
                PgView.ColumnComment oldColumnComment = null;
                PgView.ColumnComment newColumnComment = null;

                for (final PgView.ColumnComment columnComment :
                        oldView.getColumnComments()) {
                    if (columnName.equals(columnComment.getColumnName())) {
                        oldColumnComment = columnComment;
                        break;
                    }
                }

                for (final PgView.ColumnComment columnComment :
                        newView.getColumnComments()) {
                    if (columnName.equals(columnComment.getColumnName())) {
                        newColumnComment = columnComment;
                        break;
                    }
                }

                if (oldColumnComment == null && newColumnComment != null
                        || oldColumnComment != null && newColumnComment != null
                        && !oldColumnComment.getComment().equals(
                        newColumnComment.getComment())) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.print("COMMENT ON COLUMN ");
                    writer.print(PgDiffUtils.getQuotedName(newView.getName()));
                    writer.print('.');
                    writer.print(PgDiffUtils.getQuotedName(
                            newColumnComment.getColumnName()));
                    writer.print(" IS ");
                    writer.print(newColumnComment.getComment());
                    writer.println(';');
                } else if (oldColumnComment != null
                        && newColumnComment == null) {
                    searchPathHelper.outputSearchPath(writer);
                    writer.println();
                    writer.print("COMMENT ON COLUMN ");
                    writer.print(PgDiffUtils.getQuotedName(newView.getName()));
                    writer.print('.');
                    writer.print(PgDiffUtils.getQuotedName(
                            oldColumnComment.getColumnName()));
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
        final List<PgView.DefaultValue> oldValues =
                oldView.getDefaultValues();
        final List<PgView.DefaultValue> newValues =
                newView.getDefaultValues();

        // modify defaults that are in old view
        for (final PgView.DefaultValue oldValue : oldValues) {
            boolean found = false;

            for (final PgView.DefaultValue newValue : newValues) {
                if (oldValue.getColumnName().equals(newValue.getColumnName())) {
                    found = true;

                    if (!oldValue.getDefaultValue().equals(
                            newValue.getDefaultValue())) {
                        searchPathHelper.outputSearchPath(writer);
                        writer.println();
                        writer.print("ALTER TABLE ");
                        writer.print(
                                PgDiffUtils.getQuotedName(newView.getName()));
                        writer.print(" ALTER COLUMN ");
                        writer.print(PgDiffUtils.getQuotedName(
                                newValue.getColumnName()));
                        writer.print(" SET DEFAULT ");
                        writer.print(newValue.getDefaultValue());
                        writer.println(';');
                    }

                    break;
                }
            }

            if (!found) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.print("ALTER TABLE ");
                writer.print(PgDiffUtils.getQuotedName(newView.getName()));
                writer.print(" ALTER COLUMN ");
                writer.print(PgDiffUtils.getQuotedName(
                        oldValue.getColumnName()));
                writer.println(" DROP DEFAULT;");
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

            searchPathHelper.outputSearchPath(writer);
            writer.println();
            writer.print("ALTER TABLE ");
            writer.print(PgDiffUtils.getQuotedName(newView.getName()));
            writer.print(" ALTER COLUMN ");
            writer.print(PgDiffUtils.getQuotedName(newValue.getColumnName()));
            writer.print(" SET DEFAULT ");
            writer.print(newValue.getDefaultValue());
            writer.println(';');
        }
    }

    /**
     * Creates a new instance of PgDiffViews.
     */
    private PgDiffViews() {
    }
}
