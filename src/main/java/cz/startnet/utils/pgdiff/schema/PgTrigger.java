/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.schema;

/**
 * Stores trigger information.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgTrigger {
    /**
     * Function name and arguments that should be fired on the trigger.
     */
    private String function = null;

    /**
     * Name of the trigger.
     */
    private String name = null;

    /**
     * Name of the table the trigger is defined on.
     */
    private String tableName = null;

    /**
     * Whether the trigger should be fired BEFORE or AFTER action.
     * Default is before.
     */
    private boolean before = true;

    /**
     * Whether the trigger should be fired FOR EACH ROW or FOR EACH
     * STATEMENT. Default is FOR EACH STATEMENT.
     */
    private boolean forEachRow = false;

    /**
     * Whether the trigger should be fired on DELETE.
     */
    private boolean onDelete = false;

    /**
     * Whether the trigger should be fired on INSERT.
     */
    private boolean onInsert = false;

    /**
     * Whether the trigger should be fired on UPDATE.
     */
    private boolean onUpdate = false;

    /**
     * Setter for {@link #before before}.
     *
     * @param before {@link #before before}
     */
    public void setBefore(final boolean before) {
        this.before = before;
    }

    /**
     * Getter for {@link #before before}.
     *
     * @return {@link #before before}
     */
    public boolean isBefore() {
        return before;
    }

    /**
     * Creates and returns SQL for creation of trigger.
     *
     * @return created SQL
     */
    public String getCreationSQL() {
        final StringBuilder sbDDL = new StringBuilder();
        sbDDL.append("CREATE TRIGGER ");
        sbDDL.append(getName());
        sbDDL.append("\n\t");
        sbDDL.append(isBefore() ? "BEFORE" : "AFTER");

        boolean firstEvent = true;

        if (isOnInsert()) {
            sbDDL.append(" INSERT");
            firstEvent = false;
        }

        if (isOnUpdate()) {
            if (firstEvent) {
                firstEvent = false;
            } else {
                sbDDL.append(" OR");
            }

            sbDDL.append(" UPDATE");
        }

        if (isOnDelete()) {
            if (!firstEvent) {
                sbDDL.append(" OR");
            }

            sbDDL.append(" DELETE");
        }

        sbDDL.append(" ON ");
        sbDDL.append(getTableName());
        sbDDL.append("\n\tFOR EACH ");
        sbDDL.append(isForEachRow() ? "ROW" : "STATEMENT");
        sbDDL.append("\n\tEXECUTE PROCEDURE ");
        sbDDL.append(getFunction());
        sbDDL.append(';');

        return sbDDL.toString();
    }

    /**
     * Creates and returns SQL for dropping the trigger.
     *
     * @return created SQL
     */
    public String getDropSQL() {
        return "DROP TRIGGER " + getName() + " ON " + getTableName() + ";";
    }

    /**
     * Setter for {@link #forEachRow forEachRow}.
     *
     * @param forEachRow {@link #forEachRow forEachRow}
     */
    public void setForEachRow(final boolean forEachRow) {
        this.forEachRow = forEachRow;
    }

    /**
     * Getter for {@link #forEachRow forEachRow}.
     *
     * @return {@link #forEachRow forEachRow}
     */
    public boolean isForEachRow() {
        return forEachRow;
    }

    /**
     * Setter for {@link #function function}.
     *
     * @param function {@link #function function}
     */
    public void setFunction(final String function) {
        this.function = function;
    }

    /**
     * Getter for {@link #function function}.
     *
     * @return {@link #function function}
     */
    public String getFunction() {
        return function;
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
     * Setter for {@link #onDelete onDelete}.
     *
     * @param onDelete {@link #onDelete onDelete}
     */
    public void setOnDelete(final boolean onDelete) {
        this.onDelete = onDelete;
    }

    /**
     * Getter for {@link #onDelete onDelete}.
     *
     * @return {@link #onDelete onDelete}
     */
    public boolean isOnDelete() {
        return onDelete;
    }

    /**
     * Setter for {@link #onInsert onInsert}.
     *
     * @param onInsert {@link #onInsert onInsert}
     */
    public void setOnInsert(final boolean onInsert) {
        this.onInsert = onInsert;
    }

    /**
     * Getter for {@link #onInsert onInsert}.
     *
     * @return {@link #onInsert onInsert}
     */
    public boolean isOnInsert() {
        return onInsert;
    }

    /**
     * Setter for {@link #onUpdate onUpdate}.
     *
     * @param onUpdate {@link #onUpdate onUpdate}
     */
    public void setOnUpdate(final boolean onUpdate) {
        this.onUpdate = onUpdate;
    }

    /**
     * Getter for {@link #onUpdate onUpdate}.
     *
     * @return {@link #onUpdate onUpdate}
     */
    public boolean isOnUpdate() {
        return onUpdate;
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
    public boolean equals(final Object object) {
        boolean equals = false;

        if (this == object) {
            equals = true;
        } else if (object instanceof PgTrigger) {
            final PgTrigger trigger = (PgTrigger) object;
            equals =
                (before == trigger.before)
                && (forEachRow == trigger.forEachRow)
                && function.equals(trigger.function)
                && name.equals(trigger.name) && (onDelete == trigger.onDelete)
                && (onInsert == trigger.onInsert)
                && (onUpdate == trigger.onUpdate)
                && tableName.equals(trigger.tableName);
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
        return (getClass().getName() + "|" + before + "|" + forEachRow + "|"
        + function + "|" + name + "|" + onDelete + "|" + onInsert + "|"
        + onUpdate + "|" + tableName).hashCode();
    }
}
