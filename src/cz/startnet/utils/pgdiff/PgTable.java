/*
 * PgTable.java
 *
 * Created on 23. bøezen 2006, 9:48
 */
package cz.startnet.utils.pgdiff;

import java.util.HashMap;
import java.util.Map;


/**
 * Stores table information.
 * @author fordfrog
 */
public class PgTable {
    private Map<String, PgColumn> columns = new HashMap<String, PgColumn>();
    private Map<String, PgConstraint> constraints =
        new HashMap<String, PgConstraint>();
    private Map<String, PgIndex> indexes = new HashMap<String, PgIndex>();
    private String name = null;

    /**
     * Creates a new instance of PgTable.
     */
    public PgTable() {
    }

    public PgTable(String name) {
        this.name = name;
    }

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

    public Map<String, PgColumn> getColumns() {
        return columns;
    }

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

    public Map<String, PgConstraint> getConstraints() {
        return constraints;
    }

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

    public Map<String, PgIndex> getIndexes() {
        return indexes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Creates table creation SQL.
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
