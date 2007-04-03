/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgIndex;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;

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
        Pattern.compile(
                "CREATE[\\s]+INDEX[\\s]+\"?([^\\s\"]+)\"?[\\s]+"
                + "ON[\\s]+\"?([^\\s\"(]+)\"?[\\s]*([^;]+)[;]?",
                Pattern.CASE_INSENSITIVE);

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
     * @param command CREATE INDEX command
     *
     * @throws ParserException Thrown if problem occured while parsing the
     *         command.
     */
    public static void parse(final PgSchema schema, final String command) {
        final Matcher matcher = PATTERN.matcher(command.trim());

        if (matcher.matches()) {
            final String indexName = matcher.group(1);
            final String tableName = matcher.group(2);
            final String def = matcher.group(3);

            if ((indexName == null) || (tableName == null) || (def == null)) {
                throw new ParserException(
                        ParserException.CANNOT_PARSE_COMMAND + command);
            }

            final PgTable table = schema.getTable(tableName.trim());
            final PgIndex index = new PgIndex(indexName);
            table.addIndex(index);
            index.setDefinition(def.trim());
            index.setTableName(table.getName());
        } else {
            throw new ParserException(
                    ParserException.CANNOT_PARSE_COMMAND + command);
        }
    }
}
