package cz.startnet.utils.pgdiff.schema;

import java.util.ArrayList;
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
    private final List<PgSchema> schemas = new ArrayList<PgSchema>();
    /**
     * Current default schema.
     */
    private PgSchema defaultSchema;

    /**
     * Creates a new PgDatabase object.
     */
    public PgDatabase() {
        super();
        schemas.add(new PgSchema("public"));
        defaultSchema = schemas.get(0);
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
     * Returns schema of given name or null if the schema has not been
     * found.
     *
     * @param name schema name
     *
     * @return found schema or null
     */
    public PgSchema getSchema(final String name) {
        PgSchema schema = null;

        for (PgSchema curSchema : schemas) {
            if (curSchema.getName().equals(name)) {
                schema = curSchema;

                break;
            }
        }

        return schema;
    }

    /**
     * Getter for {@link #schemas}.
     *
     * @return {@link #schemas}
     */
    public List<PgSchema> getSchemas() {
        return schemas;
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
