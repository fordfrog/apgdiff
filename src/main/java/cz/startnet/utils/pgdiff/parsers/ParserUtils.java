package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import java.util.List;

/**
 * Parser utilities.
 *
 * @author fordfrog
 */
public class ParserUtils {

    /**
     * Creates a new instance of ParserUtils.
     */
    private ParserUtils() {
    }

    /**
     * Returns object name from optionally schema qualified name.
     *
     * @param name optionally schema qualified name
     *
     * @return name of the object
     */
    public static String getObjectName(final String name) {
        final String result;
        final int pos = name.indexOf('.');

        if (pos == -1) {
            result = name;
        } else {
            result = name.substring(pos + 1);
        }

        return result;
    }

    /**
     * Returns schema name from optionally schema qualified name.
     *
     * @param name optionally schema qualified name
     * @param database database
     *
     * @return name of the schema
     */
    public static String getSchemaName(final String name,
            final PgDatabase database) {
        final String result;
        final int pos = name.indexOf('.');

        if (pos == -1) {
            result = database.getDefaultSchema().getName();
        } else {
            result = name.substring(0, pos);
        }

        return result;
    }

    /**
     * Generates unique name from the prefix, list of names, and postfix.
     *
     * @param prefix prefix
     * @param names list of names
     * @param postfix postfix
     *
     * @return generated name
     */
    public static String generateName(final String prefix,
            final List<String> names, final String postfix) {
        final String adjName;

        if (names.size() == 1) {
            adjName = names.get(0);
        } else {
            final StringBuilder sbString = new StringBuilder(names.size() * 15);

            for (final String name : names) {
                if (sbString.length() > 0) {
                    sbString.append(',');
                }

                sbString.append(name);
            }

            adjName = Integer.toHexString(sbString.toString().hashCode());
        }

        final StringBuilder sbResult = new StringBuilder(30);

        if (prefix != null && !prefix.isEmpty()) {
            sbResult.append(prefix);
        }

        sbResult.append(adjName);

        if (postfix != null && !postfix.isEmpty()) {
            sbResult.append(postfix);
        }

        return sbResult.toString();
    }
}
