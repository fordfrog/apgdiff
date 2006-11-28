/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgSchema;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses CREATE INDEX commands.
 *
 * @author fordfrog
 * @version $Id$
 */
public class CreateIndexParser {
    /**
     * Pattern for parsing CREATE INDEX definition.
     */
    private static final Pattern PATTERN =
        Pattern.compile("CREATE INDEX ([^ ]+) ON ([^ ]+) ([^;]+)[;]?");

    /**
     * Creates a new instance of CreateIndexParser.
     */
    private CreateIndexParser() {
        super();
    }

    /**
     * Parses CREATE INDEX command.
     *
     * @param schema schema to be filled
     * @param line first line read
     *
     * @throws ParserException Thrown if problem occured while parsing the
     *         command.
     */
    public static void parse(final PgSchema schema, final String line) {
        final Matcher matcher = PATTERN.matcher(line.trim());

        if (matcher.matches()) {
            final String indexName = matcher.group(1).trim();
            final String tableName = matcher.group(2).trim();
            final String def = matcher.group(3).trim();

            if ((indexName == null) || (tableName == null) || (def == null)) {
                throw new ParserException(
                        ParserException.CANNOT_PARSE_COMMAND + line);
            }

            schema.getTable(tableName).getIndex(indexName).setDefinition(def);
        } else {
            throw new ParserException(
                    ParserException.CANNOT_PARSE_COMMAND + line);
        }
    }
}
