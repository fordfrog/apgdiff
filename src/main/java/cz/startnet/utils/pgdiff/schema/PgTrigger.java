package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;

/**
 * Stores trigger information.
 *
 * @author fordfrog
 */
public class PgTrigger {

    /**
     * Function name and arguments that should be fired on the trigger.
     */
    private String function;
    /**
     * Name of the trigger.
     */
    private String name;
    /**
     * Name of the table the trigger is defined on.
     */
    private String tableName;
    /**
     * Whether the trigger should be fired BEFORE or AFTER action.
     * Default is before.
     */
    private boolean before = true;
    /**
     * Whether the trigger should be fired FOR EACH ROW or FOR EACH
     * STATEMENT. Default is FOR EACH STATEMENT.
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
     * Setter for {@link #before}.
     *
     * @param before {@link #before}
     */
    public void setBefore(final boolean before) {
        this.before = before;
    }

    /**
     * Getter for {@link #before}.
     *
     * @return {@link #before}
     */
    public boolean isBefore() {
        return before;
    }

    /**
     * Creates and returns SQL for creation of trigger.
     *
     * @param quoteNames whether names should be quoted
     *
     * @return created SQL
     */
    public String getCreationSQL(final boolean quoteNames) {
        final StringBuilder sbDDL = new StringBuilder();
        sbDDL.append("CREATE TRIGGER ");
        sbDDL.append(PgDiffUtils.getQuotedName(getName(), quoteNames));
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
        sbDDL.append(PgDiffUtils.getQuotedName(getTableName(), quoteNames));
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
     * @param quoteNames whether names should be quoted
     *
     * @return created SQL
     */
    public String getDropSQL(final boolean quoteNames) {
        return "DROP TRIGGER "
                + PgDiffUtils.getQuotedName(getName(), quoteNames) + " ON "
                + PgDiffUtils.getQuotedName(getTableName(), quoteNames) + ";";
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
        } else if (object instanceof PgTrigger) {
            final PgTrigger trigger = (PgTrigger) object;
            equals = (before == trigger.before)
                    && (forEachRow == trigger.forEachRow)
                    && function.equals(trigger.function)
                    && name.equals(trigger.name)
                    && (onDelete == trigger.onDelete)
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
