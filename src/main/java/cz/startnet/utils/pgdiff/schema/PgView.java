package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;
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
     * Creates and returns SQL for creation of the view.
     *
     * @return created SQL command
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

        return sbSQL.toString();
    }

    /**
     * Creates and returns SQL command for dropping the view.
     *
     * @return created SQL command
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
}
