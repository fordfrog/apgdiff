/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses CREATE FUNCTION and CREATE OR REPLACE FUNCTION commands.
 *
 * @author fordfrog
 * @version $Id$
 */
public class CreateFunctionParser {
    /**
     * Pattern for parsing CREATE FUNCTION and CREATE OR REPLACE
     * FUNCTION command.
     */
    private static final Pattern PATTERN =
        Pattern.compile(
                "^CREATE[\\s]+(?:OR[\\s]+REPLACE[\\s]+)?FUNCTION[\\s]+"
                + "([^\\s(]+)\\(([^)]*)\\).*$",
                Pattern.CASE_INSENSITIVE + Pattern.DOTALL);

    /**
     * Pattern for parsing function arguments.
     */
    private static final Pattern PATTERN_ARGUMENT =
        Pattern.compile(
                "^(?:(?:IN|OUT|INOUT)[\\s]+)?(?:\"?[^\\s\"]+\"?[\\s]+)?(.+)$",
                Pattern.CASE_INSENSITIVE);

    /**
     * Creates a new instance of CreateFunctionParser.
     */
    private CreateFunctionParser() {
        super();
    }

    /**
     * Parses CREATE FUNCTION and CREATE OR REPLACE FUNCTION command.
     *
     * @param database database
     * @param command CREATE FUNCTION command
     *
     * @throws ParserException Thrown if problem occured while parsing the
     *         command.
     */
    public static void parse(final PgDatabase database, final String command) {
        final Matcher matcher = PATTERN.matcher(command.trim());

        if (matcher.matches()) {
            final String functionName = matcher.group(1).trim();
            final String arguments = matcher.group(2);
            final PgFunction function = new PgFunction();
            function.setDeclaration(
                    getFunctionDeclaration(functionName, arguments));
            function.setDefinition(command);
            function.setName(ParserUtils.getObjectName(functionName));
            database.getSchema(
                    ParserUtils.getSchemaName(functionName, database))
                    .addFunction(function);
        } else {
            throw new ParserException(
                    ParserException.CANNOT_PARSE_COMMAND + command);
        }
    }

    /**
     * Creates declaration string of the function. The string consists
     * of function name, '(', list of argument types separated by ',' and ')'.
     *
     * @param functionName name of the function
     * @param arguments string containing arguments part of function
     *        declaration
     *
     * @return function name
     *
     * @throws ParserException Thrown if cannot parse function arguments.
     */
    private static String getFunctionDeclaration(
        final String functionName,
        final String arguments) {
        final String result;

        if ((arguments == null) || (arguments.trim().length() == 0)) {
            result = functionName + "()";
        } else {
            final String[] parts = arguments.split(",");
            final List<String> args = new ArrayList<String>();

            for (String part : parts) {
                final Matcher matcher = PATTERN_ARGUMENT.matcher(part.trim());

                if (matcher.matches()) {
                    args.add(matcher.group(1).trim());
                } else {
                    throw new ParserException(
                            "Cannot parse function argument: " + part);
                }
            }

            final StringBuilder sbResult = new StringBuilder(functionName);
            sbResult.append('(');

            for (int i = 0; i < args.size(); i++) {
                if (i > 0) {
                    sbResult.append(',');
                }

                sbResult.append(args.get(i).toLowerCase(Locale.ENGLISH));
            }

            sbResult.append(')');
            result = sbResult.toString();
        }

        return result;
    }
}
