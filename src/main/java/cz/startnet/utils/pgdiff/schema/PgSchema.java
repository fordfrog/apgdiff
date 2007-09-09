/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;

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
     * List of views defined in the schema.
     */
    private final List<PgView> views = new ArrayList<PgView>();

    /**
     * Name of the schema.
     */
    private final String name;

    /**
     * Schema authorization.
     */
    private String authorization;

    /**
     * Creates a new PgSchema object.
     *
     * @param name {@link #name}
     */
    public PgSchema(final String name) {
        super();
        this.name = name;
    }

    /**
     * Setter for {@link #authorization}.
     *
     * @param authorization {@link #authorization}
     */
    public void setAuthorization(final String authorization) {
        this.authorization = authorization;
    }

    /**
     * Getter for {@link #authorization}.
     *
     * @return {@link #authorization}
     */
    public String getAuthorization() {
        return authorization;
    }

    /**
     * Creates and returns SQL for creation of the schema.
     *
     * @param quoteNames whether names should be quoted
     *
     * @return created SQL
     */
    public String getCreationSQL(final boolean quoteNames) {
        final StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("CREATE SCHEMA ");
        sbSQL.append(PgDiffUtils.getQuotedName(getName(), quoteNames));

        if (getAuthorization() != null) {
            sbSQL.append(" AUTHORIOZATION ");
            sbSQL.append(
                    PgDiffUtils.getQuotedName(getAuthorization(), quoteNames));
        }

        sbSQL.append(';');

        return sbSQL.toString();
    }

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
     * Getter for {@link #functions}.
     *
     * @return {@link #functions}
     */
    public List<PgFunction> getFunctions() {
        return functions;
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
     * Getter for {@link #sequences}.
     *
     * @return {@link #sequences}
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
     * Getter for {@link #tables}.
     *
     * @return {@link #tables}
     */
    public List<PgTable> getTables() {
        return tables;
    }

    /**
     * Finds view according to specified view <code>name</code>.
     *
     * @param name name of the view to be searched
     *
     * @return found view or null if no such view has been found
     */
    public PgView getView(final String name) {
        PgView view = null;

        for (PgView curView : views) {
            if (curView.getName().equals(name)) {
                view = curView;

                break;
            }
        }

        return view;
    }

    /**
     * Getter for {@link #views}.
     *
     * @return {@link #views}
     */
    public List<PgView> getViews() {
        return views;
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
     * Adds <code>view</code> to the list of views.
     *
     * @param view view
     */
    public void addView(final PgView view) {
        views.add(view);
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

    /**
     * Returns true if schema contains view with given
     * <code>name</code>, otherwise false.
     *
     * @param name name of the view
     *
     * @return true if schema contains view with given <code>name</code>,
     *         otherwise false.
     */
    public boolean containsView(final String name) {
        boolean found = false;

        for (PgView view : views) {
            if (view.getName().equals(name)) {
                found = true;

                break;
            }
        }

        return found;
    }
}
