/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses CREATE VIEW commands.
 *
 * @author fordfrog
 * @version $Id$
 */
public class CreateViewParser {
    /**
     * Pattern for parsing CREATE VIEW definition.
     */
    private static final Pattern PATTERN =
        Pattern.compile(
                "CREATE[\\s]+(?:OR[\\s]+REPLACE[\\s]+)?VIEW[\\s]+"
                + "\"?([^\\s\"]+)\"?[\\s]+(?:\\(([^)]+)\\)[\\s]+)?"
                + "AS[\\s]+(.+)?(?:;)",
                Pattern.CASE_INSENSITIVE);

    /**
     * Creates a new instance of CreateViewParser.
     */
    private CreateViewParser() {
        super();
    }

    /**
     * Parses CREATE VIEW command.
     *
     * @param database database
     * @param command CREATE VIEW command
     *
     * @throws ParserException Thrown if problem occured while parsing the
     *         command.
     */
    public static void parse(final PgDatabase database, final String command) {
        final Matcher matcher = PATTERN.matcher(command.trim());

        if (matcher.matches()) {
            final String viewName = matcher.group(1);
            final String columnNames = matcher.group(2);
            final String query = matcher.group(3);

            if ((viewName == null) || (query == null)) {
                throw new ParserException(
                        ParserException.CANNOT_PARSE_COMMAND + command);
            }

            final PgView view = new PgView(ParserUtils.getObjectName(viewName));
            view.setColumnNames(columnNames);
            view.setQuery(query);

            final PgSchema schema =
                database.getSchema(
                        ParserUtils.getSchemaName(viewName, database));
            schema.addView(view);
        } else {
            throw new ParserException(
                    ParserException.CANNOT_PARSE_COMMAND + command);
        }
    }
}
