/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;

import java.util.regex.Pattern;


/**
 * Stores table constraint information.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgConstraint {
    /**
     * Pattern for checking whether the constraint is PRIMARY KEY
     * constraint.
     */
    private static final Pattern PATTERN_PRIMARY_KEY =
        Pattern.compile(".*PRIMARY[\\s]+KEY.*", Pattern.CASE_INSENSITIVE);

    /**
     * Definition of the constraint.
     */
    private String definition = null;

    /**
     * Name of the constraint.
     */
    private String name = null;

    /**
     * Name of the table the constraint is defined on.
     */
    private String tableName = null;

    /**
     * Creates a new PgConstraint object.
     *
     * @param name name of the constraint
     */
    public PgConstraint(String name) {
        this.name = name;
    }

    /**
     * Creates and returns SQL for creation of the constraint.
     *
     * @param quoteNames whether names should be quoted
     *
     * @return created SQL
     */
    public String getCreationSQL(final boolean quoteNames) {
        final StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("ALTER TABLE ");
        sbSQL.append(PgDiffUtils.getQuotedName(getTableName(), quoteNames));
        sbSQL.append("\n\tADD CONSTRAINT ");
        sbSQL.append(PgDiffUtils.getQuotedName(getName(), quoteNames));
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
     * Creates and returns SQL for dropping the constraint.
     *
     * @param quoteNames whether names should be quoted
     *
     * @return created SQL
     */
    public String getDropSQL(final boolean quoteNames) {
        final StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("ALTER TABLE ");
        sbSQL.append(PgDiffUtils.getQuotedName(getTableName(), quoteNames));
        sbSQL.append("\n\tDROP CONSTRAINT ");
        sbSQL.append(PgDiffUtils.getQuotedName(getName(), quoteNames));
        sbSQL.append(';');

        return sbSQL.toString();
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
     * Returns true if this is a PRIMARY KEY constraint, otherwise
     * false.
     *
     * @return true if this is a PRIMARY KEY constraint, otherwise false
     */
    public boolean isPrimaryKeyConstraint() {
        return PATTERN_PRIMARY_KEY.matcher(definition).matches();
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
        } else if (object instanceof PgConstraint) {
            final PgConstraint constraint = (PgConstraint) object;
            equals =
                definition.equals(constraint.definition)
                && name.equals(constraint.name)
                && tableName.equals(constraint.tableName);
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
