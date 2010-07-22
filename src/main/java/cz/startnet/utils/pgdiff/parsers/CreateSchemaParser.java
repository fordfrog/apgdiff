package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgSchema;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses CREATE SCHEMA commands.
 *
 * @author fordfrog
 */
public class CreateSchemaParser {

    /**
     * Pattern for parsing CREATE SCHEMA ... AUTHORIZATION ...
     */
    private static final Pattern PATTERN_CREATE_SCHEMA = Pattern.compile(
            "^CREATE[\\s]+SCHEMA[\\s]+([^\\s;]+)"
            + "(?:[\\s]+AUTHORIZATION[\\s]+([^\\s;]+))?;$",
            Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for parsing CREATE SCHEMA AUTHORIZATION ...
     */
    private static final Pattern PATTERN_CREATE_SCHEMA_AUTHORIZATION =
            Pattern.compile(
            "^CREATE[\\s]+SCHEMA[\\s]+AUTHORIZATION[\\s]+([^\\s;]+);$",
            Pattern.CASE_INSENSITIVE);

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
        final Matcher matcher = PATTERN_CREATE_SCHEMA.matcher(command);

        if (matcher.matches()) {
            final PgSchema schema = new PgSchema(matcher.group(1));
            final String authorization = matcher.group(2);

            if (authorization != null) {
                schema.setAuthorization(authorization);
            }

            database.addSchema(schema);
        } else {
            final Matcher matcher2 =
                    PATTERN_CREATE_SCHEMA_AUTHORIZATION.matcher(command);

            if (matcher2.matches()) {
                final PgSchema schema = new PgSchema(matcher.group(1));
                schema.setAuthorization(schema.getName());
                database.addSchema(schema);
            } else {
                throw new ParserException(
                        ParserException.CANNOT_PARSE_COMMAND + command);
            }
        }
    }
}
