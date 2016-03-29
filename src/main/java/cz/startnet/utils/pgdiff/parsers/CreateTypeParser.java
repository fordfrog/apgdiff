/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.Resources;
import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgType;
import java.text.MessageFormat;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Parses CREATE TABLE statements.
 *
 * @author fordfrog
 */
public class CreateTypeParser {

    /**
     * Parses CREATE TYPE statement.
     *
     * @param database database
     * @param statement CREATE TYPE statement
     */
    public static void parse(final PgDatabase database,
            final String statement) {
        
        
        final Parser parser = new Parser(statement);
        parser.expect("CREATE", "TYPE");

        final String typeName = parser.parseIdentifier();
        final PgType type = new PgType(ParserUtils.getObjectName(typeName));
        final String schemaName
                = ParserUtils.getSchemaName(typeName, database);
        final PgSchema schema = database.getSchema(schemaName);

        if (schema == null) {
            throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindSchema"), schemaName,
                    statement));
        }

        schema.addType(type);

        parser.expect("AS");
        if (parser.expectOptional("ENUM")) {
            type.setIsEnum(true);
        }
        parser.expect("(");
        while (!parser.expectOptional(")")) {
            if (type.getIsEnum()) {
                
                String name = parser.getExpression();
                type.addEnumValue(name);
                
                if (parser.expectOptional(")")) {
                    break;
                } else {
                    parser.expect(",");
                }
            } else {
                parseColumn(parser, type);

                if (parser.expectOptional(")")) {
                    break;
                } else {
                    parser.expect(",");
                }
            }
        }

        while (!parser.expectOptional(";")) {

        }
    }

    /**
     * Parses column definition.
     *
     * @param parser parser
     * @param type type
     */
    private static void parseColumn(final Parser parser, final PgType type) {
        final PgColumn column = new PgColumn(
                ParserUtils.getObjectName(parser.parseIdentifier()));
        type.addColumn(column);
        column.parseDefinition(parser.getExpression());
    }

    /**
     * Creates a new instance of CreateTableParser.
     */
    private CreateTypeParser() {
    }
}
