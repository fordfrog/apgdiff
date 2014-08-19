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
public class PgType {

    /**
     * List of columns defined on the table.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgColumn> columns = new ArrayList<>();
    private final List<String> enumValues = new ArrayList<>();

    /**
     * Name of the table.
     */
    private String name;

    private Boolean isEnum = false;

    /**
     * Creates a new PgTable object.
     *
     * @param name {@link #name}
     */
    public PgType(final String name) {
        this.name = name;
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
     * Creates and returns SQL for creation of the table.
     *
     * @return created SQL statement
     */
    public String getCreationSQL() {
        final StringBuilder sbSQL = new StringBuilder(1000);
        sbSQL.append("CREATE TYPE ");
        sbSQL.append(PgDiffUtils.getQuotedName(name));
        if (isEnum) {
            sbSQL.append(" AS ENUM (\n");
        } else {
            sbSQL.append(" AS (\n");
        }

        boolean first = true;
        if (isEnum) {
            for (String enumValue : enumValues) {
                if (first) {
                    first = false;
                } else {
                    sbSQL.append(",\n");
                }
                sbSQL.append("\t");
                sbSQL.append(enumValue);
            }
            sbSQL.append("\n)");
        } else {
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
        }

        sbSQL.append(';');

        for (PgColumn column : getColumnsWithStatistics()) {
            sbSQL.append("\nALTER TABLE ONLY ");
            sbSQL.append(PgDiffUtils.getQuotedName(name));
            sbSQL.append(" ALTER COLUMN ");
            sbSQL.append(
                    PgDiffUtils.getQuotedName(column.getName()));
            sbSQL.append(';');
        }

        return sbSQL.toString();
    }

    /**
     * Creates and returns SQL statement for dropping the table.
     *
     * @return created SQL statement
     */
    public String getDropSQL() {
        return "DROP TYPE " + PgDiffUtils.getQuotedName(getName()) + ";";
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
     * Adds {@code column} to the list of columns.
     *
     * @param column column
     */
    public void addColumn(final PgColumn column) {
        columns.add(column);
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
     * Returns list of columns that have statistics defined.
     *
     * @return list of columns that have statistics defined
     */
    private List<PgColumn> getColumnsWithStatistics() {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgColumn> list = new ArrayList<>();

        for (PgColumn column : columns) {
            if (column.getStatistics() != null) {
                list.add(column);
            }
        }

        return list;
    }

    public void setIsEnum(boolean value) {
        isEnum = value;
    }

    public boolean getIsEnum() {
        return isEnum;
    }

    public void addEnumValue(String value) {
        enumValues.add(value);
    }
}
