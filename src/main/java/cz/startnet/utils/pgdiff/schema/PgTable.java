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
public class PgTable extends PgRelation {

    /**
     * List of constraints defined on the table.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgConstraint> constraints =
            new ArrayList<PgConstraint>();
    /**
     * List of names of inherited tables.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<String> inherits = new ArrayList<String>();
    /**
     * WITH clause. If value is null then it is not set, otherwise can be set to
     * OIDS=true, OIDS=false, or storage parameters can be set.
     */
    private String with;

    /**
     * Creates a new PgTable object.
     *
     * @param name {@link #name}
     */
    public PgTable(final String name) {
        setName(name);
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
     * Adds {@code constraint} to the list of constraints.
     *
     * @param constraint constraint
     */
    public void addConstraint(final PgConstraint constraint) {
        constraints.add(constraint);
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
