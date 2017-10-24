/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores trigger information.
 *
 * @author fordfrog
 */
public class PgTrigger {

    /**
     * Enumeration of when, with respect to event, a trigger should fire.
     * e.g. BEFORE, AFTER or INSTEAD OF an event.
     */
    public enum EventTimeQualification {
        before,
        after,
        instead_of;

        private static final Map<EventTimeQualification, String> stringRepresentation;

        static {
            HashMap<EventTimeQualification, String> aMap = new HashMap<EventTimeQualification, String>();
            aMap.put(EventTimeQualification.before, "BEFORE");
            aMap.put(EventTimeQualification.after, "AFTER");
            aMap.put(EventTimeQualification.instead_of, "INSTEAD OF");

            stringRepresentation = Collections.unmodifiableMap(aMap);
        }

        public static String toString(EventTimeQualification eventTimeQualification) {
            return stringRepresentation.get(eventTimeQualification);
        }
    }
    
    /**
     * Whether the trigger is contraint.
     */
    private boolean constraint;
    
    /**
     * This controls whether the constraint can be deferred. 
     * A constraint that is not deferrable will be checked immediately after every command. 
     * Checking of constraints that are deferrable can be postponed until the end of the transaction.
     */
    
    private boolean deferrable;
    
    /**
     * If a constraint is deferrable, this clause specifies the default time to check the constraint. 
     * If the constraint is INITIALLY IMMEDIATE, it is checked after each statement. 
     * This is the default. If the constraint is INITIALLY DEFERRED, it is checked only at the end of the transaction.
     */
    
    private boolean deferred;

    /**
     * Function name and arguments that should be fired on the trigger.
     */
    private String function;
    /**
     * Name of the trigger.
     */
    private String name;
    /**
     * Name of the relation the trigger is defined on.
     */
    private String relationName;
    /**
     * Whether the trigger should be fired BEFORE, AFTER or INSTEAD OF an event.
     * Default is before.
     */
    private EventTimeQualification eventTimeQualification = EventTimeQualification.before;
    /**
     * Whether the trigger should be fired FOR EACH ROW or FOR EACH STATEMENT.
     * Default is FOR EACH STATEMENT.
     */
    private boolean forEachRow;
    /**
     * Whether the trigger should be fired on DELETE.
     */
    private boolean onDelete;
    /**
     * Whether the trigger should be fired on INSERT.
     */
    private boolean onInsert;
    /**
     * Whether the trigger should be fired on UPDATE.
     */
    private boolean onUpdate;
    /**
     * Whether the trigger should be fired on TRUNCATE.
     */
    private boolean onTruncate;
    /**
     * Optional list of columns for UPDATE event.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<String> updateColumns = new ArrayList<String>();
    /**
     * WHEN condition.
     */
    private String when;
    /**
     * Comment.
     */
    private String comment;

    /**
     * Setter for {@link #eventTimeQualification}.
     *
     * @param eventTimeQualification {@link #eventTimeQualification}
     */
    public void setEventTimeQualification(final EventTimeQualification eventTimeQualification) {
        this.eventTimeQualification = eventTimeQualification;
    }

