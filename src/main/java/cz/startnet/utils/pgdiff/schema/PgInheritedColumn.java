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
     * Determines whether null value is allowed in the column.
     */
    private boolean nullValue = true;
    
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

    /**
     * Setter for {@link #nullValue}.
     *
     * @param nullValue {@link #nullValue}
     */
    public void setNullValue(final boolean nullValue) {
        this.nullValue = nullValue;
    }

    /**
     * Getter for {@link #nullValue}.
     *
     * @return {@link #nullValue}
     */
    public boolean getNullValue() {
        return nullValue;
    }
 }
