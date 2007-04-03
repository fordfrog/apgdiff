/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.parsers;

import java.util.regex.Pattern;


/**
 * Parser utilities.
 *
 * @author fordfrog
 * @version $Id$
 */
public class ParserUtils {
    /**
     * Creates a new instance of ParserUtils.
     */
    private ParserUtils() {
        super();
    }

    /**
     * Returns position of last character of single command within
     * larger command (like CREATE TABLE). Last character is either ',' or
     * ')'. If no such character is found and method reaches the end of the
     * command then position after the last character in the command is
     * returned.
     *
     * @param command command
     * @param start start position
     *
     * @return end position of the command
     */
    public static int getCommandEnd(final String command, final int start) {
        int bracesCount = 0;
        boolean singleQuoteOn = false;
        int charPos = start;

        for (; charPos < command.length(); charPos++) {
            final char chr = command.charAt(charPos);

            if (chr == '(') {
                bracesCount++;
            } else if (chr == ')') {
                if (bracesCount == 0) {
                    break;
                } else {
                    bracesCount--;
                }
            } else if (chr == '\'') {
                singleQuoteOn ^= singleQuoteOn;
            } else if ((chr == ',') && !singleQuoteOn && (bracesCount == 0)) {
                break;
            }
        }

        return charPos;
    }

    /**
     * Removes semicolon from the end of the <code>command</code>, but
     * only if <code>command</code> ends with semicolon.
     *
     * @param command command
     *
     * @return original <code>string</code> without last character and trimmed
     */
    public static String removeLastSemicolon(final String command) {
        final String result;

        if (command.endsWith(";")) {
            result = command.substring(0, command.length() - 1).trim();
        } else {
            result = command;
        }

        return result;
    }

    /**
     * Removes substring from <code>string</code> based on
     * <code>start</code> and <code>end</code> position.
     *
     * @param string string
     * @param start start position of substring
     * @param end offset after the last character of the substring
     *
     * @return <code>string</code> without given substring
     */
    public static String removeSubString(
        final String string,
        final int start,
        final int end) {
        final String result;

        if (start == 0) {
            result = string.substring(end).trim();
        } else {
            result =
                string.substring(0, start).trim() + " "
                + string.substring(end).trim();
        }

        return result;
    }

    /**
     * Removes <code>subString</code> from <code>string</code>. The
     * removal is performed case insensitive.
     *
     * @param string string
     * @param subString substring
     *
     * @return <code>string</code> without <code>subString</code>
     */
    public static String removeSubString(
        final String string,
        final String subString) {
        return Pattern.compile(subString, Pattern.CASE_INSENSITIVE)
                      .matcher(string).replaceAll("");
    }
}
