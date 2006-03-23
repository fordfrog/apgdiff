/*
 * PgConstraint.java
 *
 * Created on 23. bøezen 2006, 12:10
 */
package cz.startnet.utils.pgdiff;


/**
 * Stores table constraint information.
 * @author fordfrog
 */
public class PgConstraint {
    private String definition = null;
    private String name = null;

    /**
     * Creates a new instance of PgConstraint.
     */
    public PgConstraint() {
    }

    public PgConstraint(String name) {
        this.name = name;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getDefinition() {
        return definition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
