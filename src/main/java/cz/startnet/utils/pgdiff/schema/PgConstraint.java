/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;
import java.util.regex.Pattern;

/**
 * Stores table constraint information.
 *
 * @author fordfrog
 */
public class PgConstraint {

    /**
     * Pattern for checking whether the constraint is PRIMARY KEY constraint.
     */
    private static final Pattern PATTERN_PRIMARY_KEY =
            Pattern.compile(".*PRIMARY[\\s]+KEY.*", Pattern.CASE_INSENSITIVE);
    /**
     * Definition of the constraint.
     */
    private String definition;
    /**
     * Name of the constraint.
     */
    private String name;
    /**
     * Name of the table the constraint is defined on.
     */
    private String tableName;
    /**
     * Comment.
     */
    private String comment;

    /**
     * Creates a new PgConstraint object.
     *
     * @param name {@link #name}
     */
    public PgConstraint(String name) {
        this.name = name;
    }

    /**
     * Creates and returns SQL for creation of the constraint.
     *
     * @return created SQL
     */
    public String getCreationSQL() {
        final StringBuilder sbSQL = new StringBuilder(100);
        sbSQL.append("ALTER TABLE ");
        sbSQL.append(PgDiffUtils.getQuotedName(getTableName()));
        sbSQL.append("\n\tADD CONSTRAINT ");
        sbSQL.append(PgDiffUtils.getQuotedName(getName()));
        sbSQL.append(' ');
        sbSQL.append(getDefinition());
        sbSQL.append(';');

        if (comment != null && !comment.isEmpty()) {
            sbSQL.append("\n\nCOMMENT ON CONSTRAINT ");
            sbSQL.append(PgDiffUtils.getQuotedName(name));
            sbSQL.append(" ON ");
            sbSQL.append(PgDiffUtils.getQuotedName(tableName));
            sbSQL.append(" IS ");
            sbSQL.append(comment);
            sbSQL.append(';');
        }

        return sbSQL.toString();
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
     * Creates and returns SQL for dropping the constraint.
     *
     * @return created SQL
     */
    public String getDropSQL() {
        final StringBuilder sbSQL = new StringBuilder(100);
        sbSQL.append("ALTER TABLE ");
        sbSQL.append(PgDiffUtils.getQuotedName(getTableName()));
        sbSQL.append("\n\tDROP CONSTRAINT ");
        sbSQL.append(PgDiffUtils.getQuotedName(getName()));
        sbSQL.append(';');

        return sbSQL.toString();
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
     * Returns true if this is a PRIMARY KEY constraint, otherwise false.
     *
     * @return true if this is a PRIMARY KEY constraint, otherwise false
     */
    public boolean isPrimaryKeyConstraint() {
        return PATTERN_PRIMARY_KEY.matcher(definition).matches();
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
        } else if (object instanceof PgConstraint) {
            final PgConstraint constraint = (PgConstraint) object;
            equals = definition.equals(constraint.getDefinition())
                    && name.equals(constraint.getName())
                    && tableName.equals(constraint.getTableName());
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
