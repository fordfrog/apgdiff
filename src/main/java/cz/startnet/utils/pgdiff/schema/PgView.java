package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;

/**
 * Stores view information.
 *
 * @author fordfrog
 */
public class PgView {

    /**
     * String specifying column names.
     */
    private String columnNames;
    /**
     * Name of the view.
     */
    private final String name;
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
        super();
        this.name = name;
    }

    /**
     * Setter for {@link #columnNames}.
     *
     * @param columnNames {@link #columnNames}
     */
    public void setColumnNames(final String columnNames) {
        this.columnNames = columnNames;
    }

    /**
     * Getter for {@link #columnNames}.
     *
     * @return {@link #columnNames}
     */
    public String getColumnNames() {
        return columnNames;
    }

    /**
     * Creates and returns SQL for creation of the view.
     *
     * @param quoteNames whether names should be quoted
     *
     * @return created SQL command
     */
    public String getCreationSQL(final boolean quoteNames) {
        final StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("CREATE VIEW ");
        sbSQL.append(PgDiffUtils.getQuotedName(name, quoteNames));

        if ((columnNames != null) && (columnNames.length() > 0)) {
            sbSQL.append(" (");
            sbSQL.append(columnNames);
            sbSQL.append(')');
        }

        sbSQL.append(" AS\n\t");
        sbSQL.append(query);
        sbSQL.append(';');

        return sbSQL.toString();
    }

    /**
     * Creates and returns SQL command for dropping the view.
     *
     * @param quoteNames whether names should be quoted
     *
     * @return created SQL command
     */
    public String getDropSQL(final boolean quoteNames) {
        return "DROP VIEW " + PgDiffUtils.getQuotedName(getName(), quoteNames)
                + ";";
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
}