    /**
     * Getter for {@link #eventTimeQualification}.
     *
     * @return {@link #eventTimeQualification}
     */
    public EventTimeQualification getEventTimeQualification() {
        return eventTimeQualification;
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
     * Creates and returns SQL for creation of trigger.
     *
     * @return created SQL
     */
    public String getCreationSQL() {
        final StringBuilder sbSQL = new StringBuilder(100);
        sbSQL.append("CREATE");
        if(isConstraint())
        	sbSQL.append(" CONSTRAINT");
        sbSQL.append(" TRIGGER ");
        sbSQL.append(PgDiffUtils.getQuotedName(getName()));
        sbSQL.append(System.getProperty("line.separator"));
        sbSQL.append("\t");
        sbSQL.append(EventTimeQualification.toString(getEventTimeQualification()));

        boolean firstEvent = true;
        
        if (isOnInsert()) {
            sbSQL.append(" INSERT");
            firstEvent = false;
        }

        if (isOnUpdate()) {
            if (firstEvent) {
                firstEvent = false;
            } else {
                sbSQL.append(" OR");
            }

            sbSQL.append(" UPDATE");

            if (!updateColumns.isEmpty()) {
                sbSQL.append(" OF");

                boolean first = true;

                for (final String columnName : updateColumns) {
                    if (first) {
                        first = false;
                    } else {
                        sbSQL.append(',');
                    }

                    sbSQL.append(' ');
                    sbSQL.append(columnName);
                }
            }
        }

        if (isOnDelete()) {
            if (!firstEvent) {
                sbSQL.append(" OR");
            }

            sbSQL.append(" DELETE");
        }

        if (isOnTruncate()) {
            if (!firstEvent) {
                sbSQL.append(" OR");
            }

            sbSQL.append(" TRUNCATE");
        }

        sbSQL.append(" ON ");
        sbSQL.append(PgDiffUtils.getQuotedName(getRelationName()));
        if(isConstraint()){
        	sbSQL.append(System.getProperty("line.separator"));
        	sbSQL.append("\t");
        	if(isDeferrable()){
        		sbSQL.append(" DEFERRABLE ");
        		if(isDeferred()){
        			sbSQL.append(" INITIALLY DEFERRED ");
        		}else{
        			sbSQL.append(" INITIALLY IMMEDIATE ");
        		}
        	}else {
        		sbSQL.append(" NOT DEFERRABLE ");
        	}
        }
        sbSQL.append(System.getProperty("line.separator"));
        sbSQL.append("\tFOR EACH ");
        sbSQL.append(isForEachRow() ? "ROW" : "STATEMENT");

        if (when != null && !when.isEmpty()) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("\tWHEN (");
            sbSQL.append(when);
            sbSQL.append(')');
        }

        sbSQL.append(System.getProperty("line.separator"));
        sbSQL.append("\tEXECUTE PROCEDURE ");
        sbSQL.append(getFunction());
        sbSQL.append(';');

        if (comment != null && !comment.isEmpty()) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("COMMENT ON TRIGGER ");
            sbSQL.append(PgDiffUtils.getQuotedName(name));
            sbSQL.append(" ON ");
            sbSQL.append(PgDiffUtils.getQuotedName(relationName));
            sbSQL.append(" IS ");
            sbSQL.append(comment);
            sbSQL.append(';');
        }

