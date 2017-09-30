/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;

/**
 * Stores table index information.
 *
 * @author fordfrog
 */
public class PgIndex {

    /**
     * Definition of the index.
     */
    private String definition;
    /**
     * Name of the index.
     */
    private String name;
    /**
     * Table name the index is defined on.
     */
    private String tableName;
    /**
     * Whether the index is unique.
     */
    private boolean unique;
    /**
     * Comment.
     */
    private String comment;

    /**
     * Creates a new PgIndex object.
     *
     * @param name {@link #name}
     */
    public PgIndex(final String name) {
        this.name = name;
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
     * Creates and returns SQL for creation of the index.
     *
     * @return created SQL
     */
    public String getCreationSQL() {
        final StringBuilder sbSQL = new StringBuilder(100);
        sbSQL.append("CREATE ");

        if (isUnique()) {
            sbSQL.append("UNIQUE ");
        }

        sbSQL.append("INDEX ");
        sbSQL.append(PgDiffUtils.getCreateIfNotExists());        
        sbSQL.append(PgDiffUtils.getQuotedName(getName()));
        sbSQL.append(" ON ");
        sbSQL.append(PgDiffUtils.getQuotedName(getTableName()));
        sbSQL.append(' ');
        sbSQL.append(getDefinition());
        sbSQL.append(';');

        if (comment != null && !comment.isEmpty()) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("COMMENT ON INDEX ");
            sbSQL.append(PgDiffUtils.getQuotedName(name));
            sbSQL.append(" IS ");
            sbSQL.append(comment);
            sbSQL.append(';');
        }

        return sbSQL.toString();
    }

    /**
     * Setter for {@link #definition}.
     *
     * @param definition {@link #definition}
     */
    public void setDefinition(final String definition) {
        this.definition = definition;
    }

    /**
     * Getter for {@link #definition}.
     *
     * @return {@link #definition}
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Creates and returns SQL statement for dropping the index.
     *
     * @return created SQL statement
     */
    public String getDropSQL() {
        return "DROP INDEX " + PgDiffUtils.getDropIfExists() + PgDiffUtils.getQuotedName(getName()) + ";";
    }

    /**
     * Setter for {@link #name}.
     *
     * @param name {@link #name}
     */
    public void setName(final String name) {
        this.name = name;
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
     * Setter for {@link #tableName}.
     *
     * @param tableName {@link #tableName}
     */
    public void setTableName(final String tableName) {
        this.tableName = tableName;
    }

    /**
     * Getter for {@link #tableName}.
     *
     * @return {@link #tableName}
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
            equals = definition.equals(index.getDefinition())
                    && name.equals(index.getName())
                    && tableName.equals(index.getTableName())
                    && unique == index.isUnique();
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
                + tableName + "|" + unique).hashCode();
    }

    /**
     * Getter for {@link #unique}.
     *
     * @return {@link #unique}
     */
    public boolean isUnique() {
        return unique;
    }

    /**
     * Setter for {@link #unique}.
     *
     * @param unique {@link #unique}
     */
    public void setUnique(final boolean unique) {
        this.unique = unique;
    }
}
