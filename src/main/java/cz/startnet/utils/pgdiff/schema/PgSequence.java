/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.startnet.utils.pgdiff.PgDiffUtils;

/**
 * Stores sequence information.
 *
 * @author fordfrog
 */
public class PgSequence {

    /**
     * Value for CACHE or null if no value is specified.
     */
    private String cache;
    /**
     * Value for INCREMENT BY or null if no value is specified.
     */
    private String increment;
    /**
     * Value for MAXVALUE or null if no value is specified.
     */
    private String maxValue;
    /**
     * Value for MINVALUE or null if no value is specified.
     */
    private String minValue;
    /**
     * Name of the sequence.
     */
    private String name;
    /**
     * Value for START WITH or null if no value is specified.
     */
    private String startWith;
    /**
     * True if CYCLE, false if NO CYCLE.
     */
    private boolean cycle;
    /**
     * Column the sequence is owned by.
     */
    private String ownedBy;
    /**
     * Comment.
     */
    private String comment;
    /**
     * List of privileges defined on the sequence.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgSequencePrivilege> privileges = new ArrayList<PgSequencePrivilege>();

    /**
     * Creates a new PgSequence object.
     *
     * @param name name of the sequence
     */
    public PgSequence(final String name) {
        this.name = name;
    }

    /**
     * Setter for {@link #cache}.
     *
     * @param cache {@link #cache}
     */
    public void setCache(final String cache) {
        this.cache = cache;
    }

    /**
     * Getter for {@link #cache}.
     *
     * @return {@link #cache}
     */
    public String getCache() {
        return cache;
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
     * Creates and returns SQL statement for creation of the sequence.
     *
     * @return created SQL statement
     */
    public String getCreationSQL() {
        final StringBuilder sbSQL = new StringBuilder(100);
        sbSQL.append("CREATE SEQUENCE ");
        
        sbSQL.append(PgDiffUtils.getCreateIfNotExists());
        
        sbSQL.append(PgDiffUtils.getQuotedName(name));

        if (startWith != null) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("\tSTART WITH ");
            sbSQL.append(startWith);
        }

        if (increment != null) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("\tINCREMENT BY ");
            sbSQL.append(increment);
        }

        sbSQL.append(System.getProperty("line.separator"));
        sbSQL.append("\t");

        if (maxValue == null) {
            sbSQL.append("NO MAXVALUE");
        } else {
            sbSQL.append("MAXVALUE ");
            sbSQL.append(maxValue);
        }

        sbSQL.append(System.getProperty("line.separator"));
        sbSQL.append("\t");

        if (minValue == null) {
            sbSQL.append("NO MINVALUE");
        } else {
            sbSQL.append("MINVALUE ");
            sbSQL.append(minValue);
        }

        if (cache != null) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("\tCACHE ");
            sbSQL.append(cache);
        }

        if (cycle) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("\tCYCLE");
        }

        sbSQL.append(';');

        if (comment != null && !comment.isEmpty()) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("COMMENT ON SEQUENCE ");
            sbSQL.append(PgDiffUtils.getQuotedName(name));
            sbSQL.append(" IS ");
            sbSQL.append(comment);
            sbSQL.append(';');
        }

        return sbSQL.toString();
    }

    /**
     * Creates and returns SQL statement for modification "OWNED BY" parameter.
     *
     * @return created SQL statement
     */
    public String getOwnedBySQL() {
        final StringBuilder sbSQL = new StringBuilder(100);

        sbSQL.append("ALTER SEQUENCE ");
        sbSQL.append(PgDiffUtils.getQuotedName(name));

        if (ownedBy != null && !ownedBy.isEmpty()) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("\tOWNED BY ");
            sbSQL.append(ownedBy);
        }

        sbSQL.append(';');

        return sbSQL.toString();
    }

    /**
     * Setter for {@link #cycle}.
     *
     * @param cycle {@link #cycle}
     */
    public void setCycle(final boolean cycle) {
        this.cycle = cycle;
    }

    /**
     * Getter for {@link #cycle}.
     *
     * @return {@link #cycle}
     */
    public boolean isCycle() {
        return cycle;
    }

    /**
     * Creates and returns SQL statement for dropping the sequence.
     *
     * @return created SQL
     */
    public String getDropSQL() {
        return "DROP SEQUENCE " + PgDiffUtils.getDropIfExists() + PgDiffUtils.getQuotedName(getName()) + ";";
    }

    /**
     * Setter for {@link #increment}.
     *
     * @param increment {@link #increment}
     */
    public void setIncrement(final String increment) {
        this.increment = increment;
    }

    /**
     * Getter for {@link #increment}.
     *
     * @return {@link #increment}
     */
    public String getIncrement() {
        return increment;
    }

    /**
     * Setter for {@link #maxValue}.
     *
     * @param maxValue {@link #maxValue}
     */
    public void setMaxValue(final String maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Getter for {@link #maxValue}.
     *
     * @return {@link #maxValue}
     */
    public String getMaxValue() {
        return maxValue;
    }

    /**
     * Setter for {@link #minValue}.
     *
     * @param minValue {@link #minValue}
     */
    public void setMinValue(final String minValue) {
        this.minValue = minValue;
    }

    /**
     * Getter for {@link #minValue}.
     *
     * @return {@link #minValue}
     */
    public String getMinValue() {
        return minValue;
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
     * Setter for {@link #startWith}.
     *
     * @param startWith {@link #startWith}
     */
    public void setStartWith(final String startWith) {
        this.startWith = startWith;
    }

    /**
     * Getter for {@link #startWith}.
     *
     * @return {@link #startWith}
     */
    public String getStartWith() {
        return startWith;
    }

    /**
     * Getter for {@link #ownedBy}.
     *
     * @return {@link #ownedBy}
     */
    public String getOwnedBy() {
        return ownedBy;
    }

    /**
     * Setter for {@link #ownedBy}.
     *
     * @param ownedBy {@link #ownedBy}
     */
    public void setOwnedBy(final String ownedBy) {
        this.ownedBy = ownedBy;
    }

    public List<PgSequencePrivilege> getPrivileges() {
        return Collections.unmodifiableList(privileges);
    }

    public PgSequencePrivilege getPrivilege(final String roleName) {
        for (PgSequencePrivilege privilege : privileges) {
            if (privilege.getRoleName().equals(roleName)) {
                return privilege;
            }
        }
        return null;
    }

    public void addPrivilege(final PgSequencePrivilege privilege) {
        privileges.add(privilege);
    }
}