        return sbSQL.toString();
    }

    /**
     * Creates and returns SQL for dropping the trigger.
     *
     * @return created SQL
     */
    public String getDropSQL() {
        return "DROP TRIGGER " + PgDiffUtils.getDropIfExists() + PgDiffUtils.getQuotedName(getName()) + " ON "
                + PgDiffUtils.getQuotedName(getRelationName()) + ";";
    }
    
    /**
     * Setter for {@link #constraint}.
     *
     * @param constraint {@link #constraint}
     */
    public void setConstraint(final boolean constraint) {
        this.constraint = constraint;
    }

    /**
     * Getter for {@link #constraint}.
     *
     * @return {@link #constraint}
     */
    public boolean isConstraint() {
        return constraint;
    }
    
    /**
     * Setter for {@link #deferrable}.
     *
     * @param deferrable {@link #deferrable}
     */
    public void setDeferrable(final boolean deferrable) {
        this.deferrable = deferrable;
    }

    /**
     * Getter for {@link #deferrable}.
     *
     * @return {@link #deferrable}
     */
    public boolean isDeferrable() {
        return deferrable;
    }
    
    /**
     * Setter for {@link #deferred}.
     *
     * @param deferred {@link #deferred}
     */
    public void setDeferred(final boolean deferred) {
        this.deferred = deferred;
    }

    /**
     * Getter for {@link #deferred}.
     *
     * @return {@link #deferred}
     */
    public boolean isDeferred() {
        return deferred;
    }

    /**
     * Setter for {@link #forEachRow}.
     *
     * @param forEachRow {@link #forEachRow}
     */
    public void setForEachRow(final boolean forEachRow) {
        this.forEachRow = forEachRow;
    }

    /**
     * Getter for {@link #forEachRow}.
     *
     * @return {@link #forEachRow}
     */
    public boolean isForEachRow() {
        return forEachRow;
    }

    /**
     * Setter for {@link #function}.
     *
     * @param function {@link #function}
     */
    public void setFunction(final String function) {
        this.function = function;
    }

    /**
     * Getter for {@link #function}.
     *
     * @return {@link #function}
     */
    public String getFunction() {
        return function;
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
     * Setter for {@link #onDelete}.
     *
     * @param onDelete {@link #onDelete}
     */
    public void setOnDelete(final boolean onDelete) {
        this.onDelete = onDelete;
    }

    /**
     * Getter for {@link #onDelete}.
     *
     * @return {@link #onDelete}
     */
    public boolean isOnDelete() {
        return onDelete;
    }

    /**
     * Setter for {@link #onInsert}.
     *
     * @param onInsert {@link #onInsert}
     */
    public void setOnInsert(final boolean onInsert) {
        this.onInsert = onInsert;
    }

    /**
     * Getter for {@link #onInsert}.
     *
     * @return {@link #onInsert}
     */
    public boolean isOnInsert() {
        return onInsert;
    }

    /**
     * Setter for {@link #onUpdate}.
     *
     * @param onUpdate {@link #onUpdate}
     */
    public void setOnUpdate(final boolean onUpdate) {
        this.onUpdate = onUpdate;
    }

    /**
     * Getter for {@link #onUpdate}.
     *
     * @return {@link #onUpdate}
     */
    public boolean isOnUpdate() {
        return onUpdate;
    }

    /**
     * Getter for {@link #onTruncate}.
     *
     * @return {@link #onTruncate}
     */
    public boolean isOnTruncate() {
        return onTruncate;
    }

    /**
     * Setter for {@link #onTruncate}.
     *
     * @param onTruncate {@link #onTruncate}
     */
    public void setOnTruncate(final boolean onTruncate) {
        this.onTruncate = onTruncate;
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
     * Getter for {@link #updateColumns}.
     *
     * @return {@link #updateColumns}
     */
    public List<String> getUpdateColumns() {
        return Collections.unmodifiableList(updateColumns);
    }

    /**
     * Adds column name to the list of update columns.
     *
     * @param columnName column name
     */
    public void addUpdateColumn(final String columnName) {
        updateColumns.add(columnName);
    }

    /**
     * Getter for {@link #when}.
     *
     * @return {@link #when}
     */
    public String getWhen() {
        return when;
    }

    /**
     * Setter for {@link #when}.
     *
     * @param when {@link #when}
     */
    public void setWhen(final String when) {
        this.when = when;
    }

    @Override
    public boolean equals(final Object object) {
        boolean equals = false;

        if (this == object) {
            equals = true;
        } else if (object instanceof PgTrigger) {
            final PgTrigger trigger = (PgTrigger) object;
            equals = (eventTimeQualification == trigger.getEventTimeQualification())
                    && (forEachRow == trigger.isForEachRow())
                    && function.equals(trigger.getFunction())
                    && name.equals(trigger.getName())
                    && (onDelete == trigger.isOnDelete())
                    && (onInsert == trigger.isOnInsert())
                    && (onUpdate == trigger.isOnUpdate())
                    && (onTruncate == trigger.isOnTruncate())
                    && relationName.equals(trigger.getRelationName())
                    && constraint == trigger.isConstraint()
                    && deferrable == trigger.isDeferrable()
                    && deferred == trigger.isDeferred();

            if (equals) {
                final List<String> sorted1 =
                        new ArrayList<String>(updateColumns);
                final List<String> sorted2 =
                        new ArrayList<String>(trigger.getUpdateColumns());
                Collections.sort(sorted1);
                Collections.sort(sorted2);

                equals = sorted1.equals(sorted2);
            }
        }

        return equals;
    }

    @Override
    public int hashCode() {
        return (getClass().getName() + "|" + eventTimeQualification + "|" + forEachRow + "|"
                + function + "|" + name + "|" + onDelete + "|" + onInsert + "|"
                + onUpdate + "|" + onTruncate + "|" + relationName).hashCode();
    }
}
