/*
 * PgSequence.java
 *
 * Created on 23. bøezen 2006, 10:41
 */
package cz.startnet.utils.pgdiff;


/**
 * Stores sequence information.
 * @author fordfrog
 */
public class PgSequence {
    private String definition = null;
    private String name = null;

    /**
     * Creates a new instance of PgSequence.
     */
    public PgSequence() {
    }

    public PgSequence(String name) {
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
