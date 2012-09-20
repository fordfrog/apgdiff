/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.Resources;
import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgFunction;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import java.text.MessageFormat;

/**
 * Parses CREATE FUNCTION and CREATE OR REPLACE FUNCTION statements.
 *
 * @author fordfrog
 */
public class CreateFunctionParser {

    /**
     * Parses CREATE FUNCTION and CREATE OR REPLACE FUNCTION statement.
     *
     * @param database  database
     * @param statement CREATE FUNCTION statement
     */
    public static void parse(final PgDatabase database,
            final String statement) {
        final Parser parser = new Parser(statement);
        parser.expect("CREATE");
        parser.expectOptional("OR", "REPLACE");
        parser.expect("FUNCTION");

        final String functionName = parser.parseIdentifier();
        final String schemaName =
                ParserUtils.getSchemaName(functionName, database);
        final PgSchema schema = database.getSchema(schemaName);

        if (schema == null) {
            throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindSchema"), schemaName,
                    statement));
        }

        final PgFunction function = new PgFunction();
        function.setName(ParserUtils.getObjectName(functionName));
        schema.addFunction(function);

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

            final int position = parser.getPosition();
            String argumentName = null;
            String dataType = parser.parseDataType();

            final int position2 = parser.getPosition();

            if (!parser.expectOptional(")") && !parser.expectOptional(",")
                    && !parser.expectOptional("=")
                    && !parser.expectOptional("DEFAULT")) {
                parser.setPosition(position);
                argumentName =
                        ParserUtils.getObjectName(parser.parseIdentifier());
                dataType = parser.parseDataType();
            } else {
                parser.setPosition(position2);
            }

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

    /**
     * Creates a new instance of CreateFunctionParser.
     */
    private CreateFunctionParser() {
    }
}
