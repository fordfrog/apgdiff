/*
 * $CVSHeader$
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgConstraint;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Diffs constraints.
 *
 * @author fordfrog
 * @version $CVSHeader$
 */
public class PgDiffConstraints {
    /**
     * Creates a new instance of PgDiffConstraints.
     */
    private PgDiffConstraints() {
        super();
    }

    /**
     * Outputs commands for differences in constraints.
     *
     * @param writer writer the output should be written to
     * @param oldSchema original schema
     * @param newSchema new schema
     * @param primaryKey determines whether primery keys should be processed or
     *        any other constraints should be processed
     */
    public static void diffConstraints(
        final PrintWriter writer,
        final PgSchema oldSchema,
        final PgSchema newSchema,
        final boolean primaryKey) {
        final Map<String, PgTable> oldTables = oldSchema.getTables();

        for (PgTable newTable : newSchema.getTables().values()) {
            final String newTableName = newTable.getName();
            final PgTable oldTable = oldTables.get(newTableName);

            // Drop constraints that do not exist in new schema or are modified
            for (PgConstraint constraint : getDropConstraints(
                        oldTables.get(newTableName),
                        newTable,
                        primaryKey)) {
                writer.println();
                writer.println("ALTER TABLE " + newTableName);
                writer.println(
                        "\tDROP CONSTRAINT " + constraint.getName() + ";");
            }

            // Add new constraints
            for (PgConstraint constraint : getNewConstraints(
                        oldTables.get(newTableName),
                        newTable,
                        primaryKey)) {
                writer.println();
                writer.println("ALTER TABLE " + newTableName);
                writer.println(
                        "\tADD CONSTRAINT " + constraint.getName() + " "
                        + constraint.getDefinition() + ";");
            }
        }
    }

    /**
     * Returns list of constraints that should be dropped.
     *
     * @param oldTable original table or null
     * @param newTable new table or null
     * @param primaryKey determines whether primery keys should be processed or
     *        any other constraints should be processed
     *
     * @return list of constraints that should be dropped
     *
     * @todo Constraints that are dependent on a removed field should not be
     *       added to drop because they are already removed.
     */
    private static List<PgConstraint> getDropConstraints(
        final PgTable oldTable,
        final PgTable newTable,
        final boolean primaryKey) {
        final List<PgConstraint> list = new ArrayList<PgConstraint>();

        if ((newTable != null) && (oldTable != null)) {
            final Set<String> newNames = newTable.getConstraints().keySet();

            for (final PgConstraint constraint : oldTable.getConstraints()
                                                         .values()) {
                if (
                    (constraint.isPrimaryKeyConstraint() == primaryKey)
                        && (!newNames.contains(constraint.getName())
                        || !newTable.getConstraint(constraint.getName())
                                        .getDefinition().equals(
                                constraint.getDefinition()))) {
                    list.add(constraint);
                }
            }
        }

        return list;
    }

    /**
     * Returns list of constraints that should be added.
     *
     * @param oldTable original table
     * @param newTable new table
     * @param primaryKey determines whether primery keys should be processed or
     *        any other constraints should be processed
     *
     * @return list of constraints that should be added
     */
    private static List<PgConstraint> getNewConstraints(
        final PgTable oldTable,
        final PgTable newTable,
        final boolean primaryKey) {
        final List<PgConstraint> list = new ArrayList<PgConstraint>();

        if (newTable != null) {
            if (oldTable == null) {
                for (final PgConstraint constraint : newTable.getConstraints()
                                                             .values()) {
                    if (constraint.isPrimaryKeyConstraint() == primaryKey) {
                        list.add(constraint);
                    }
                }
            } else {
                final Set<String> oldNames = oldTable.getConstraints().keySet();

                for (final PgConstraint constraint : newTable.getConstraints()
                                                             .values()) {
                    if (
                        (constraint.isPrimaryKeyConstraint() == primaryKey)
                            && (!oldNames.contains(constraint.getName())
                            || !oldTable.getConstraint(constraint.getName())
                                            .getDefinition().equals(
                                    constraint.getDefinition()))) {
                        list.add(constraint);
                    }
                }
            }
        }

        return list;
    }
}
