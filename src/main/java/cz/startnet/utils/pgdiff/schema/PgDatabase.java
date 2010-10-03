/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores database information.
 *
 * @author fordfrog
 */
public class PgDatabase {

    /**
     * List of database schemas.
     */
    private final List<PgSchema> schemas = new ArrayList<PgSchema>(1);
    /**
     * Array of ignored statements.
     */
    @SuppressWarnings("CollectionWithoutInitialCapacity")
    private final List<String> ignoredStatements = new ArrayList<String>();
    /**
     * Current default schema.
     */
    private PgSchema defaultSchema;
    /**
     * Comment.
     */
    private String comment;

    /**
     * Creates a new PgDatabase object.
     */
    public PgDatabase() {
        schemas.add(new PgSchema("public"));
        defaultSchema = schemas.get(0);
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
     * Sets default schema according to the <code>name</code> of the
     * schema.
     *
     * @param name name of the schema
     */
    public void setDefaultSchema(final String name) {
        defaultSchema = getSchema(name);
    }

    /**
     * Getter for {@link #defaultSchema}.
     *
     * @return {@link #defaultSchema}
     */
    public PgSchema getDefaultSchema() {
        return defaultSchema;
    }

    /**
     * Getter for {@link #ignoredStatements}.
     *
     * @return {@link #ignoredStatements}
     */
    public List<String> getIgnoredStatements() {
        return Collections.unmodifiableList(ignoredStatements);
    }

    /**
     * Adds ignored statement to the list of ignored statements.
     *
     * @param ignoredStatement ignored statement
     */
    public void addIgnoredStatement(final String ignoredStatement) {
        ignoredStatements.add(ignoredStatement);
    }

    /**
     * Returns schema of given name or null if the schema has not been
     * found.
     *
     * @param name schema name
     *
     * @return found schema or null
     */
    public PgSchema getSchema(final String name) {
        PgSchema schema = null;

        for (final PgSchema curSchema : schemas) {
            if (curSchema.getName().equals(name)) {
                schema = curSchema;

                break;
            }
        }

        return schema;
    }

    /**
     * Getter for {@link #schemas}. The list cannot be modified.
     *
     * @return {@link #schemas}
     */
    public List<PgSchema> getSchemas() {
        return Collections.unmodifiableList(schemas);
    }

    /**
     * Adds <code>schema</code> to the lists of schemas.
     *
     * @param schema schema
     */
    public void addSchema(final PgSchema schema) {
        schemas.add(schema);
    }
}
