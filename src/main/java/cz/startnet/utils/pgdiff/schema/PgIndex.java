/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;


/**
 * Stores table index information.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgIndex {
    /**
     * Definition of the index.
     */
    private String definition = null;

    /**
     * Name of the index.
     */
    private String name = null;

    /**
     * Table name the index is defined on.
     */
    private String tableName = null;

    /**
     * Creates a new PgIndex object.
     *
     * @param name name of the index
     */
    public PgIndex(final String name) {
        this.name = name;
    }

    /**
     * Creates and returns SQL for creation of the index.
     *
     * @param quoteNames whether names should be quoted
     *
     * @return created SQL
     */
    public String getCreationSQL(final boolean quoteNames) {
        final StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("CREATE INDEX ");
        sbSQL.append(PgDiffUtils.getQuotedName(getName(), quoteNames));
        sbSQL.append(" ON ");
        sbSQL.append(PgDiffUtils.getQuotedName(getTableName(), quoteNames));
        sbSQL.append(' ');
        sbSQL.append(getDefinition());
        sbSQL.append(';');

        return sbSQL.toString();
    }

    /**
     * Setter for {@link #definition definition}.
     *
     * @param definition {@link #definition definition}
     */
    public void setDefinition(final String definition) {
        this.definition = definition;
    }

    /**
     * Getter for {@link #definition definition}.
     *
     * @return {@link #definition definition}
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Creates and returns SQL command for dropping the index.
     *
     * @param quoteNames whether names should be quoted
     *
     * @return created SQL command
     */
    public String getDropSQL(final boolean quoteNames) {
        return "DROP INDEX " + PgDiffUtils.getQuotedName(getName(), quoteNames)
        + ";";
    }

    /**
     * Setter for {@link #name name}.
     *
     * @param name {@link #name name}
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for {@link #name name}.
     *
     * @return {@link #name name}
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for {@link #tableName tableName}.
     *
     * @param tableName {@link #tableName tableName}
     */
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    /**
     * Getter for {@link #tableName tableName}.
     *
     * @return {@link #tableName tableName}
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * {@inheritDoc}
     *
     * @param object {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean equals(final Object object) {
        boolean equals = false;

        if (this == object) {
            equals = true;
        } else if (object instanceof PgIndex) {
            final PgIndex index = (PgIndex) object;
            equals =
                definition.equals(index.definition) && name.equals(index.name)
                && tableName.equals(index.tableName);
        }

        return equals;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return (getClass().getName() + "|" + definition + "|" + name + "|"
        + tableName).hashCode();
    }
}
