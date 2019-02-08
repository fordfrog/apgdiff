/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;
import java.util.List;

/**
 * Stores rule information.
 *
 * @author jalissonmello
 */
public class PgRule extends PgRelation {

    private String query;

    /**
     * Name of the relation the rule is defined on.
     */
    private String relationName;

    /**
     * event of rule.
     */
    private String event;

    /**
     * Creates a new PgView object.
     *
     * @param name {@link #name}
     */
    public PgRule(final String name) {
        setName(name);
    }

    /**
     * Creates and returns SQL for creation of the view.
     *
     * @return created SQL statement
     */
    public String getCreationSQL() {
        final StringBuilder sbSQL = new StringBuilder(query.length() * 2);
        sbSQL.append("CREATE ");
        sbSQL.append(getRelationKind());
        sbSQL.append(' ');
        sbSQL.append(PgDiffUtils.getQuotedName(name));

        sbSQL.append(" AS");
        sbSQL.append(System.getProperty("line.separator"));
        sbSQL.append(" ON ");        
        sbSQL.append(event);
        sbSQL.append(" TO ");
        sbSQL.append(relationName);
        sbSQL.append(System.getProperty("line.separator"));
        sbSQL.append(" ");
        sbSQL.append(query);
        sbSQL.append(";");

        sbSQL.append(getCommentDefinitionSQL());

        return sbSQL.toString();
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
     * Setter for {@link #relationName}.
     *
     * @param relationName {@link #relationName}
     */
    public void setRelationName(final String relationName) {
        this.relationName = relationName;
    }

    /**
     * Getter for {@link #relationName}.
     *
     * @return {@link #relationName}
     */
    public String getRelationName() {
        return relationName;
    }

    /**
     * Setter for {@link #event}.
     *
     * @param event {@link #event}
     */
    public void setEvent(final String event) {
        this.event = event;
    }

    /**
     * Getter for {@link #event}.
     *
     * @return {@link #event}
     */
    public String getEvent() {
        return event;
    }

    @Override
    public String getRelationKind() {
        return "RULE";
    }
    
    /**
     * Creates and returns SQL for dropping the rule.
     *
     * @return created SQL
     */
    public String getDropSQL() {
        return "DROP RULE " + PgDiffUtils.getDropIfExists() + PgDiffUtils.getQuotedName(getName()) + " ON "
                + PgDiffUtils.getQuotedName(getRelationName()) + ";";
    }

    @Override
    public boolean equals(final Object object) {
        boolean equals = false;

        if (this == object) {
            equals = true;
        } else if (object instanceof PgRule) {
            final PgRule rule = (PgRule) object;
            equals = (event == rule.getEvent()
                    && relationName.equals(rule.getRelationName())
                    && name.equals(rule.getName())
                    && query.equals(rule.getQuery()));

        }

        return equals;
    }

}
