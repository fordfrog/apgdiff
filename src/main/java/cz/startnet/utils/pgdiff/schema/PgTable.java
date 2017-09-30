/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.Pair;
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
     * List of inheritedColumns defined on the table.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<PgInheritedColumn> inheritedColumns = new ArrayList<PgInheritedColumn>();
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
    private final List<Pair<String,String>> inherits = new ArrayList<Pair<String,String>>();
    /**
     * WITH clause. If value is null then it is not set, otherwise can be set to
     * OIDS=true, OIDS=false, or storage parameters can be set.
     */
    private String with;
    /**
     * Is this a UNLOGGED table?
     */
    private boolean unlogged;
    /**
     * Is this a FOREIGN table?
     */
    private boolean foreign;
    /**
     * Does this table have RLS enabled?
     */
    private Boolean rlsEnabled;
    /**
     * Does this table have RLS forced?
     */
    private Boolean rlsForced;

    private String foreignServer;

    /**
     * RLS Policies
     */
    private List<PgPolicy> policies = new ArrayList<PgPolicy>();

    /**
     * PgDatabase
     */
    private final PgDatabase database;

    /**
     * PgSchema
     */
    private final PgSchema schema;

    /**
     * Creates a new PgTable object.
     *
     * @param name {@link #name}
     * @param database name of database
     * @param schema name of schema
     */
    public PgTable(final String name, final PgDatabase database, final PgSchema schema) {
        setName(name);
        this.database = database;
        this.schema = schema;
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
     * Returns relation kind for CREATE/ALTER/DROP commands.
     *
     * @return relation kind
     */
    public String getRelationKind() {
        return "TABLE";
    }

    /**
     * Creates and returns SQL for creation of the table.
     *
     * @param schema schema of current statement
     *
     * @return created SQL statement
     */
    public String getCreationSQL(final PgSchema schema) {
        final StringBuilder sbSQL = new StringBuilder(1000);
        sbSQL.append("CREATE ");
        if (isUnlogged()) {
            sbSQL.append("UNLOGGED ");
        }
        if (isForeign()) {
            sbSQL.append("FOREIGN ");
        }
        sbSQL.append("TABLE ");
        sbSQL.append(PgDiffUtils.getCreateIfNotExists());
        sbSQL.append(PgDiffUtils.getQuotedName(name));
        sbSQL.append(" (");
        sbSQL.append(System.getProperty("line.separator"));

        boolean first = true;

        if (columns.isEmpty()) {
            sbSQL.append(')');
        } else {
            for (PgColumn column : columns) {
                if (first) {
                    first = false;
                } else {
                    sbSQL.append(",");
                    sbSQL.append(System.getProperty("line.separator"));
                }

                sbSQL.append("\t");
                sbSQL.append(column.getFullDefinition(false));
            }

            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append(")");
        }

        if (inherits != null && !inherits.isEmpty()) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("INHERITS (");

            first = true;

            for (final Pair<String,String> inheritPair : inherits) {
                if (first) {
                    first = false;
                } else {
                    sbSQL.append(", ");
                }
                String inheritTableName = null;
                if(schema.getName().equals(inheritPair.getL())){
                    inheritTableName = inheritPair.getR();
                } else {
                    inheritTableName = String.format("%s.%s", inheritPair.getL(), inheritPair.getR());
                }
                sbSQL.append(inheritTableName);
            }

            sbSQL.append(")");
        }

        if (with != null && !with.isEmpty()) {
            sbSQL.append(System.getProperty("line.separator"));

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

        if (isForeign()) {
            sbSQL.append("SERVER ");
            sbSQL.append(getForeignServer());
        }
        
        if (tablespace != null && !tablespace.isEmpty()) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("TABLESPACE ");
            sbSQL.append(tablespace);
        }

        sbSQL.append(';');

        //Inherited column default override
        for (PgInheritedColumn column : getInheritedColumns()) {
            if(column.getDefaultValue() != null){
                sbSQL.append(System.getProperty("line.separator"));
                sbSQL.append(System.getProperty("line.separator"));
                sbSQL.append("ALTER TABLE ONLY ");
                sbSQL.append(PgDiffUtils.getQuotedName(name));
                sbSQL.append(System.getProperty("line.separator"));
                sbSQL.append("\tALTER COLUMN ");
                sbSQL.append(
                    PgDiffUtils.getQuotedName(column.getInheritedColumn().getName()));
                sbSQL.append(" SET DEFAULT ");
                sbSQL.append(column.getDefaultValue());
                sbSQL.append(';');
            }
        }

        for (PgColumn column : getColumnsWithStatistics()) {
            sbSQL.append(System.getProperty("line.separator"));
            sbSQL.append("ALTER TABLE ONLY ");
            sbSQL.append(PgDiffUtils.getQuotedName(name));
            sbSQL.append(" ALTER COLUMN ");
            sbSQL.append(
                    PgDiffUtils.getQuotedName(column.getName()));
            sbSQL.append(" SET STATISTICS ");
            sbSQL.append(column.getStatistics());
            sbSQL.append(';');
        }

        sbSQL.append(getCommentDefinitionSQL());

        return sbSQL.toString();
    }

    /**
     * Setter for {@link #inherits}.
     *
     * @param schemaName name of schema
     * @param tableName name of inherited table
     */
    public void addInherits(final String schemaName, final String tableName) {
        inherits.add(new Pair<String, String>(schemaName, tableName));
        final PgTable inheritedTable = database.getSchema(schemaName).getTable(tableName);
        for( PgColumn column : inheritedTable.getColumns() ) {
          PgInheritedColumn inheritedColumn = new PgInheritedColumn(column);
          inheritedColumns.add(inheritedColumn);
        }
    }

    /**
     * Getter for {@link #inherits}.
     *
     * @return {@link #inherits}
     */
    public List<Pair<String,String>> getInherits() {
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
     * Getter for {@link #tablespace}.
     *
     * @return {@link #tablespace}
     */
    public String getTablespace() {
        return tablespace;
    }

    /**
     * Setter for {@link #tablespace}.
     *
     * @param tablespace {@link #tablespace}
     */
    public void setTablespace(final String tablespace) {
        this.tablespace = tablespace;
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
     * Adds {@code inheritedColumn} to the list of inheritedColumns.
     *
     * @param inheritedColumn inheritedColumn
     */
    public void addInheritedColumn(final PgInheritedColumn inheritedColumn) {
        inheritedColumns.add(inheritedColumn);
    }

    /**
     * Finds inheritedColumn according to specified name {@code name}.
     *
     * @param name name of the inheritedColumn to be searched
     *
     * @return found inheritedColumn or null if no such inheritedColumn
     * has been found
     */
    public PgInheritedColumn getInheritedColumn(final String name) {
        if (inherits != null && !inherits.isEmpty()) {
            for (PgInheritedColumn inheritedColumn : inheritedColumns) {
                if (inheritedColumn.getInheritedColumn().getName().equals(name)) {
                    return inheritedColumn;
                }
            }
        }
        return null;
    }

    /**
     * Getter for {@link #inheritedColumns}. The list cannot be modified.
     *
     * @return {@link #inheritedColumns}
     */
    public List<PgInheritedColumn> getInheritedColumns() {
        return Collections.unmodifiableList(inheritedColumns);
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
     * Returns true if table contains given inheritedColumn {@code name},
     * otherwise false.
     *
     * @param name name of the inheritedColumn
     *
     * @return true if table contains given inheritedColumn {@code name},
     * otherwise false
     */
    public boolean containsInheritedColumn(final String name) {
        if (inherits != null && !inherits.isEmpty()) {
            for (PgInheritedColumn inheritedColumn : inheritedColumns) {
                if (inheritedColumn.getInheritedColumn().getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
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

    public boolean isUnlogged() {
        return unlogged;
    }

    public void setUnlogged(boolean unlogged) {
        this.unlogged = unlogged;
    }
    
    /**
     * Foreign Tables
     */
    
    @Override
    public String getDropSQL() {
        
        return "DROP " + ((isForeign()) ? "FOREIGN ":"") + getRelationKind() + " " + PgDiffUtils.getDropIfExists() +
                PgDiffUtils.getQuotedName(getName()) + ";";
    }
    
    public boolean isForeign() {
        return foreign;
    }

    public void setForeign(boolean foreign) {
        this.foreign = foreign;
    }
    
    public void setForeignServer(String server){
    	foreignServer = server;
    }
    
    public String getForeignServer(){
    	return foreignServer;
    }

    public Boolean hasRLSEnabled() {
        return rlsEnabled;
    }

    public void setRLSEnabled(Boolean rlsEnabled) {
        this.rlsEnabled = rlsEnabled;
    }

    public Boolean hasRLSForced() {
        return rlsForced;
    }

    public void setRLSForced(Boolean rlsForced) {
        this.rlsForced = rlsForced;
    }

    public void addPolicy(final PgPolicy policy) {
        policies.add(policy);
    }

    public PgPolicy getPolicy(final String name) {
        for (PgPolicy policy : policies) {
            if (policy.getName().equals(name)) {
                return policy;
            }
        }

        return null;
    }

    public List<PgPolicy> getPolicies() {
        return Collections.unmodifiableList(policies);
    }
}
