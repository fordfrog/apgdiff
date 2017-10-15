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

/**
 * Base class for tables and views.
 *
 * @author Marti Raudsepp
 */
public abstract class PgRelation {

    /**
     * List of columns defined on the relation.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    protected final List<PgColumn> columns = new ArrayList<PgColumn>();
    /**
     * List of indexes defined on the relation.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgIndex> indexes = new ArrayList<PgIndex>();
    /**
     * List of triggers defined on the table/view.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgTrigger> triggers = new ArrayList<PgTrigger>();
    /**
     * Name of the index on which the table/matview is clustered
     */
    private String clusterIndexName;
    /**
     * Name of the relation.
     */
    protected String name;
    /**
     * Tablespace value.
     */
    protected String tablespace;
    /**
     * Comment.
     */
    String comment;

    /**
     * List of privileges defined on the table.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgRelationPrivilege> privileges = new ArrayList<PgRelationPrivilege>();
    /**
     * Column the table is owner to.
     */
    private String ownerTo;

    /**
     * Setter for {@link #clusterIndexName}.
     *
     * @param name {@link #clusterIndexName}
     */
    public void setClusterIndexName(final String name) {
        clusterIndexName = name;
    }

    /**
     * Getter for {@link #clusterIndexName}.
     *
     * @return {@link #clusterIndexName}
     */
    public String getClusterIndexName() {
        return clusterIndexName;
    }

    /**
     * Finds column according to specified column {@code name}.
     *
     * @param name name of the column to be searched
     *
     * @return found column or null if no such column has been found
     */
    public PgColumn getColumn(final String name) {
        for (PgColumn column : columns) {
            if (column.getName().equals(name)) {
                return column;
            }
        }

        return null;
    }

    /**
     * Getter for {@link #columns}. The list cannot be modified.
     *
     * @return {@link #columns}
     */
    public List<PgColumn> getColumns() {
        return Collections.unmodifiableList(columns);
    }

    /**
     * Generates SQL code for declaring relation and column comments
     *
     * @return SQL code for declaring relation and column comments
     */
    protected String getCommentDefinitionSQL() {
        final StringBuilder sbSQL = new StringBuilder(100);

        if (comment != null && !comment.isEmpty()) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("COMMENT ON ");
            sbSQL.append(getRelationKind());
            sbSQL.append(' ');
            sbSQL.append(PgDiffUtils.getQuotedName(name));
            sbSQL.append(" IS ");
            sbSQL.append(comment);
            sbSQL.append(';');
        }

        for (final PgColumn column : columns) {
            if (column.getComment() != null && !column.getComment().isEmpty()) {
                sbSQL.append(System.getProperty("line.separator"));
                sbSQL.append(System.getProperty("line.separator"));
                sbSQL.append("COMMENT ON COLUMN ");
                sbSQL.append(PgDiffUtils.getQuotedName(name));
                sbSQL.append('.');
                sbSQL.append(PgDiffUtils.getQuotedName(column.getName()));
                sbSQL.append(" IS ");
                sbSQL.append(column.getComment());
                sbSQL.append(';');
            }
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
     * Finds index according to specified index {@code name}.
     *
     * @param name name of the index to be searched
     *
     * @return found index or null if no such index has been found
     */
    public PgIndex getIndex(final String name) {
        for (PgIndex index : indexes) {
            if (index.getName().equals(name)) {
                return index;
            }
        }

        return null;
    }

    /**
     * Finds trigger according to specified trigger {@code name}.
     *
     * @param name name of the trigger to be searched
     *
     * @return found trigger or null if no such trigger has been found
     */
    public PgTrigger getTrigger(final String name) {
        for (PgTrigger trigger : triggers) {
            if (trigger.getName().equals(name)) {
                return trigger;
            }
        }

        return null;
    }

    /**
     * Getter for {@link #indexes}. The list cannot be modified.
     *
     * @return {@link #indexes}
     */
    public List<PgIndex> getIndexes() {
        return Collections.unmodifiableList(indexes);
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
     * Getter for {@link #triggers}. The list cannot be modified.
     *
     * @return {@link #triggers}
     */
    public List<PgTrigger> getTriggers() {
        return Collections.unmodifiableList(triggers);
    }

    /**
     * Getter for {@link #tablespace}.
     *
     * @return {@link #tablespace}
     */
    public String getTablespace() {
        return tablespace;
    }

    /**
     * Setter for {@link #tablespace}.
     *
     * @param tablespace {@link #tablespace}
     */
    public void setTablespace(final String tablespace) {
        this.tablespace = tablespace;
    }

    /**
     * Adds {@code column} to the list of columns.
     *
     * @param column column
     */
    public void addColumn(final PgColumn column) {
        columns.add(column);
    }

    /**
     * Adds {@code index} to the list of indexes.
     *
     * @param index index
     */
    public void addIndex(final PgIndex index) {
        indexes.add(index);
    }

    /**
     * Adds {@code trigger} to the list of triggers.
     *
     * @param trigger trigger
     */
    public void addTrigger(final PgTrigger trigger) {
        triggers.add(trigger);
    }

    /**
     * Returns relation kind for CREATE/ALTER/DROP commands.
     *
     * @return relation kind
     */
    public abstract String getRelationKind();

    /**
     * Creates and returns SQL statement for dropping the relation.
     *
     * @return created SQL statement
     */
    public String getDropSQL() {
        return "DROP " + getRelationKind() + " " + PgDiffUtils.getDropIfExists()+
                PgDiffUtils.getQuotedName(getName()) + ";";
}

    /**
     * Returns true if table contains given column {@code name}, otherwise
     * false.
     *
     * @param name name of the column
     *
     * @return true if table contains given column {@code name}, otherwise false
     */
    public boolean containsColumn(final String name) {
        for (PgColumn column : columns) {
            if (column.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Finds inheritedColumn according to specified name {@code name}.
     *
     * @param name name of the inheritedColumn to be searched
     *
     * @return found inheritedColumn or null if no such inheritedColumn
     * has been found
     */
    public PgInheritedColumn getInheritedColumn(final String name) {
        return null;
    }

    /**
     * Returns true if table contains given inheritedColumn {@code name},
     * otherwise false.
     *
     * @param name name of the inheritedColumn
     *
     * @return true if table contains given inheritedColumn {@code name},
     * otherwise false
     */
    public boolean containsInheritedColumn(final String name) {
        return false;
    }

    /**
     * Returns true if table/matview contains given index {@code name}, otherwise false.
     *
     * @param name name of the index
     *
     * @return true if table/matview contains given index {@code name}, otherwise false
     */
    public boolean containsIndex(final String name) {
        for (PgIndex index : indexes) {
            if (index.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    public List<PgRelationPrivilege> getPrivileges() {
        return Collections.unmodifiableList(privileges);
    }

    /**
     * Getter for {@link #ownerTo}.
     *
     * @return {@link #ownerTo}
     */
    public String getOwnerTo() {
        return ownerTo;
    }

    /**
     * Setter for {@link #ownerTo}.
     *
     * @param ownerTo
     *            {@link #ownerTo}
     */
    public void setOwnerTo(final String ownerTo) {
        this.ownerTo = ownerTo;
    }


    public void addPrivilege(final PgRelationPrivilege privilege) {
        privileges.add(privilege);
    }


    public PgRelationPrivilege getPrivilege(final String roleName) {
        for (PgRelationPrivilege privilege : privileges) {
            if (privilege.getRoleName().equals(roleName)) {
                return privilege;
            }
        }
        return null;
    }
}
