package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;
import cz.startnet.utils.pgdiff.schema.PgTrigger;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.List;

/**
 * Diffs triggers.
 *
 * @author fordfrog
 */
public class PgDiffTriggers {

    /**
     * Creates a new instance of PgDiffTriggers.
     */
    private PgDiffTriggers() {
        super();
    }

    /**
     * Outputs commands for creation of new triggers.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void createTriggers(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema) {
        for (final PgTable newTable : newSchema.getTables()) {
            final PgTable oldTable;

            if (oldSchema == null) {
                oldTable = null;
            } else {
                oldTable = oldSchema.getTable(newTable.getName());
            }

            // Add new triggers
            for (final PgTrigger trigger : getNewTriggers(oldTable, newTable)) {
                writer.println();
                writer.println(
                        trigger.getCreationSQL(arguments.isQuoteNames()));
            }
        }
    }

    /**
     * Outputs commands for dropping triggers.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     */
    public static void dropTriggers(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema) {
        for (final PgTable newTable : newSchema.getTables()) {
            final PgTable oldTable;

            if (oldSchema == null) {
                oldTable = null;
            } else {
                oldTable = oldSchema.getTable(newTable.getName());
            }

            // Drop triggers that no more exist or are modified
            for (final PgTrigger trigger :
                    getDropTriggers(oldTable, newTable)) {
                writer.println();
                writer.println(trigger.getDropSQL(arguments.isQuoteNames()));
            }
        }
    }

    /**
     * Returns list of triggers that should be dropped.
     *
     * @param oldTable original table
     * @param newTable new table
     *
     * @return list of triggers that should be dropped
     */
    private static List<PgTrigger> getDropTriggers(final PgTable oldTable,
            final PgTable newTable) {
        final List<PgTrigger> list = new ArrayList<PgTrigger>();

        if ((newTable != null) && (oldTable != null)) {
            final List<PgTrigger> newTriggers = newTable.getTriggers();

            for (final PgTrigger oldTrigger : oldTable.getTriggers()) {
                if (!newTriggers.contains(oldTrigger)) {
                    list.add(oldTrigger);
                }
            }
        }

        return list;
    }

    /**
     * Returns list of triggers that should be added.
     *
     * @param oldTable original table
     * @param newTable new table
     *
     * @return list of triggers that should be added
     */
    private static List<PgTrigger> getNewTriggers(final PgTable oldTable,
            final PgTable newTable) {
        final List<PgTrigger> list = new ArrayList<PgTrigger>();

        if (newTable != null) {
            if (oldTable == null) {
                list.addAll(newTable.getTriggers());
            } else {
                for (final PgTrigger newTrigger : newTable.getTriggers()) {
                    if (!oldTable.getTriggers().contains(newTrigger)) {
                        list.add(newTrigger);
                    }
                }
            }
        }

        return list;
    }
}
