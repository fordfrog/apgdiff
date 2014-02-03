/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.Resources;
import cz.startnet.utils.pgdiff.schema.*;

import java.text.MessageFormat;

/**
 * Parses CREATE INDEX statements.
 *
 * @author fordfrog
 */
public class CreateIndexParser {

    /**
     * Parses CREATE INDEX statement.
     *
     * @param database  database
     * @param statement CREATE INDEX statement
     */
    public static void parse(final PgDatabase database,
            final String statement) {
        final Parser parser = new Parser(statement);
        parser.expect("CREATE");

        final boolean unique = parser.expectOptional("UNIQUE");

        parser.expect("INDEX");
        parser.expectOptional("CONCURRENTLY");

        final String indexName =
                ParserUtils.getObjectName(parser.parseIdentifier());

        parser.expect("ON");

        final String tableName = parser.parseIdentifier();
        final String definition = parser.getRest();
        final String schemaName =
                ParserUtils.getSchemaName(tableName, database);
        final PgSchema schema = database.getSchema(schemaName);

        if (schema == null) {
            throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindSchema"), schemaName,
                    statement));
        }

        final String objectName = ParserUtils.getObjectName(tableName);
        final PgTable table = schema.getTable(objectName);
        final PgView view = schema.getView(objectName);
        final PgIndex index = new PgIndex(indexName);

        if (table != null) {
            table.addIndex(index);
        }
        else if (view != null) {
            view.addIndex(index);
        }
        else {
            throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindObject"), tableName,
                    statement));
        }

        schema.addIndex(index);
        index.setDefinition(definition.trim());
        index.setTableName(objectName);
        index.setUnique(unique);
    }

    /**
     * Creates a new instance of CreateIndexParser.
     */
    private CreateIndexParser() {
    }
}
