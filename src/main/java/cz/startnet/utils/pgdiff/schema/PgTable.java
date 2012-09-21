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
 * Stores table information.
 *
 * @author fordfrog
 */
public class PgTable {

    /**
     * List of columns defined on the table.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgColumn> columns = new ArrayList<PgColumn>();
    /**
     * List of constraints defined on the table.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgConstraint> constraints =
            new ArrayList<PgConstraint>();
    /**
     * List of indexes defined on the table.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgIndex> indexes = new ArrayList<PgIndex>();
    /**
     * List of triggers defined on the table.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgTrigger> triggers = new ArrayList<PgTrigger>();
    /**
     * Name of the index on which the table is clustered
     */
    private String clusterIndexName;
    /**
     * List of names of inherited tables.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<String> inherits = new ArrayList<String>();
    /**
     * Name of the table.
     */
    private String name;
    /**
     * WITH clause. If value is null then it is not set, otherwise can be set to
     * OIDS=true, OIDS=false, or storage parameters can be set.
     */
    private String with;
    /**
     * Tablespace value.
     */
    private String tablespace;
    /**
     * Comment.
     */
    private String comment;

    /**
     * Creates a new PgTable object.
     *
     * @param name {@link #name}
     */
    public PgTable(final String name) {
        this.name = name;
    }

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
     * Finds constraint according to specified constraint {@code name}.
     *
     * @param name name of the constraint to be searched
     *
     * @return found constraint or null if no such constraint has been found
     */
    public PgConstraint getConstraint(final String name) {
        for (PgConstraint constraint : constraints) {
            if (constraint.getName().equals(name)) {
                return constraint;
            }
        }

        return null;
    }

    /**
     * Getter for {@link #constraints}. The list cannot be modified.
     *
     * @return {@link #constraints}
     */
    public List<PgConstraint> getConstraints() {
        return Collections.unmodifiableList(constraints);
    }

    /**
     * Creates and returns SQL for creation of the table.
     *
     * @return created SQL statement
     */
    public String getCreationSQL() {
        final StringBuilder sbSQL = new StringBuilder(1000);
        sbSQL.append("CREATE TABLE ");
        sbSQL.append(PgDiffUtils.getQuotedName(name));
        sbSQL.append(" (\n");

        boolean first = true;

        if (columns.isEmpty()) {
            sbSQL.append(')');
        } else {
            for (PgColumn column : columns) {
                if (first) {
                    first = false;
                } else {
                    sbSQL.append(",\n");
                }

                sbSQL.append("\t");
                sbSQL.append(column.getFullDefinition(false));
            }

            sbSQL.append("\n)");
        }

        if (inherits != null && !inherits.isEmpty()) {
            sbSQL.append("\nINHERITS (");

            first = true;

            for (final String tableName : inherits) {
                if (first) {
                    first = false;
                } else {
                    sbSQL.append(", ");
                }

                sbSQL.append(tableName);
            }

            sbSQL.append(")");
        }

        if (with != null && !with.isEmpty()) {
            sbSQL.append("\n");

            if ("OIDS=false".equalsIgnoreCase(with)) {
                sbSQL.append("WITHOUT OIDS");
            } else {
                sbSQL.append("WITH ");

                if ("OIDS".equalsIgnoreCase(with)
                        || "OIDS=true".equalsIgnoreCase(with)) {
                    sbSQL.append("OIDS");
                } else {
                    sbSQL.append(with);
                }
            }
        }

        if (tablespace != null && !tablespace.isEmpty()) {
            sbSQL.append("\nTABLESPACE ");
            sbSQL.append(tablespace);
        }

        sbSQL.append(';');

        for (PgColumn column : getColumnsWithStatistics()) {
            sbSQL.append("\nALTER TABLE ONLY ");
            sbSQL.append(PgDiffUtils.getQuotedName(name));
            sbSQL.append(" ALTER COLUMN ");
            sbSQL.append(
                    PgDiffUtils.getQuotedName(column.getName()));
            sbSQL.append(" SET STATISTICS ");
            sbSQL.append(column.getStatistics());
            sbSQL.append(';');
        }

        if (comment != null && !comment.isEmpty()) {
            sbSQL.append("\n\nCOMMENT ON TABLE ");
            sbSQL.append(PgDiffUtils.getQuotedName(name));
            sbSQL.append(" IS ");
            sbSQL.append(comment);
            sbSQL.append(';');
        }

        for (final PgColumn column : columns) {
            if (column.getComment() != null && !column.getComment().isEmpty()) {
                sbSQL.append("\n\nCOMMENT ON COLUMN ");
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
     * Creates and returns SQL statement for dropping the table.
     *
     * @return created SQL statement
     */
    public String getDropSQL() {
        return "DROP TABLE " + PgDiffUtils.getQuotedName(getName()) + ";";
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
     * Setter for {@link #inherits}.
     *
     * @param tableName name of inherited table
     */
    public void addInherits(final String tableName) {
        inherits.add(tableName);
    }

    /**
     * Getter for {@link #inherits}.
     *
     * @return {@link #inherits}
     */
    public List<String> getInherits() {
        return Collections.unmodifiableList(inherits);
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
     * Setter for {@link #with}.
     *
     * @param with {@link #with}
     */
    public void setWith(final String with) {
        this.with = with;
    }

    /**
     * Getter for {@link #with}
     *
     * @return {@link #with}
     */
    public String getWith() {
        return with;
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
     * Adds {@code constraint} to the list of constraints.
     *
     * @param constraint constraint
     */
    public void addConstraint(final PgConstraint constraint) {
        constraints.add(constraint);
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
     * Returns true if table contains given constraint {@code name}, otherwise
     * false.
     *
     * @param name name of the constraint
     *
     * @return true if table contains given constraint {@code name}, otherwise
     *         false
     */
    public boolean containsConstraint(final String name) {
        for (PgConstraint constraint : constraints) {
            if (constraint.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if table contains given index {@code name}, otherwise false.
     *
     * @param name name of the index
     *
     * @return true if table contains given index {@code name}, otherwise false
     */
    public boolean containsIndex(final String name) {
        for (PgIndex index : indexes) {
            if (index.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns list of columns that have statistics defined.
     *
     * @return list of columns that have statistics defined
     */
    private List<PgColumn> getColumnsWithStatistics() {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgColumn> list = new ArrayList<PgColumn>();

        for (PgColumn column : columns) {
            if (column.getStatistics() != null) {
                list.add(column);
            }
        }

        return list;
    }
}
