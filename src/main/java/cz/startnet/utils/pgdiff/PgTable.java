/*
 * $CVSHeader$
 */
package cz.startnet.utils.pgdiff;

import java.util.HashMap;
import java.util.Map;


/**
 * Stores table information.
 *
 * @author fordfrog
 * @version $CVSHeader$
 */
public class PgTable {
    /**
     * Map of column names and columns.
     */
    private Map<String, PgColumn> columns = new HashMap<String, PgColumn>();

    /**
     * Map of constraint names and constraints.
     */
    private Map<String, PgConstraint> constraints =
        new HashMap<String, PgConstraint>();

    /**
     * Map of index names and indexes.
     */
    private Map<String, PgIndex> indexes = new HashMap<String, PgIndex>();

    /**
     * Name of the table.
     */
    private String name = null;

    /**
     * Creates a new instance of PgTable.
     */
    public PgTable() {
    }

    /**
     * Creates a new PgTable object.
     *
     * @param name name of the table
     */
    public PgTable(String name) {
        this.name = name;
    }

    /**
     * Returns column with given name. If the column exists in the
     * {@link #columns columns} then the existing column is returned otherwise
     * new column is created.
     *
     * @param name name of the column
     *
     * @return existing or new column
     */
    public PgColumn getColumn(String name) {
        PgColumn column = null;

        if (columns.containsKey(name)) {
            column = columns.get(name);
        } else {
            column = new PgColumn(name);
            columns.put(name, column);
        }

        return column;
    }

    /**
     * Returns map of all columns.
     *
     * @return map of all columns
     */
    public Map<String, PgColumn> getColumns() {
        return columns;
    }

    /**
     * Returns constraint with given name. If the constraint exists in
     * the {@link #constraints constraints} then the existing constraint is
     * returned otherwise new constraint is created.
     *
     * @param name name of the constraint
     *
     * @return existing or new constraint
     */
    public PgConstraint getConstraint(String name) {
        PgConstraint constraint = null;

        if (constraints.containsKey(name)) {
            constraint = constraints.get(name);
        } else {
            constraint = new PgConstraint(name);
            constraints.put(name, constraint);
        }

        return constraint;
    }

    /**
     * Returns map of all constraints.
     *
     * @return map of all constraints
     */
    public Map<String, PgConstraint> getConstraints() {
        return constraints;
    }

    /**
     * Returns index with given name. If the index exists in the {@link
     * #indexes indexes} then the existing index is returned otherwise new
     * index is created.
     *
     * @param name name of the index
     *
     * @return existing or new index
     */
    public PgIndex getIndex(String name) {
        PgIndex index = null;

        if (indexes.containsKey(name)) {
            index = indexes.get(name);
        } else {
            index = new PgIndex(name);
            indexes.put(name, index);
        }

        return index;
    }

    /**
     * Returns map of all indexes.
     *
     * @return map of all indexes
     */
    public Map<String, PgIndex> getIndexes() {
        return indexes;
    }

    /**
     * Setter for {@link #name name}.
     *
     * @param name {@link #name name}
     */
    public void setName(String name) {
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
     * Creates table creation SQL.
     *
     * @return SQL for creation of the table
     */
    public String getTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE " + name + " (\n");

        for (PgColumn column : columns.values()) {
            sb.append("\t" + column.getFullDefinition() + ",\n");
        }

        sb.setLength(sb.length() - 2);
        sb.append("\n);");

        return sb.toString();
    }
}
