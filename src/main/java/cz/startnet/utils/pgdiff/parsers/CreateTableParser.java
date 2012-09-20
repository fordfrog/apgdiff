/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.Resources;
import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgConstraint;
import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;
import java.text.MessageFormat;

/**
 * Parses CREATE TABLE statements.
 *
 * @author fordfrog
 */
public class CreateTableParser {

    /**
     * Parses CREATE TABLE statement.
     *
     * @param database  database
     * @param statement CREATE TABLE statement
     */
    public static void parse(final PgDatabase database,
            final String statement) {
        final Parser parser = new Parser(statement);
        parser.expect("CREATE", "TABLE");

        // Optional IF NOT EXISTS, irrelevant for our purposes
        parser.expectOptional("IF", "NOT", "EXISTS");

        final String tableName = parser.parseIdentifier();
        final PgTable table = new PgTable(ParserUtils.getObjectName(tableName));
        final String schemaName =
                ParserUtils.getSchemaName(tableName, database);
        final PgSchema schema = database.getSchema(schemaName);

        if (schema == null) {
            throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindSchema"), schemaName,
                    statement));
        }

        schema.addTable(table);

        parser.expect("(");

        while (!parser.expectOptional(")")) {
            if (parser.expectOptional("CONSTRAINT")) {
                parseConstraint(parser, table);
            } else {
                parseColumn(parser, table);
            }

            if (parser.expectOptional(")")) {
                break;
            } else {
                parser.expect(",");
            }
        }

        while (!parser.expectOptional(";")) {
            if (parser.expectOptional("INHERITS")) {
                parseInherits(parser, table);
            } else if (parser.expectOptional("WITHOUT")) {
                table.setWith("OIDS=false");
            } else if (parser.expectOptional("WITH")) {
                if (parser.expectOptional("OIDS")
                        || parser.expectOptional("OIDS=true")) {
                    table.setWith("OIDS=true");
                } else if (parser.expectOptional("OIDS=false")) {
                    table.setWith("OIDS=false");
                } else {
                    table.setWith(parser.getExpression());
                }
            } else if (parser.expectOptional("TABLESPACE")) {
                table.setTablespace(parser.parseString());
            } else {
                parser.throwUnsupportedCommand();
            }
        }
    }

    /**
     * Parses INHERITS.
     *
     * @param parser parser
     * @param table  pg table
     */
    private static void parseInherits(final Parser parser,
            final PgTable table) {
        parser.expect("(");

        while (!parser.expectOptional(")")) {
            table.addInherits(
                    ParserUtils.getObjectName(parser.parseIdentifier()));

            if (parser.expectOptional(")")) {
                break;
            } else {
                parser.expect(",");
            }
        }
    }

    /**
     * Parses CONSTRAINT definition.
     *
     * @param parser parser
     * @param table  table
     */
    private static void parseConstraint(final Parser parser,
            final PgTable table) {
        final PgConstraint constraint = new PgConstraint(
                ParserUtils.getObjectName(parser.parseIdentifier()));
        table.addConstraint(constraint);
        constraint.setDefinition(parser.getExpression());
        constraint.setTableName(table.getName());
    }

    /**
     * Parses column definition.
     *
     * @param parser parser
     * @param table  table
     */
    private static void parseColumn(final Parser parser, final PgTable table) {
        final PgColumn column = new PgColumn(
                ParserUtils.getObjectName(parser.parseIdentifier()));
        table.addColumn(column);
        column.parseDefinition(parser.getExpression());
    }

    /**
     * Creates a new instance of CreateTableParser.
     */
    private CreateTableParser() {
    }
}
