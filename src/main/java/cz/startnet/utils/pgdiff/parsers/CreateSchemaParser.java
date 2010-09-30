package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgSchema;

/**
 * Parses CREATE SCHEMA commands.
 *
 * @author fordfrog
 */
public class CreateSchemaParser {

    /**
     * Creates a new CreateSchemaParser object.
     */
    private CreateSchemaParser() {
    }

    /**
     * Parses CREATE SCHEMA command.
     *
     * @param database database
     * @param command CREATE SCHEMA command
     *
     * @throws ParserException Thrown if problem occured while parsing the
     *         command.
     */
    public static void parse(final PgDatabase database, final String command) {
        final Parser parser = new Parser(command);
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
}
