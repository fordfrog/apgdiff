/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgExtension;
import cz.startnet.utils.pgdiff.schema.PgSchema;

/**
 * Parses CREATE EXTENSION statements.
 *
 * @author atila
 */
public class CreateExtensionParser {

    /**
     * Parses CREATE EXTENSION statement.
     *
     * @param database  database
     * @param statement CREATE EXTENSION statement
     */
    public static void parse(final PgDatabase database,
            final String statement) {
        final Parser parser = new Parser(statement);
        parser.expect("CREATE", "EXTENSION");
        parser.expectOptional("IF", "NOT", "EXISTS");
        final String extensionName = parser.parseIdentifier();
        
        final PgExtension extension = new PgExtension(extensionName);

        parser.expectOptional("WITH");
        if (parser.expectOptional("SCHEMA")) {
            extension.setSchema(new PgSchema(parser.parseString()));
        }
        
        if (parser.expectOptional("VERSION")) {
            extension.setVersion(parser.parseString());
        }
        
        if (parser.expectOptional("FROM")) {
            extension.setFrom(parser.parseString());
        }
        database.addExtension(extension);
    }

    /**
     * Creates a new instance of CreateExtensionParser.
     */
    private CreateExtensionParser() {
    }
}
