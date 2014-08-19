/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgColumnUtils;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgGrant;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Diffs grants.
 *
 * @author fordfrog
 */
public class PgDiffGrants {

    /**
     * Outputs statements for altering grants.
     *
     * @param writer writer the output should be written to
     * @param arguments object containing arguments settings
     * @param oldSchema original schema
     * @param newSchema new schema
     * @param searchPathHelper search path helper
     */
    public static void alterGrants(final PrintWriter writer,
            final PgDiffArguments arguments, final PgSchema oldSchema,
            final PgSchema newSchema, final SearchPathHelper searchPathHelper) {
        for (final PgGrant newGrant : newSchema.getGrants()) {
            if (oldSchema == null
                    || !oldSchema.containsGrant(newGrant.getRole(), newGrant.getObject())) {
                continue;
            }

            final PgGrant oldGrant = oldSchema.getGrant(newGrant.getRole(), newGrant.getObject());
            if (!oldGrant.getPrivileges().equals(newGrant.getPrivileges())) {
                writer.println(oldGrant.getDropSQL());
                writer.println(newGrant.getCreationSQL());
            }

        }
    }

    /**
     * Outputs statements for creation of new grants.
     *
     * @param writer writer the output should be written to
     * @param oldSchema original schema
     * @param newSchema new schema
     * @param searchPathHelper search path helper
     */
    public static void createGrants(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        for (final PgGrant grant : newSchema.getGrants()) {
            if (oldSchema == null
                    || !oldSchema.containsGrant(grant.getRole(), grant.getObject())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(grant.getCreationSQL());
            }
        }
    }

    /**
     * Outputs statements for revoking grants.
     *
     * @param writer writer the output should be written to
     * @param oldSchema original schema
     * @param newSchema new schema
     * @param searchPathHelper search path helper
     */
    public static void revokeGrants(final PrintWriter writer,
            final PgSchema oldSchema, final PgSchema newSchema,
            final SearchPathHelper searchPathHelper) {
        if (oldSchema == null) {
            return;
        }

        for (final PgGrant grant : oldSchema.getGrants()) {
            if (!newSchema.containsGrant(grant.getRole(), grant.getObject())) {
                searchPathHelper.outputSearchPath(writer);
                writer.println();
                writer.println(grant.getDropSQL());
            }
        }
    }

    /**
     * Creates a new instance of PgDiffGrants.
     */
    private PgDiffGrants() {
    }
}
