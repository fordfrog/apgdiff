/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.schema;

import java.util.ArrayList;
import java.util.List;


/**
 * Stores schema information.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgSchema {
    /**
     * List of functions defined in the schema.
     */
    private final List<PgFunction> functions = new ArrayList<PgFunction>();

    /**
     * List of sequences defined in the schema.
     */
    private final List<PgSequence> sequences = new ArrayList<PgSequence>();

    /**
     * List of tables defined in the schema.
     */
    private final List<PgTable> tables = new ArrayList<PgTable>();

    /**
     * Finds function according to specified function
     * <code>declaration</code>.
     *
     * @param declaration declaration of the function to be searched
     *
     * @return found function or null if no such function has been found
     */
    public PgFunction getFunction(final String declaration) {
        PgFunction function = null;

        for (PgFunction curFunction : functions) {
            if (curFunction.getDeclaration().equals(declaration)) {
                function = curFunction;

                break;
            }
        }

        return function;
    }

    /**
     * Getter for {@link #functions functions}.
     *
     * @return {@link #functions functions}
     */
    public List<PgFunction> getFunctions() {
        return functions;
    }

    /**
     * Finds sequence according to specified sequence
     * <code>name</code>.
     *
     * @param name name of the sequence to be searched
     *
     * @return found sequence or null if no such sequence has been found
     */
    public PgSequence getSequence(final String name) {
        PgSequence sequence = null;

        for (PgSequence curSequence : sequences) {
            if (curSequence.getName().equals(name)) {
                sequence = curSequence;

                break;
            }
        }

        return sequence;
    }

    /**
     * Getter for {@link #sequences sequences}.
     *
     * @return {@link #sequences sequences}
     */
    public List<PgSequence> getSequences() {
        return sequences;
    }

    /**
     * Finds table according to specified table <code>name</code>.
     *
     * @param name name of the table to be searched
     *
     * @return found table or null if no such table has been found
     */
    public PgTable getTable(final String name) {
        PgTable table = null;

        for (PgTable curTable : tables) {
            if (curTable.getName().equals(name)) {
                table = curTable;

                break;
            }
        }

        return table;
    }

    /**
     * Getter for {@link #tables tables}.
     *
     * @return {@link #tables tables}
     */
    public List<PgTable> getTables() {
        return tables;
    }

    /**
     * Adds <code>function</code> to the list of functions.
     *
     * @param function function
     */
    public void addFunction(final PgFunction function) {
        functions.add(function);
    }

    /**
     * Adds <code>sequence</code> to the list of sequences.
     *
     * @param sequence sequence
     */
    public void addSequence(final PgSequence sequence) {
        sequences.add(sequence);
    }

    /**
     * Adds <code>table</code> to the list of tables.
     *
     * @param table table
     */
    public void addTable(final PgTable table) {
        tables.add(table);
    }

    /**
     * Returns true if schema contains function with given
     * <code>declaration</code>, otherwise false.
     *
     * @param declaration declaration of the function
     *
     * @return true if schema contains function with given
     *         <code>declaration</code>, otherwise false
     */
    public boolean containsFunction(final String declaration) {
        boolean found = false;

        for (PgFunction function : functions) {
            if (function.getDeclaration().equals(declaration)) {
                found = true;

                break;
            }
        }

        return found;
    }

    /**
     * Returns true if schema contains sequence with given
     * <code>name</code>, otherwise false.
     *
     * @param name name of the sequence
     *
     * @return true if schema contains sequence with given <code>name</code>,
     *         otherwise false
     */
    public boolean containsSequence(final String name) {
        boolean found = false;

        for (PgSequence sequence : sequences) {
            if (sequence.getName().equals(name)) {
                found = true;

                break;
            }
        }

        return found;
    }

    /**
     * Returns true if schema contains table with given
     * <code>name</code>, otherwise false.
     *
     * @param name name of the table
     *
     * @return true if schema contains table with given <code>name</code>,
     *         otherwise false.
     */
    public boolean containsTable(final String name) {
        boolean found = false;

        for (PgTable table : tables) {
            if (table.getName().equals(name)) {
                found = true;

                break;
            }
        }

        return found;
    }
}
