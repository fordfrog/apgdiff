/*
 * PgSchema.java
 *
 * Created on 22. bøezen 2006, 21:03
 */
package cz.startnet.utils.pgdiff;

import java.util.HashMap;
import java.util.Map;


/**
 * Stores schema information.
 * @author fordfrog
 */
public class PgSchema {
    private Map<String, PgSequence> sequences =
        new HashMap<String, PgSequence>();
    private Map<String, PgTable> tables = new HashMap<String, PgTable>();

    /**
     * Creates a new instance of PgSchema.
     */
    public PgSchema() {
    }

    public PgSequence getSequence(String name) {
        PgSequence sequence = null;

        if (sequences.containsKey(name)) {
            sequence = sequences.get(name);
        } else {
            sequence = new PgSequence(name);
            sequences.put(name, sequence);
        }

        return sequence;
    }

    public Map<String, PgSequence> getSequences() {
        return sequences;
    }

    public PgTable getTable(String name) {
        PgTable table = null;

        if (tables.containsKey(name)) {
            table = tables.get(name);
        } else {
            table = new PgTable(name);
            tables.put(name, table);
        }

        return table;
    }

    public Map<String, PgTable> getTables() {
        return tables;
    }
}
