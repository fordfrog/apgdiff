/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores view information.
 *
 * @author fordfrog
 */
public class PgView extends PgRelation {

    /**
     * Were column names explicitly declared as part of the view?
     */
    private boolean declareColumnNames = false;
    /**
     * Is this a MATERIALIZED view?
     */
    private boolean materialized;
    /**
     * SQL query of the view.
     */
    private String query;

    /**
     * Creates a new PgView object.
     *
     * @param name {@link #name}
     */
    public PgView(final String name) {
        setName(name);
    }

    /**
     * Sets the list of declared column names for the view.
     *
     * @param columnNames list of column names
     */
    public void setDeclaredColumnNames(final List<String> columnNames) {
        // Can only be set once for a view, before defaults/comments are set
        assert !declareColumnNames;
        assert columns.isEmpty();

        if (columnNames == null || columnNames.isEmpty())
            return;

        declareColumnNames = true;

        for (final String colName: columnNames) {
            addColumn(new PgColumn(colName));
        }
    }

    /**
     * Returns a list of column names if the names were declared along with the view, null otherwise.
     *
     * @return list of column names or null
     */
    public List<String> getDeclaredColumnNames() {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<String> list = new ArrayList<String>();

        if (!declareColumnNames)
            return null;

        for (PgColumn column : columns) {
            list.add(column.getName());
        }

        return list;
    }

    /**
     * Creates and returns SQL for creation of the view.
     *
     * @return created SQL statement
     */
    public String getCreationSQL() {
        final StringBuilder sbSQL = new StringBuilder(query.length() * 2);
        sbSQL.append("CREATE ");
        if (materialized)
            sbSQL.append("MATERIALIZED ");
        sbSQL.append("VIEW ");
        sbSQL.append(PgDiffUtils.getQuotedName(name));

        if (declareColumnNames) {
            assert columns != null && !columns.isEmpty();

            sbSQL.append(" (");

            for (int i = 0; i < columns.size(); i++) {
                if (i > 0) {
                    sbSQL.append(", ");
                }

                sbSQL.append(PgDiffUtils.getQuotedName(columns.get(i).getName()));
            }
            sbSQL.append(')');
        }

        sbSQL.append(" AS\n\t");
        sbSQL.append(query);
        sbSQL.append(';');

        /* Column default values */
        for (final PgColumn col : getColumns()) {
            String defaultValue = col.getDefaultValue();

            if (defaultValue != null && !defaultValue.isEmpty()) {
                sbSQL.append("\n\nALTER VIEW ");
                sbSQL.append(PgDiffUtils.getQuotedName(name));
                sbSQL.append(" ALTER COLUMN ");
                sbSQL.append(PgDiffUtils.getQuotedName(col.getName()));
                sbSQL.append(" SET DEFAULT ");
                sbSQL.append(defaultValue);
                sbSQL.append(';');
            }
        }

        if (comment != null && !comment.isEmpty()) {
            sbSQL.append("\n\nCOMMENT ON VIEW ");
            sbSQL.append(PgDiffUtils.getQuotedName(name));
            sbSQL.append(" IS ");
            sbSQL.append(comment);
            sbSQL.append(';');
        }

        sbSQL.append(getColumnCommentDefinition());

        return sbSQL.toString();
    }

    /**
     * Creates and returns SQL statement for dropping the view.
     *
     * @return created SQL statement
     */
    public String getDropSQL() {
        return "DROP " + (materialized ? "MATERIALIZED " : "") +
                "VIEW " + PgDiffUtils.getQuotedName(getName()) + ";";
    }

    /**
     * Setter for {@link #materialized}.
     *
     * @param materialized {@link #materialized}
     */
    public void setMaterialized(boolean materialized) {
        this.materialized = materialized;
    }

    /**
     * Getter for {@link #materialized}.
     *
     * @return {@link #materialized}
     */
    public boolean isMaterialized() {
        return materialized;
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
     * Finds column according to specified column {@code name}.
     *
     * @param name name of the column to be searched
     *
     * @return found column or null if no such column has been found
     */
    public PgColumn getColumn(final String name) {
        PgColumn col = super.getColumn(name);
        if (col == null && !declareColumnNames) {
            /*
             * In views, we don't always know columns beforehand; create a new
             * column if the view didn't declare col names.
             */
            col = new PgColumn(name);
            addColumn(col);
        }
        return col;
    }

    /**
     * Adds/replaces column comment.
     *
     * @param columnName column name
     * @param comment    comment
     */
    public void addColumnComment(final String columnName,
            final String comment) {
        PgColumn col = getColumn(columnName);
        col.setComment(comment);
    }

    /**
     * Removes column comment if present.
     *
     * @param columnName column name
     */
    public void removeColumnComment(final String columnName) {
        addColumnComment(columnName, null);
    }
}
