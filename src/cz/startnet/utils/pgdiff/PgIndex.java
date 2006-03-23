/*
 * PgIndex.java
 *
 * Created on 23. bøezen 2006, 12:56
 */
package cz.startnet.utils.pgdiff;


/**
 * Stores table index information.
 * @author fordfrog
 */
public class PgIndex {
    private String definition = null;
    private String name = null;

    /**
     * Creates a new instance of PgIndex.
     */
    public PgIndex() {
    }

    public PgIndex(String name) {
        this.setName(name);
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
