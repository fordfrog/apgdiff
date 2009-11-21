package cz.startnet.utils.pgdiff;

/**
 * Utilities for creation of diffs.
 *
 * @author fordfrog
 */
public class PgDiffUtils {

    /**
     * Creates a new PgDiffUtils object.
     */
    private PgDiffUtils() {
        super();
    }

    /**
     * If <code>quoteName</code> is true then returns quoted name
     * otherwise returns the original name.
     *
     * @param name name
     * @param quoteName whether the name should be quoted
     *
     * @return if <code>quoteName</code> is true then returns quoted name
     *         otherwise returns the original name
     */
    public static String getQuotedName(final String name,
            final boolean quoteName) {
        return quoteName ? ("\"" + name + "\"") : name;
    }
}
