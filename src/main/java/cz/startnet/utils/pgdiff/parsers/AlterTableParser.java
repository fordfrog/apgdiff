package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgConstraint;
import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgTable;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses ALTER TABLE commands.
 *
 * @author fordfrog
 */
public class AlterTableParser {

    /**
     * Creates a new instance of AlterTableParser.
     */
    private AlterTableParser() {
    }

    /**
     * Parses ALTER TABLE command.
     *
     * @param database database
     * @param command ALTER TABLE command
     *
     * @throws ParserException Thrown if problem occured while parsing DDL.
     */
    public static void parse(final PgDatabase database, final String command) {
        final Parser parser = new Parser(command);
        parser.expect("ALTER", "TABLE");
        parser.expectOptional("ONLY");

        final String tableName = parser.parseIdentifier();

        final PgTable table = database.getSchema(
                ParserUtils.getSchemaName(tableName, database)).getTable(
                ParserUtils.getObjectName(tableName));

        while (!parser.expectOptional(";")) {
            if (parser.expectOptional("ALTER")) {
                parseAlterColumn(parser, table);
            } else if (parser.expectOptional("CLUSTER", "ON")) {
                table.setClusterIndexName(parser.parseIdentifier());
            } else if (parser.expectOptional("OWNER", "TO")) {
                // we do not parse this one so we just consume the expression
                parser.getExpression();
            } else if (parser.expectOptional("ADD")) {
                if (parser.expectOptional("FOREIGN", "KEY")) {
                    parseAddForeignKey(parser, table);
                } else if (parser.expectOptional("CONSTRAINT")) {
                    parseAddConstraint(parser, table);
                } else {
                    parser.throwUnsupportedCommand();
                }
            } else {
                parser.throwUnsupportedCommand();
            }

            if (parser.expectOptional(";")) {
                break;
            } else {
                parser.expect(",");
            }
        }
    }

    /**
     * Parses ADD CONSTRAINT action.
     *
     * @param parser parser
     * @param table pg table
     */
    private static void parseAddConstraint(final Parser parser,
            final PgTable table) {
        final String constraintName = parser.parseIdentifier();
        final PgConstraint constraint =
                new PgConstraint(constraintName);
        table.addConstraint(constraint);
        constraint.setDefinition(parser.getExpression());
        constraint.setTableName(table.getName());
    }

    /**
     * Parses ALTER COLUMN action.
     *
     * @param parser parser
     * @param table pg table
     */
    private static void parseAlterColumn(final Parser parser,
            final PgTable table) {
        parser.expectOptional("COLUMN");

        final String columnName = parser.parseIdentifier();

        if (parser.expectOptional("SET")) {
            if (parser.expectOptional("STATISTICS")) {
                final PgColumn col = table.getColumn(columnName);
                col.setStatistics(parser.parseInteger());
            } else if (parser.expectOptional("DEFAULT")) {
                final String defaultValue = parser.getExpression();

                if (table.containsColumn(columnName)) {
                    final PgColumn column = table.getColumn(columnName);
                    column.setDefaultValue(defaultValue);
                } else {
                    throw new ParserException("Cannot find column '"
                            + columnName + " 'in table '" + table.getName()
                            + "'");
                }
            } else {
                parser.throwUnsupportedCommand();
            }
        } else {
            parser.throwUnsupportedCommand();
        }
    }

    /**
     * Parses ADD FOREIGN KEY action.
     *
     * @param parser parser
     * @param table pg table
     */
    private static void parseAddForeignKey(final Parser parser,
            final PgTable table) {
        final List<String> columnNames = new ArrayList<String>(1);
        parser.expect("(");

        while (!parser.expectOptional(")")) {
            columnNames.add(parser.parseIdentifier());

            if (parser.expectOptional(")")) {
                break;
            } else {
                parser.expect(",");
            }
        }

        final String constraintName = ParserUtils.generateName(
                table.getName() + "_", columnNames, "_fkey");
        final PgConstraint constraint =
                new PgConstraint(constraintName);
        table.addConstraint(constraint);
        constraint.setDefinition(parser.getExpression());
        constraint.setTableName(table.getName());
    }
}
