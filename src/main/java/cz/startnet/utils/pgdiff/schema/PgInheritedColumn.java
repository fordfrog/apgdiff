/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

/**
 * Stores inherited column information.
 *
 * @author dwatson78
 */
 public class PgInheritedColumn {
    /**
     * Inherited column
     */
    private final PgColumn inheritedColumn;
    
    public PgInheritedColumn(final PgColumn inheritedColumn) {
        this.inheritedColumn = inheritedColumn;
    }
    
    /**
     * Getter for {@link #inheritedColumn}.
     *
     * @return {@link #inheritedColumn}
     */
    public PgColumn getInheritedColumn() {
        return inheritedColumn;
    }
    
    /**
     * Default value of the column.
     */
    private String defaultValue;
    
    /**
     * Setter for {@link #defaultValue}.
     *
     * @param defaultValue {@link #defaultValue}
     */
    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    /**
     * Getter for {@link #defaultValue}.
     *
     * @return {@link #defaultValue}
     */
    public String getDefaultValue() {
        return defaultValue;
    }
 }
