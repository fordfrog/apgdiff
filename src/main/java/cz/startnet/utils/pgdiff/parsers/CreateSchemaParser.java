/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgSchema;

/**
 * Parses CREATE SCHEMA statements.
 *
 * @author fordfrog
 */
public class CreateSchemaParser {

    /**
     * Parses CREATE SCHEMA statement.
     *
     * @param database  database
     * @param statement CREATE SCHEMA statement
     */
    public static void parse(final PgDatabase database,
            final String statement) {
        final Parser parser = new Parser(statement);
        parser.expect("CREATE", "SCHEMA");

        if (parser.expectOptional("AUTHORIZATION")) {
            final PgSchema schema = new PgSchema(
                    ParserUtils.getObjectName(parser.parseIdentifier()));
            database.addSchema(schema);
            schema.setAuthorization(schema.getName());

            final String definition = parser.getRest();

            if (definition != null && !definition.isEmpty()) {
                schema.setDefinition(definition);
            }
        } else {
            final PgSchema schema = new PgSchema(
                    ParserUtils.getObjectName(parser.parseIdentifier()));
            database.addSchema(schema);

            if (parser.expectOptional("AUTHORIZATION")) {
                schema.setAuthorization(
                        ParserUtils.getObjectName(parser.parseIdentifier()));
            }

            final String definition = parser.getRest();

            if (definition != null && !definition.isEmpty()) {
                schema.setDefinition(definition);
            }
        }
    }

    /**
     * Creates a new CreateSchemaParser object.
     */
    private CreateSchemaParser() {
    }
}
