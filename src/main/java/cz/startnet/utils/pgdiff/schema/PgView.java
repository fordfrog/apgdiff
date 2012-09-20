/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores view information.
 *
 * @author fordfrog
 */
public class PgView {

    /**
     * List of column names.
     */
    private List<String> columnNames;
    /**
     * Name of the view.
     */
    private final String name;
    /**
     * SQL query of the view.
     */
    private String query;
    /**
     * List of optional column default values.
     */
    private final List<DefaultValue> defaultValues =
            new ArrayList<DefaultValue>(0);
    /**
     * List of optional column comments.
     */
    private final List<ColumnComment> columnComments =
            new ArrayList<ColumnComment>(0);
    /**
     * Comment.
     */
    private String comment;

    /**
     * Creates a new PgView object.
     *
     * @param name {@link #name}
     */
    public PgView(final String name) {
        this.name = name;
    }

    /**
     * Setter for {@link #columnNames}.
     *
     * @param columnNames {@link #columnNames}
     */
    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setColumnNames(final List<String> columnNames) {
        this.columnNames = columnNames;
    }

    /**
     * Getter for {@link #columnNames}. The list cannot be modified.
     *
     * @return {@link #columnNames}
     */
    public List<String> getColumnNames() {
        return Collections.unmodifiableList(columnNames);
    }

    /**
     * Getter for {@link #comment}.
     *
     * @return {@link #comment}
     */
    public String getComment() {
        return comment;
    }

    /**
     * Setter for {@link #comment}.
     *
     * @param comment {@link #comment}
     */
    public void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * Creates and returns SQL for creation of the view.
     *
     * @return created SQL statement
     */
    public String getCreationSQL() {
        final StringBuilder sbSQL = new StringBuilder(query.length() * 2);
        sbSQL.append("CREATE VIEW ");
        sbSQL.append(PgDiffUtils.getQuotedName(name));

        if (columnNames != null && !columnNames.isEmpty()) {
            sbSQL.append(" (");

            for (int i = 0; i < columnNames.size(); i++) {
                if (i > 0) {
                    sbSQL.append(", ");
                }

                sbSQL.append(PgDiffUtils.getQuotedName(columnNames.get(i)));
            }
            sbSQL.append(')');
        }

        sbSQL.append(" AS\n\t");
        sbSQL.append(query);
        sbSQL.append(';');

        for (final DefaultValue defaultValue : defaultValues) {
            sbSQL.append("\n\nALTER VIEW ");
            sbSQL.append(PgDiffUtils.getQuotedName(name));
            sbSQL.append(" ALTER COLUMN ");
            sbSQL.append(
                    PgDiffUtils.getQuotedName(defaultValue.getColumnName()));
            sbSQL.append(" SET DEFAULT ");
            sbSQL.append(defaultValue.getDefaultValue());
            sbSQL.append(';');
        }

        if (comment != null && !comment.isEmpty()) {
            sbSQL.append("\n\nCOMMENT ON VIEW ");
            sbSQL.append(PgDiffUtils.getQuotedName(name));
            sbSQL.append(" IS ");
            sbSQL.append(comment);
            sbSQL.append(';');
        }

        for (final ColumnComment columnComment : columnComments) {
            if (columnComment.getComment() != null
                    && !columnComment.getComment().isEmpty()) {
                sbSQL.append("\n\nCOMMENT ON COLUMN ");
                sbSQL.append(PgDiffUtils.getQuotedName(
                        columnComment.getColumnName()));
                sbSQL.append(" IS ");
                sbSQL.append(columnComment.getComment());
                sbSQL.append(';');
            }
        }

        return sbSQL.toString();
    }

    /**
     * Creates and returns SQL statement for dropping the view.
     *
     * @return created SQL statement
     */
    public String getDropSQL() {
        return "DROP VIEW " + PgDiffUtils.getQuotedName(getName()) + ";";
    }

    /**
     * Getter for {@link #name}.
     *
     * @return {@link #name}
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for {@link #query}.
     *
     * @param query {@link #query}
     */
    public void setQuery(final String query) {
        this.query = query;
    }

    /**
     * Getter for {@link #query}.
     *
     * @return {@link #query}
     */
    public String getQuery() {
        return query;
    }

    /**
     * Adds/replaces column default value specification.
     *
     * @param columnName   column name
     * @param defaultValue default value
     */
    public void addColumnDefaultValue(final String columnName,
            final String defaultValue) {
        removeColumnDefaultValue(columnName);
        defaultValues.add(new DefaultValue(columnName, defaultValue));
    }

    /**
     * Removes column default value if present.
     *
     * @param columnName column name
     */
    public void removeColumnDefaultValue(final String columnName) {
        for (final DefaultValue item : defaultValues) {
            if (item.getColumnName().equals(columnName)) {
                defaultValues.remove(item);
                return;
            }
        }
    }

    /**
     * Getter for {@link #defaultValues}.
     *
     * @return {@link #defaultValues}
     */
    public List<DefaultValue> getDefaultValues() {
        return Collections.unmodifiableList(defaultValues);
    }

    /**
     * Adds/replaces column comment.
     *
     * @param columnName column name
     * @param comment    comment
     */
    public void addColumnComment(final String columnName,
            final String comment) {
        removeColumnDefaultValue(columnName);
        columnComments.add(new ColumnComment(columnName, comment));
    }

    /**
     * Removes column comment if present.
     *
     * @param columnName column name
     */
    public void removeColumnComment(final String columnName) {
        for (final ColumnComment item : columnComments) {
            if (item.getColumnName().equals(columnName)) {
                columnComments.remove(item);
                return;
            }
        }
    }

    /**
     * Getter for {@link #columnComments}.
     *
     * @return {@link #columnComments}
     */
    public List<ColumnComment> getColumnComments() {
        return Collections.unmodifiableList(columnComments);
    }

    /**
     * Contains information about default value of column.
     */
    @SuppressWarnings("PublicInnerClass")
    public class DefaultValue {

        /**
         * Column name.
         */
        private final String columnName;
        /**
         * Default value.
         */
        private final String defaultValue;

        /**
         * Creates new instance of DefaultValue.
         *
         * @param columnName   {@link #columnName}
         * @param defaultValue {@link #defaultValue}
         */
        DefaultValue(final String columnName, final String defaultValue) {
            this.columnName = columnName;
            this.defaultValue = defaultValue;
        }

        /**
         * Getter for {@link #columnName}.
         *
         * @return {@link #columnName}
         */
        public String getColumnName() {
            return columnName;
        }

        /**
         * Getter for {@link #defaultValue}.
         *
         * @return {@link #defaultValue}
         */
        public String getDefaultValue() {
            return defaultValue;
        }
    }

    /**
     * Contains information about column comment.
     */
    @SuppressWarnings("PublicInnerClass")
    public class ColumnComment {

        /**
         * Column name.
         */
        private final String columnName;
        /**
         * Comment.
         */
        private final String comment;

        /**
         * Creates new instance of ColumnComment.
         *
         * @param columnName {@link #columnName}
         * @param comment    {@link #comment}
         */
        ColumnComment(final String columnName, final String comment) {
            this.columnName = columnName;
            this.comment = comment;
        }

        /**
         * Getter for {@link #columnName}.
         *
         * @return {@link #columnName}
         */
        public String getColumnName() {
            return columnName;
        }

        /**
         * Getter for {@link #comment}.
         *
         * @return {@link #comment}
         */
        public String getComment() {
            return comment;
        }
    }
}
