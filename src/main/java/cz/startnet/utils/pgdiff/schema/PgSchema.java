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
 * Stores schema information.
 *
 * @author fordfrog
 */
public class PgSchema {

    /**
     * List of functions defined in the schema.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgFunction> functions = new ArrayList<PgFunction>();
    /**
     * List of sequences defined in the schema.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgSequence> sequences = new ArrayList<PgSequence>();
    /**
     * List of rels defined in the schema.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgRelation> rels = new ArrayList<PgRelation>();
    /**
     * List of types defined in the schema.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgType> types = new ArrayList<PgType>();
    /**
     * List of indexes defined in the schema.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgIndex> indexes = new ArrayList<PgIndex>();
    /**
     * List of primary keys defined in the schema.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgConstraint> primaryKeys =
            new ArrayList<PgConstraint>();
   /**
     * List of rules defined in the schema.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgRule> rules = new ArrayList<PgRule>();
    
    /**
     * Name of the schema.
     */
    private final String name;
    /**
     * Schema authorization.
     */
    private String authorization;
    /**
     * Optional definition of schema elements.
     */
    private String definition;
    /**
     * Comment.
     */
    private String comment;

    /**
     * Creates a new PgSchema object.
     *
     * @param name {@link #name}
     */
    public PgSchema(final String name) {
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
     * Getter for {@link #comment}.
     *
     * @return {@link #comment}
     */
    public String getComment() {
        return comment;
    }

    /**
     * Setter for {@link #comment}.
     *
     * @param comment {@link #comment}
     */
    public void setComment(final String comment) {
        this.comment = comment;
    }

    /**
     * Getter for {@link #definition}.
     *
     * @return {@link #definition}
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Setter for {@link #definition}.
     *
     * @param definition {@link #definition}
     */
    public void setDefinition(final String definition) {
        this.definition = definition;
    }

    /**
     * Creates and returns SQL for creation of the schema.
     *
     * @return created SQL
     */
    public String getCreationSQL() {
        final StringBuilder sbSQL = new StringBuilder(50);
        sbSQL.append("CREATE SCHEMA ");
        sbSQL.append(PgDiffUtils.getCreateIfNotExists());
        sbSQL.append(PgDiffUtils.getQuotedName(getName()));

        if (getAuthorization() != null) {
            sbSQL.append(" AUTHORIZATION ");
            sbSQL.append(PgDiffUtils.getQuotedName(getAuthorization()));
        }

        sbSQL.append(';');

        if (comment != null && !comment.isEmpty()) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("COMMENT ON SCHEMA ");
            sbSQL.append(PgDiffUtils.getQuotedName(name));
            sbSQL.append(" IS ");
            sbSQL.append(comment);
            sbSQL.append(';');
        }

        return sbSQL.toString();
    }

    /**
     * Finds function according to specified function {@code signature}.
     *
     * @param signature signature of the function to be searched
     *
     * @return found function or null if no such function has been found
     */
    public PgFunction getFunction(final String signature) {
        for (PgFunction function : functions) {
            if (function.getSignature().equals(signature)) {
                return function;
            }
        }

        return null;
    }

    /**
     * Getter for {@link #functions}. The list cannot be modified.
     *
     * @return {@link #functions}
     */
    public List<PgFunction> getFunctions() {
        return Collections.unmodifiableList(functions);
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
     * Finds index according to specified index {@code name}.
     *
     * @param name name of the index to be searched
     *
     * @return found index or null if no such index has been found
     */
    public PgIndex getIndex(final String name) {
        for (PgIndex index : indexes) {
            if (index.getName().equals(name)) {
                return index;
            }
        }

        return null;
    }

    /**
     * Finds primary key according to specified primary key {@code name}.
     *
     * @param name name of the primary key to be searched
     *
     * @return found primary key or null if no such primary key has been found
     */
    public PgConstraint getPrimaryKey(final String name) {
        for (PgConstraint constraint : primaryKeys) {
            if (constraint.getName().equals(name)) {
                return constraint;
            }
        }

        return null;
    }

    /**
     * Finds sequence according to specified sequence {@code name}.
     *
     * @param name name of the sequence to be searched
     *
     * @return found sequence or null if no such sequence has been found
     */
    public PgSequence getSequence(final String name) {
        for (PgSequence sequence : sequences) {
            if (sequence.getName().equals(name)) {
                return sequence;
            }
        }

        return null;
    }

    /**
     * Getter for {@link #indexes}. The list cannot be modified.
     *
     * @return {@link #indexes}
     */
    public List<PgIndex> getIndexes() {
        return Collections.unmodifiableList(indexes);
    }

    /**
     * Getter for {@link #primaryKeys}. The list cannot be modified.
     *
     * @return {@link #primaryKeys}
     */
    public List<PgConstraint> getPrimaryKeys() {
        return Collections.unmodifiableList(primaryKeys);
    }

    /**
     * Getter for {@link #sequences}. The list cannot be modified.
     *
     * @return {@link #sequences}
     */
    public List<PgSequence> getSequences() {
        return Collections.unmodifiableList(sequences);
    }

    /**
     * Finds table/view according to specified {@code name}.
     *
     * @param name name of the table/view to be searched
     *
     * @return found table or null if no such table has been found
     */
    public PgRelation getRelation(final String name) {
        for (PgRelation rel : rels) {
            if (rel.getName().equals(name)) {
                return rel;
            }
        }

        return null;
    }

    /**
     * Finds table according to specified table {@code name}.
     *
     * @param name name of the table to be searched
     *
     * @return found table or null if no such table has been found
     */
    public PgTable getTable(final String name) {
        PgRelation rel = this.getRelation(name);
        if (rel == null || !(rel instanceof PgTable))
            return null;
        return (PgTable) rel;
    }

    /**
     * Get a list of tables from {@link #rels}.
     *
     * @return list of tables
     */
    public List<PgTable> getTables() {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgTable> list = new ArrayList<PgTable>();

        for (PgRelation rel : rels) {
            if (rel instanceof PgTable) {
                list.add((PgTable) rel);
            }
        }

        return list;
    }

    /**
     * Getter for {@link #rels}. The list cannot be modified.
     *
     * @return {@link #rels}
     */
    public List<PgRelation> getRels() {
        return Collections.unmodifiableList(rels);
    }

    /**
     * Finds view according to specified view {@code name}.
     *
     * @param name name of the view to be searched
     *
     * @return found view or null if no such view has been found
     */
    public PgView getView(final String name) {
        PgRelation rel = this.getRelation(name);
        if (rel == null || !(rel instanceof PgView))
            return null;
        return (PgView) rel;
    }

    /**
     * Get a list of views from {@link #rels}.
     *
     * @return list of views
     */
    public List<PgView> getViews() {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgView> list = new ArrayList<PgView>();

        for (PgRelation rel : rels) {
            if (rel instanceof PgView) {
                list.add((PgView) rel);
            }
        }

        return list;
    }

    /**
     * Adds {@code index} to the list of indexes.
     *
     * @param index index
     */
    public void addIndex(final PgIndex index) {
        indexes.add(index);
    }

    /**
     * Adds {@code primary key} to the list of primary keys.
     *
     * @param primaryKey index
     */
    public void addPrimaryKey(final PgConstraint primaryKey) {
        primaryKeys.add(primaryKey);
    }

    /**
     * Adds {@code function} to the list of functions.
     *
     * @param function function
     */
    public void addFunction(final PgFunction function) {
        functions.add(function);
    }

    /**
     * Adds {@code sequence} to the list of sequences.
     *
     * @param sequence sequence
     */
    public void addSequence(final PgSequence sequence) {
        sequences.add(sequence);
    }

    /**
     * Adds {@code rel} table or view to the list of rels.
     *
     * @param rel relation
     */
    public void addRelation(final PgRelation rel) {
        rels.add(rel);
    }

    /**
     * Adds {@code type} to the list of types.
     *
     * @param type type
     */
    public void addType(final PgType type) {
        types.add(type);
    }

    /**
     * Returns a list of types
     *
     * @return types List of PgType
     */
    public List<PgType> getTypes() {
        return types;
    }

    /**
     * Finds type according to specified name {@code name}.
     *
     * @param name name of the type to be searched
     *
     * @return found type or null if no such table has been found
     */
    public PgType getType(final String name) {
        for (PgType type : types) {
            if (type.getName().equals(name)) {
                return type;
            }
        }

        return null;
    }
    
    /**
     * Get a list of rules from {@link #rels}.
     *
     * @return list of rules
     */
    public List<PgRule> getRules() {
        @SuppressWarnings("CollectionWithoutInitialCapacity")
        final List<PgRule> list = new ArrayList<PgRule>();

        for (PgRelation rel : rels) {
            if (rel instanceof PgRule) {
                list.add((PgRule) rel);
            }
        }

        return list;
    }
    
    /**
     * Returns true if schema contains type with given {@code name}, otherwise
     * false.
     *
     * @param name name of the table
     *
     * @return true if schema contains table with given {@code name}, otherwise
     * false.
     */
    public boolean containsRule(final String name) {
        for (PgRule rule : rules) {
            if (rule.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if schema contains type with given {@code name}, otherwise
     * false.
     *
     * @param name name of the table
     *
     * @return true if schema contains table with given {@code name}, otherwise
     * false.
     */
    public boolean containsType(final String name) {
        for (PgType type : types) {
            if (type.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if schema contains function with given {@code signature},
     * otherwise false.
     *
     * @param signature signature of the function
     *
     * @return true if schema contains function with given {@code signature},
     *         otherwise false
     */
    public boolean containsFunction(final String signature) {
        for (PgFunction function : functions) {
            if (function.getSignature().equals(signature)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if schema contains sequence with given {@code name},
     * otherwise false.
     *
     * @param name name of the sequence
     *
     * @return true if schema contains sequence with given {@code name},
     *         otherwise false
     */
    public boolean containsSequence(final String name) {
        for (PgSequence sequence : sequences) {
            if (sequence.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns true if schema contains table with given {@code name}, otherwise
     * false.
     *
     * @param name name of the table
     *
     * @return true if schema contains table with given {@code name}, otherwise
     *         false.
     */
    public boolean containsTable(final String name) {
        return getTable(name) != null;
    }

    /**
     * Returns true if schema contains view with given {@code name}, otherwise
     * false.
     *
     * @param name name of the view
     *
     * @return true if schema contains view with given {@code name}, otherwise
     *         false.
     */
    public boolean containsView(final String name) {
        return getView(name) != null;
    }
}
