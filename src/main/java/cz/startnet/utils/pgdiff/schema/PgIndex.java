/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.schema;

/**
 * Stores table index information.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgIndex {
    /**
     * Definition of the index.
     */
    private String definition = null;

    /**
     * Name of the index.
     */
    private String name = null;

    /**
     * Creates a new PgIndex object.
     *
     * @param name name of the index
     */
    public PgIndex(final String name) {
        this.name = name;
    }

    /**
     * Setter for {@link #definition definition}.
     *
     * @param definition {@link #definition definition}
     */
    public void setDefinition(final String definition) {
        this.definition = definition;
    }

    /**
     * Getter for {@link #definition definition}.
     *
     * @return {@link #definition definition}
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Setter for {@link #name name}.
     *
     * @param name {@link #name name}
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Getter for {@link #name name}.
     *
     * @return {@link #name name}
     */
    public String getName() {
        return name;
    }
}
