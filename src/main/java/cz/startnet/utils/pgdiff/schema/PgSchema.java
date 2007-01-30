/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Stores schema information.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgSchema {
    /**
     * List of ordered sequences.
     */
    private final List<PgSequence> orderedSequences =
        new ArrayList<PgSequence>();

    /**
     * list of ordered tables.
     */
    private final List<PgTable> orderedTables = new ArrayList<PgTable>();

    /**
     * Map of sequence names and sequence definitions.
     */
    private final Map<String, PgSequence> sequences = //NOPMD
        new HashMap<String, PgSequence>();

    /**
     * Map of table names and table definitions.
     */
    private final Map<String, PgTable> tables = new HashMap<String, PgTable>(); //NOPMD

    /**
     * Returns a collection of all sequences ordered as specified in
     * the DDL.
     *
     * @return collection of all the sequences
     */
    public List<PgSequence> getOrderedSequences() {
        return orderedSequences;
    }

    /**
     * Returns a collection of all tables ordered as specified in the
     * DDL.
     *
     * @return collection of all the tables
     */
    public List<PgTable> getOrderedTables() {
        return orderedTables;
    }

    /**
     * Returns sequence with given name. If the sequence exists in the
     * {@link #sequences sequences} then the existing sequence is returned
     * otherwise new sequence is created.
     *
     * @param name name of the sequence
     *
     * @return existing or new sequence
     */
    public PgSequence getSequence(final String name) {
        PgSequence sequence = null;

        if (sequences.containsKey(name)) {
            sequence = sequences.get(name);
        } else {
            sequence = new PgSequence(name);
            sequences.put(name, sequence);
            getOrderedSequences().add(sequence);
        }

        return sequence;
    }

    /**
     * Returns map of all sequences.
     *
     * @return map of all sequences
     */
    public Map<String, PgSequence> getSequences() {
        return sequences;
    }

    /**
     * Returns table with given name. If the table exists in the {@link
     * #tables tables} then the existing table is returned otherwise new table
     * is created.
     *
     * @param name name of the table
     *
     * @return existing or new table
     */
    public PgTable getTable(final String name) {
        PgTable table = null;

        if (tables.containsKey(name)) {
            table = tables.get(name);
        } else {
            table = new PgTable(name);
            tables.put(name, table);
            getOrderedTables().add(table);
        }

        return table;
    }

    /**
     * Returns map of all tables.
     *
     * @return map of all tables
     */
    public Map<String, PgTable> getTables() {
        return tables;
    }

    /**
     * Returns true if schema contains table definition, otherwise
     * false.
     *
     * @param name name of the table
     *
     * @return true if schema contains table definition, otherwise false
     */
    public boolean containsTable(final String name) {
        return tables.containsKey(name);
    }
}
