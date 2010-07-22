package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgView;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses CREATE VIEW commands.
 *
 * @author fordfrog
 */
public class CreateViewParser {

    /**
     * Creates a new instance of CreateViewParser.
     */
    private CreateViewParser() {
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
        final Parser parser = new Parser(command);
        parser.expect("CREATE");
        parser.expectOptional("OR", "REPLACE");
        parser.expect("VIEW");

        final String viewName = parser.parseIdentifier();

        final boolean columnsExist = parser.expectOptional("(");
        final List<String> columnNames = new ArrayList<String>(10);

        if (columnsExist) {
            while (!parser.expectOptional(")")) {
                columnNames.add(parser.parseIdentifier());
                parser.expectOptional(",");
            }
        }

        parser.expect("AS");

        final String query = parser.getRest();

        final PgView view = new PgView(ParserUtils.getObjectName(viewName));
        view.setColumnNames(columnNames);
        view.setQuery(query);

        final PgSchema schema = database.getSchema(
                ParserUtils.getSchemaName(viewName, database));
        schema.addView(view);
    }
}
