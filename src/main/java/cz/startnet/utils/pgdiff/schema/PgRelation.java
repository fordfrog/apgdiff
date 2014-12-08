/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Base class for tables and views.
 *
 * @author Marti Raudsepp
 */
public class PgRelation {

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
}
