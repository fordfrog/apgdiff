package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgFunction;

/**
 * Parses CREATE FUNCTION and CREATE OR REPLACE FUNCTION commands.
 *
 * @author fordfrog
 */
public class CreateFunctionParser {

    /**
     * Creates a new instance of CreateFunctionParser.
     */
    private CreateFunctionParser() {
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
        final Parser parser = new Parser(command);
        parser.expect("CREATE");
        parser.expectOptional("OR", "REPLACE");
        parser.expect("FUNCTION");

        final String functionName = parser.parseIdentifier();
        final PgFunction function = new PgFunction();
        function.setName(ParserUtils.getObjectName(functionName));
        database.getSchema(ParserUtils.getSchemaName(
                functionName, database)).addFunction(function);

        parser.expect("(");

        while (!parser.expectOptional(")")) {
            final String mode;

            if (parser.expectOptional("IN")) {
                mode = "IN";
            } else if (parser.expectOptional("OUT")) {
                mode = "OUT";
            } else if (parser.expectOptional("INOUT")) {
                mode = "INOUT";
            } else if (parser.expectOptional("VARIADIC")) {
                mode = "VARIADIC";
            } else {
                mode = null;
            }

            final int endPos =
                    parser.findOneOfInExpression(true, "=", "DEFAULT");
            final int startPos = parser.parseDataTypeBackward(endPos);
            final String dataType =
                    parser.getSubString(startPos, endPos).trim();
            final String argumentName;

            if (startPos > parser.getPosition()) {
                argumentName = parser.parseIdentifier();
            } else {
                argumentName = null;
            }

            parser.setPosition(endPos);

            final String defaultExpression;

            if (parser.expectOptional("=")
                    || parser.expectOptional("DEFAULT")) {
                defaultExpression = parser.getExpression();
            } else {
                defaultExpression = null;
            }

            final PgFunction.Argument argument = new PgFunction.Argument();
            argument.setDataType(dataType);
            argument.setDefaultExpression(defaultExpression);
            argument.setMode(mode);
            argument.setName(argumentName);
            function.addArgument(argument);

            if (parser.expectOptional(")")) {
                break;
            } else {
                parser.expect(",");
            }
        }

        function.setBody(parser.getRest());
    }
}
