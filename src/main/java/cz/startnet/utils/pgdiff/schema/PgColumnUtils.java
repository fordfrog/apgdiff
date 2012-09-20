/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.schema;

import java.util.Locale;

/**
 * Utilities for {@link PgColumn}.
 *
 * @author fordfrog
 */
public class PgColumnUtils {

    /**
     * Returns default value for given column type. If no default value is
     * specified then null is returned.
     *
     * @param type column type
     *
     * @return found default value or null
     */
    public static String getDefaultValue(final String type) {
        final String defaultValue;
        final String adjType = type.toLowerCase(Locale.ENGLISH);

        if ("smallint".equals(adjType)
                || "integer".equals(adjType)
                || "bigint".equals(adjType)
                || adjType.startsWith("decimal")
                || adjType.startsWith("numeric")
                || "real".equals(adjType)
                || "double precision".equals(adjType)
                || "int2".equals(adjType)
                || "int4".equals(adjType)
                || "int8".equals(adjType)
                || adjType.startsWith("float")
                || "double".equals(adjType)
                || "money".equals(adjType)) {
            defaultValue = "0";
        } else if (adjType.startsWith("character varying")
                || adjType.startsWith("varchar")
                || adjType.startsWith("character")
                || adjType.startsWith("char")
                || "text".equals(adjType)) {
            defaultValue = "''";
        } else if ("boolean".equals(adjType)) {
            defaultValue = "false";
        } else {
            defaultValue = null;
        }

        return defaultValue;
    }

    /**
     * Creates a new PgColumnUtils object.
     */
    private PgColumnUtils() {
    }
}
