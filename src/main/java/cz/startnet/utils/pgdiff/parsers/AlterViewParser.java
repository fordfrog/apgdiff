/**
 * Copyright 2010 StartNet s.r.o.
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgView;

/**
 * Parses ALTER VIEW commands.
 * 
 * @author fordfrog
 */
public class AlterViewParser {

    /**
     * Creates new instance of AlterViewParser.
     */
    private AlterViewParser() {
    }

    /**
     * Parses ALTER VIEW command.
     * 
     * @param database database
     * @param command ALTER VIEW command
     */
    public static void parse(final PgDatabase database, final String command) {
        final Parser parser = new Parser(command);
        parser.expect("ALTER", "VIEW");

        final String viewName = parser.parseIdentifier();
        final PgView view = database.getSchema(
                ParserUtils.getSchemaName(viewName, database)).getView(
                ParserUtils.getObjectName(viewName));

        while (!parser.expectOptional(";")) {
            if (parser.expectOptional("ALTER")) {
                parser.expectOptional("COLUMN");

                final String columnName =
                        ParserUtils.getObjectName(parser.parseIdentifier());

                if (parser.expectOptional("SET", "DEFAULT")) {
                    final String expression = parser.getExpression();
                    view.addColumnDefaultValue(columnName, expression);
                } else if (parser.expectOptional("DROP", "DEFAULT")) {
                    view.removeColumnDefaultValue(columnName);
                } else {
                    parser.throwUnsupportedCommand();
                }
            } else if (parser.expectOptional("OWNER", "TO")) {
                // we do not support OWNER TO so just consume the output
                parser.getExpression();
            } else {
                parser.throwUnsupportedCommand();
            }
        }
    }
}
