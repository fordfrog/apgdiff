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
    }

    /**
     * If name contains only lower case characters and digits, it is returned
     * not quoted, otherwise the string is returned quoted.
     *
     * @param name name
     *
     * @return quoted string if needed, otherwise not quoted string
     */
    public static String getQuotedName(final String name) {
        boolean isAllLowerCase = true;

        for (int i = 0; i < name.length(); i++) {
            final char chr = name.charAt(i);

            if (Character.isUpperCase(chr)) {
                isAllLowerCase = false;
                break;
            }
        }
        return isAllLowerCase ? name : ("\"" + name + "\"");
    }
}
