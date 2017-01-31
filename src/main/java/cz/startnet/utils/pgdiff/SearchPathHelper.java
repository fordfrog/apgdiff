/**
 * Copyright 2010 StartNet s.r.o.
 */
package cz.startnet.utils.pgdiff;

import java.io.PrintWriter;

/**
 * Helps to output search path only if it was not output yet.
 *
 * @author fordfrog
 */
public class SearchPathHelper {

    /**
     * Statement to output.
     */
    private final String searchPath;
    /**
     * Name of current schema.
     */
    private final String schema;
    /**
     * Flag determining whether the statement was already output.
     */
    private boolean wasOutput;
    /**
     * Flag determining whether statements should be schema-qualified.
     */
    private boolean schemaQualify;
    /**
     * Flag determining whether anything should be output at all.
     */
    private boolean setSearchPath;

    /**
     * Creates new instance of SearchPathHelper.
     *
     * @param schema {@link #schema}
     * @param schemaQualify {@link #schemaQualify}
     * @param setSearchPath {@link #setSearchPath}
     */
    public SearchPathHelper(final String schema, final boolean schemaQualify, final boolean setSearchPath) {
        this.schema = PgDiffUtils.getQuotedName(schema, true);
        this.schemaQualify = schemaQualify;
        this.searchPath = "SET search_path = " + PgDiffUtils.getQuotedName(schema, true)
        	+ ", pg_catalog;";
        this.setSearchPath = setSearchPath;
        
        if (schemaQualify) {
        	wasOutput = true;
        }
    }

    /**
     * Outputs search path if it was not output yet.
     *
     * @param writer writer
     */
    public void outputSearchPath(final PrintWriter writer) {
        if (!wasOutput && setSearchPath) {
            writer.println();
            writer.println(searchPath);
            wasOutput = true;
        }
    }
    
    
    public String getQuotedName(String objectName) {
    	if (schemaQualify) {
    		return schema + "." + PgDiffUtils.getQuotedName(objectName);
    	}
    	else {
    		return PgDiffUtils.getQuotedName(objectName);
    	}
    }
    
    
}
