/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import cz.startnet.utils.pgdiff.PgDiffUtils;

/**
 *
 * @author atila
 */
public class PgExtension {
    /**
     * Name of the extension.
     */
    private final String name;
    /**
     * PgSchema
     */
    private PgSchema schema;
    /**
     * Version of the extension.
     */
    private String version;
    /**
     * Previous version of the extension.
     */
    private String from;
    
    public PgExtension(final String name) {
        this.name = name;
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
     * Getter for {@link #schema}.
     *
     * @return {@link #schema}
     */
    public PgSchema getSchema() {
        return schema;
    }

    /**
     * Setter for {@link #schema}.
     *
     * @param schema {@link #schema}
     */
    public void setSchema(PgSchema schema) {
        this.schema = schema;
    }

    /**
     * Getter for {@link #version}.
     *
     * @return {@link #version}
     */
    public String getVersion() {
        return version;
    }

    /**
     * Setter for {@link #version}.
     *
     * @param version {@link #version}
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Getter for {@link #from}.
     *
     * @return {@link #from}
     */
    public String getFrom() {
        return from;
    }

    /**
     * Setter for {@link #from}.
     *
     * @param from {@link #from}
     */
    public void setFrom(String from) {
        this.from = from;
    }
    
    /**
     * Returns creation SQL of the function.
     *
     * @return creation SQL
     */
    public String getCreationSQL() {
        final StringBuilder sbSQL = new StringBuilder();
        sbSQL.append("CREATE EXTENSION ");
        sbSQL.append(PgDiffUtils.getCreateIfNotExists());
        sbSQL.append(PgDiffUtils.getQuotedName(name));
        if (schema != null) {
            sbSQL.append(" SCHEMA ");
            sbSQL.append(schema.getName());
        }
        if (version != null && !version.isEmpty()) {
            sbSQL.append(" VERSION ");
            sbSQL.append(version);
        }
        if (from != null && !from.isEmpty()) {
            sbSQL.append(" FROM ");
            sbSQL.append(from);
        }
        sbSQL.append(';');
        return sbSQL.toString();
    }
    
    @Override
    public boolean equals(final Object object) {
        boolean equals = false;
        if (this == object) {
            equals = true;
        } else if (object instanceof PgExtension) {
            final PgExtension extension = (PgExtension) object;
            equals = name.equals(extension.getName())
                    && from.equals(extension.getFrom())
                    && version.equals(extension.getVersion());
        }
        return equals;
    }

    @Override
    public int hashCode() {
        return (getClass().getName() + "|" + name + "|" + version + "|"
                + from).hashCode();
    }
}
