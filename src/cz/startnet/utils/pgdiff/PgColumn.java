/*
 * PgColumn.java
 *
 * Created on 23. bøezen 2006, 9:58
 */
package cz.startnet.utils.pgdiff;


/**
 * Stores column information.
 * @author fordfrog
 */
public class PgColumn {
    private String definition = null;
    private String name = null;

    /**
     * Creates a new instance of PgColumn.
     */
    public PgColumn() {
    }

    public PgColumn(String name) {
        this.name = name;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getDefinition() {
        return definition;
    }

    public String getFullDefinition() {
        return name + " " + definition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
