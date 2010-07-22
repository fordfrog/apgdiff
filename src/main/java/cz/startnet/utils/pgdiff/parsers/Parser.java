package cz.startnet.utils.pgdiff.parsers;

import java.util.Locale;

/**
 * Class for parsing strings.
 * 
 * @author fordfrog
 */
public final class Parser {

    /**
     * String to be parsed.
     */
    private final String string;
    /**
     * Current position.
     */
    private int position;

    /**
     * Creates new instance of Parser.
     *
     * @param string {@link #string}
     */
    public Parser(final String string) {
        this.string = string;
        skipWhitespace();
    }

    /**
     * Checks whether the string contains given word on current position. If not
     * then throws an exception.
     * 
     * @param words list of words to check
     */
    public void expect(final String... words) {
        for (final String word : words) {
            expect(word, false);
        }
    }

    /**
     * Checks whether the string contains given word on current position. If not
     * and expectation is optional then position is not changed and method
     * returns true. If expectation is not optional, exception with error
     * description is thrown. If word is found, position is moved at first
     * non-whitespace character following the word.
     *
     * @param word word to expect
     * @param optional true if word is optional, otherwise false
     *
     * @return true if word was found, otherwise false
     */
    public boolean expect(final String word, final boolean optional) {
        final int wordEnd = position + word.length();

        if (wordEnd <= string.length()
                && string.substring(position, wordEnd).equalsIgnoreCase(word)
                && (wordEnd == string.length()
                || !Character.isLetter(wordEnd))) {
            position = wordEnd;
            skipWhitespace();

            return true;
        }

        if (optional) {
            return false;
        }

        throw new ParserException("Cannot parse string: " + string
                + "\nExpected " + word + " at position " + position);
    }

    /**
     * Checks whether string contains at current position sequence of the words.
     *
     * @param words array of words
     *
     * @return true if whole sequence was found, otherwise false
     */
    public boolean expectOptional(final String... words) {
        final boolean found = expect(words[0], true);

        if (!found) {
            return false;
        }

        for (int i = 1; i < words.length; i++) {
            skipWhitespace();
            expect(words[i]);
        }

        return true;
    }

    /**
     * Moves position in the string to next non-whitespace string.
     */
    public void skipWhitespace() {
        for (; position < string.length(); position++) {
            if (!Character.isWhitespace(string.charAt(position))) {
                break;
            }
        }
    }

    /**
     * Parses identifier from current position. If identifier is quoted, it is
     * returned as it is. If the identifier is not quoted, it is converted to
     * lowercase. If identifier does not start with letter then exception is
     * thrown. Position is placed at next first non-whitespace character.
     * 
     * @return parsed identifier
     */
    public String parseIdentifier() {
        final boolean quoted = string.charAt(position) == '"';

        if (quoted) {
            final int endPos = string.indexOf('"', position + 1);
            final String result = string.substring(position + 1, endPos);
            position = endPos + 1;
            skipWhitespace();

            return result;
        } else {
            int endPos = position;

            if (!Character.isLetter(string.charAt(endPos))) {
                throw new ParserException("Cannot parse string: " + string
                        + "\nIdentifier must begin with letter at position "
                        + position);
            }

            for (endPos++; endPos < string.length(); endPos++) {
                final char chr = string.charAt(endPos);

                if (Character.isWhitespace(chr) || chr == ',' || chr == ')'
                        || chr == ';') {
                    break;
                }
            }

            final String result =
                    string.substring(position, endPos).toLowerCase(
                    Locale.ENGLISH);

            position = endPos;
            skipWhitespace();

            return result;
        }
    }

    /**
     * Returns rest of the string. If the string ends with ';' then it is
     * removed from the string before returned.
     *
     * @return rest of the string, without trailing ';' if present
     */
    public String getRest() {
        final String result;

        if (string.charAt(string.length() - 1) == ';') {
            result = string.substring(position, string.length() - 1);
        } else {
            result = string.substring(position);
        }

        position = string.length();

        return result;
    }

    /**
     * Parses integer from the string. If next word is not integer then
     * exception is thrown.
     *
     * @return parsed integer value
     */
    public int parseInteger() {
        int endPos = position;

        for (; endPos < string.length(); endPos++) {
            if (!Character.isLetterOrDigit(string.charAt(endPos))) {
                break;
            }
        }

        try {
            final int result =
                    Integer.parseInt(string.substring(position, endPos));

            position = endPos;
            skipWhitespace();

            return result;
        } catch (final NumberFormatException ex) {
            throw new ParserException("Cannot parse string: " + string
                    + "\nExpected integer at position: " + position);
        }
    }

    /**
     * Returns expression that is ended either with ',', ')' or with end of the
     * string. If expression is empty then exception is thrown.
     *
     * @return expression string
     */
    public String getExpression() {
        final int endPos = getExpressionEnd();

        if (position == endPos) {
            throw new ParserException("Cannot parse string: " + string
                    + "\nExpected expression at position " + position);
        }

        final String result = string.substring(position, endPos).trim();

        position = endPos;

        return result;
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
    private int getExpressionEnd() {
        int bracesCount = 0;
        boolean singleQuoteOn = false;
        int charPos = position;

        for (; charPos < string.length(); charPos++) {
            final char chr = string.charAt(charPos);

            if (chr == '(') {
                bracesCount++;
            } else if (chr == ')') {
                if (bracesCount == 0) {
                    break;
                } else {
                    bracesCount--;
                }
            } else if (chr == '\'') {
                singleQuoteOn = !singleQuoteOn;
            } else if ((chr == ',') && !singleQuoteOn && (bracesCount == 0)) {
                break;
            } else if (chr == ';' && bracesCount == 0 && !singleQuoteOn) {
                break;
            }
        }

        return charPos;
    }

    /**
     * Returns current position in the string.
     *
     * @return current position in the string
     */
    public int getPosition() {
        return position;
    }

    /**
     * Returns parsed string.
     *
     * @return parsed string
     */
    public String getString() {
        return string;
    }

    /**
     * Throws exception about unsupported command.
     */
    public void throwUnsupportedCommand() {
        throw new ParserException("Cannot parse string: " + string
                + "\nUnsupported command at position " + position);
    }
}
