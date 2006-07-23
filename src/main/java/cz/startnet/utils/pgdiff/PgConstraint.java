/*
 * $CVSHeader$
 */
package cz.startnet.utils.pgdiff;

/**
 * Stores table constraint information.
 *
 * @author fordfrog
 * @version $CVSHeader$
 */
public class PgConstraint {
    /**
     * Definition of the constraint.
     */
    private String definition = null;

    /**
     * Name of the constraint.
     */
    private String name = null;

    /**
     * Creates a new instance of PgConstraint.
     */
    public PgConstraint() {
    }

    /**
     * Creates a new PgConstraint object.
     *
     * @param name name of the constraint
     */
    public PgConstraint(String name) {
        this.name = name;
    }

    /**
     * Setter for {@link #definition definition}.
     *
     * @param definition {@link #definition definition}
     */
    public void setDefinition(String definition) {
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
    public void setName(String name) {
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
