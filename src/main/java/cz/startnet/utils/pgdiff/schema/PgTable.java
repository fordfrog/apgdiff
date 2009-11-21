package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;

import java.util.ArrayList;
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
    private final List<PgColumn> columns = new ArrayList<PgColumn>();
    /**
     * List of constraints defined on the table.
     */
    private final List<PgConstraint> constraints =
            new ArrayList<PgConstraint>();
    /**
     * List of indexes defined on the table.
     */
    private final List<PgIndex> indexes = new ArrayList<PgIndex>();
    /**
     * List of triggers defined on the table.
     */
    private final List<PgTrigger> triggers = new ArrayList<PgTrigger>();
    /**
     * Name of the index on which the table is clustered
     */
    private String clusterIndexName;
    /**
     * Definition of names of inherited tables.
     */
    private String inherits;
    /**
     * Name of the table.
     */
    private String name;
    /**
     * Whether WITH OIDS is used.
     */
    private boolean withOIDS;

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
     * Finds column according to specified column <code>name</code>.
     *
     * @param name name of the column to be searched
     *
     * @return found column or null if no such column has been found
     */
    public PgColumn getColumn(final String name) {
        PgColumn column = null;

        for (PgColumn curColumn : columns) {
            if (curColumn.getName().equals(name)) {
                column = curColumn;

                break;
            }
        }

        return column;
    }

    /**
     * Getter for {@link #columns}.
     *
     * @return {@link #columns}
     */
    public List<PgColumn> getColumns() {
        return columns;
    }

    /**
     * Finds constraint according to specified constraint
     * <code>name</code>.
     *
     * @param name name of the constraint to be searched
     *
     * @return found constraint or null if no such constraint has been found
     */
    public PgConstraint getConstraint(final String name) {
        PgConstraint constraint = null;

        for (PgConstraint curConstraint : constraints) {
            if (curConstraint.getName().equals(name)) {
                constraint = curConstraint;

                break;
            }
        }

        return constraint;
    }

    /**
     * Getter for {@link #constraints}.
     *
     * @return {@link #constraints}
     */
    public List<PgConstraint> getConstraints() {
        return constraints;
    }

    /**
     * Creates and returns SQL for creation of the table.
     *
     * @param quoteNames whether names should be quoted
     *
     * @return created SQL command
     */
    public String getCreationSQL(final boolean quoteNames) {
        final StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("CREATE TABLE ");
        sbSQL.append(PgDiffUtils.getQuotedName(name, quoteNames));
        sbSQL.append(" (\n");

        for (PgColumn column : columns) {
            sbSQL.append("\t");
            sbSQL.append(column.getFullDefinition(quoteNames, false));
            sbSQL.append(",\n");
        }

        sbSQL.setLength(sbSQL.length() - 2);
        sbSQL.append("\n)");

        if ((inherits != null) && (inherits.length() > 0)) {
            sbSQL.append("\nINHERITS ");
            sbSQL.append(inherits);
        }

        sbSQL.append(';');

        for (PgColumn column : getColumnsWithStatistics()) {
            sbSQL.append("\nALTER TABLE ONLY ");
            sbSQL.append(PgDiffUtils.getQuotedName(name, quoteNames));
            sbSQL.append(" ALTER COLUMN ");
            sbSQL.append(
                    PgDiffUtils.getQuotedName(column.getName(), quoteNames));
            sbSQL.append(" SET STATISTICS ");
            sbSQL.append(column.getStatistics());
            sbSQL.append(';');
        }

        return sbSQL.toString();
    }

    /**
     * Creates and returns SQL command for dropping the table.
     *
     * @param quoteNames whether names should be quoted
     *
     * @return created SQL command
     */
    public String getDropSQL(final boolean quoteNames) {
        return "DROP TABLE " + PgDiffUtils.getQuotedName(getName(), quoteNames)
                + ";";
    }

    /**
     * Finds index according to specified index <code>name</code>.
     *
     * @param name name of the index to be searched
     *
     * @return found index or null if no such index has been found
     */
    public PgIndex getIndex(final String name) {
        PgIndex index = null;

        for (PgIndex curIndex : indexes) {
            if (curIndex.getName().equals(name)) {
                index = curIndex;

                break;
            }
        }

        return index;
    }

    /**
     * Getter for {@link #indexes}.
     *
     * @return {@link #indexes}
     */
    public List<PgIndex> getIndexes() {
        return indexes;
    }

    /**
     * Setter for {@link #inherits}.
     *
     * @param inherits {@link #inherits}
     */
    public void setInherits(final String inherits) {
        this.inherits = inherits;
    }

    /**
     * Getter for {@link #inherits}.
     *
     * @return {@link #inherits}
     */
    public String getInherits() {
        return inherits;
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
     * Getter for {@link #triggers}.
     *
     * @return {@link #triggers}
     */
    public List<PgTrigger> getTriggers() {
        return triggers;
    }

    /**
     * Setter for {@link #withOIDS}.
     *
     * @param withOIDS {@link #withOIDS}
     */
    public void setWithOIDS(final boolean withOIDS) {
        this.withOIDS = withOIDS;
    }

    /**
     * Getter for {@link #withOIDS}
     *
     * @return {@link #withOIDS}
     */
    public boolean isWithOIDS() {
        return withOIDS;
    }

    /**
     * Adds <code>column</code> to the list of columns.
     *
     * @param column column
     */
    public void addColumn(final PgColumn column) {
        columns.add(column);
    }

    /**
     * Adds <code>constraint</code> to the list of constraints.
     *
     * @param constraint constraint
     */
    public void addConstraint(final PgConstraint constraint) {
        constraints.add(constraint);
    }

    /**
     * Adds <code>index</code> to the list of indexes.
     *
     * @param index index
     */
    public void addIndex(final PgIndex index) {
        indexes.add(index);
    }

    /**
     * Adds <code>trigger</code> to the list of triggers.
     *
     * @param trigger trigger
     */
    public void addTrigger(final PgTrigger trigger) {
        triggers.add(trigger);
    }

    /**
     * Returns true if table contains given column <code>name</code>,
     * otherwise false.
     *
     * @param name name of the column
     *
     * @return true if table contains given column <code>name</code>, otherwise
     *         false
     */
    public boolean containsColumn(final String name) {
        boolean found = false;

        for (PgColumn column : columns) {
            if (column.getName().equals(name)) {
                found = true;

                break;
            }
        }

        return found;
    }

    /**
     * Returns true if table contains given constraint
     * <code>name</code>, otherwise false.
     *
     * @param name name of the constraint
     *
     * @return true if table contains given constraint <code>name</code>,
     *         otherwise false
     */
    public boolean containsConstraint(final String name) {
        boolean found = false;

        for (PgConstraint constraint : constraints) {
            if (constraint.getName().equals(name)) {
                found = true;

                break;
            }
        }

        return found;
    }

    /**
     * Returns true if table contains given index <code>name</code>,
     * otherwise false.
     *
     * @param name name of the index
     *
     * @return true if table contains given index <code>name</code>, otherwise
     *         false
     */
    public boolean containsIndex(final String name) {
        boolean found = false;

        for (PgIndex index : indexes) {
            if (index.getName().equals(name)) {
                found = true;

                break;
            }
        }

        return found;
    }

    /**
     * Returns list of columns that have statistics defined.
     *
     * @return list of columns that have statistics defined
     */
    private List<PgColumn> getColumnsWithStatistics() {
        final List<PgColumn> list = new ArrayList<PgColumn>();

        for (PgColumn column : columns) {
            if (column.getStatistics() != null) {
                list.add(column);
            }
        }

        return list;
    }
}
