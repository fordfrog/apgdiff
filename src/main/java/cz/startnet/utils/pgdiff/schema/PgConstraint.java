/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.startnet.utils.pgdiff.PgDiffUtils;
import cz.startnet.utils.pgdiff.parsers.ParserUtils;

/**
 * Stores table constraint information.
 *
 * @author fordfrog
 */
public class PgConstraint {

    /**
     * Pattern for checking whether the constraint is PRIMARY KEY constraint,
     * and extract the columns forming the primary key (group#1).
     */
    private static final Pattern PATTERN_PRIMARY_KEY =
            Pattern.compile(".*PRIMARY[\\s]+KEY[\\s]+\\(([^)]+)\\).*", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for checking whether the constraint is FOREIGN KEY constraint.
     * and extract the columns forming the foreign key (group#1), and the references table(group#2) and columns(group#3).
     */
    public static final Pattern PATTERN_FOREIGN_KEY =
    		Pattern.compile(".*FOREIGN\\s+KEY\\s*\\(\\s*([^)]+)\\s*\\)\\s+REFERENCES\\s+([^\\s]+)\\s*\\(\\s*([^)]+)\\s*\\).*",Pattern.CASE_INSENSITIVE);

    public static enum Mode {
    	StandAlone,
    	GroupElement
    };

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

    public PgConstraint(String name, String definition, PgTable table) {
        this.name = name;
        this.definition = definition;
        this.tableName = table.getName();
    }

    public static PgConstraint newConstraint(String name, String definition, PgTable table) {
    	if (isPrimaryKeyConstraint(definition)) {
    		Matcher matcher = PATTERN_PRIMARY_KEY.matcher(definition);
    		matcher.matches();
    		return new PgPkConstraint(
    				name,
    				definition,
    				table,
    				ParserUtils.splitIdentifierList(matcher.group(1)));
    	} else if (isForeignKeyConstraint(definition)) {
    		Matcher matcher = PATTERN_FOREIGN_KEY.matcher(definition);
    		matcher.matches();
    		return new PgFkConstraint(
    				name,
    				definition,
    				table,
    				ParserUtils.splitIdentifierList(matcher.group(1)),
    				matcher.group(2),
    				ParserUtils.splitIdentifierList(matcher.group(3)));
    	} else {
    		return new PgConstraint(name,definition,table);
    	}
    }

    public String getCreationSQL() {
    	return getCreationSQL(Mode.StandAlone)+";";
    }

    /**
     * Creates and returns SQL for creation of the constraint.
     *
     * @return created SQL
     */
    public String getCreationSQL(Mode mode) {
        final StringBuilder sbSQL = new StringBuilder(100);
        if (mode==Mode.StandAlone) {
        	sbSQL.append("ALTER TABLE ");
        	sbSQL.append(PgDiffUtils.getQuotedName(getTableName()));
            sbSQL.append("\n");
        }
        sbSQL.append("\tADD CONSTRAINT ");
        sbSQL.append(PgDiffUtils.getQuotedName(getName()));
        sbSQL.append(' ');
        sbSQL.append(getDefinition());

        return sbSQL.toString();
    }

    public String getCommentSQL() {
        final StringBuilder sbSQL = new StringBuilder(100);
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
        return isPrimaryKeyConstraint(definition);
    }

    public static boolean isPrimaryKeyConstraint(String definition) {
    	return PATTERN_PRIMARY_KEY.matcher(definition).matches();
    }

    /**
     * Returns true if this is a FOREIGN KEY constraint, otherwise false.
     *
     * @return true if this is a FOREIGN KEY constraint, otherwise false
     */
    public boolean isForeignKeyConstraint() {
        return isForeignKeyConstraint(definition);
    }
    public static boolean isForeignKeyConstraint(String definition) {
    	return PATTERN_FOREIGN_KEY.matcher(definition).matches();
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

    public String toString() {
    	return "PgConstraint: "+name+" "+definition;
    }
}
