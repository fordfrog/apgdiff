/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.Resources;
import cz.startnet.utils.pgdiff.schema.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Parses ALTER TABLE statements.
 *
 * @author fordfrog
 */
public class AlterRelationParser {

    /**
     * Parses ALTER TABLE statement.
     *
     * @param database                database
     * @param statement               ALTER TABLE statement
     * @param outputIgnoredStatements whether ignored statements should be
     *                                output in the diff
     */
    public static void parse(final PgDatabase database,
            final String statement, final boolean outputIgnoredStatements) {
        final Parser parser = new Parser(statement);
        parser.expect("ALTER");

        /*
         * PostgreSQL allows using ALTER TABLE on views as well as other
         * relation types, so we just ignore type here and derive its type from
         * the original CREATE command.
         */
        parser.expectOptional("FOREIGN");
        	//OK FOREIGN TABLE
        if (parser.expectOptional("TABLE")) {
            parser.expectOptional("ONLY");
        } else if (parser.expectOptional("MATERIALIZED", "VIEW")
                || parser.expectOptional("VIEW")) {
            // OK, view
        } else {
            parser.throwUnsupportedCommand();
        }

        final String relName = parser.parseIdentifier();
        final String schemaName =
                ParserUtils.getSchemaName(relName, database);
        final PgSchema schema = database.getSchema(schemaName);

        if (schema == null) {
            throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindSchema"), schemaName,
                    statement));
        }

        final String objectName = ParserUtils.getObjectName(relName);
        final PgRelation rel = schema.getRelation(objectName);

        if (rel == null) {
            final PgSequence sequence = schema.getSequence(objectName);

            if (sequence != null) {
                parseSequence(parser, sequence, outputIgnoredStatements,
                        relName, database);
                return;
            }

            throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindObject"), relName,
                    statement));
        }

        PgTable table = null;
        if (rel instanceof PgTable) {
            table = (PgTable) rel;
        }

        while (!parser.expectOptional(";")) {
            if (parser.expectOptional("ALTER")) {
                parseAlterColumn(parser, rel);
            } else if (parser.expectOptional("CLUSTER", "ON")) {
                rel.setClusterIndexName(
                        ParserUtils.getObjectName(parser.parseIdentifier()));
            } else if (parser.expectOptional("OWNER", "TO")) {
                rel.setOwnerTo(parser.parseIdentifier());
            } else if (table != null && parser.expectOptional("ADD")) {
                if (parser.expectOptional("FOREIGN", "KEY")) {
                    parseAddForeignKey(parser, table);
                } else if (parser.expectOptional("CONSTRAINT")) {
                    parseAddConstraint(parser, table, schema);
                } else {
                    parser.throwUnsupportedCommand();
                }
            } else if (table != null
                && parser.expectOptional("ENABLE", "ROW", "LEVEL", "SECURITY")) {
                table.setRLSEnabled(true);
            } else if (table != null
                && parser.expectOptional("DISABLE", "ROW", "LEVEL", "SECURITY")) {
                table.setRLSEnabled(false);
            } else if (table != null
                && parser.expectOptional("FORCE", "ROW", "LEVEL", "SECURITY")) {
                table.setRLSForced(true);
            } else if (table != null
                && parser.expectOptional("NO", "FORCE", "ROW", "LEVEL", "SECURITY")) {
                table.setRLSForced(false);
            } else if (table != null && parser.expectOptional("ENABLE")) {
                parseEnable(
                        parser, outputIgnoredStatements, relName, database);
            } else if (table != null && parser.expectOptional("DISABLE")) {
                parseDisable(
                        parser, outputIgnoredStatements, relName, database);
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
     * Parses ENABLE statements.
     *
     * @param parser                  parser
     * @param outputIgnoredStatements whether ignored statements should be
     *                                output in the diff
     * @param tableName               table name as it was specified in the
     *                                statement
     * @param database                database information
     *
     */
    private static void parseEnable(final Parser parser,
            final boolean outputIgnoredStatements, final String tableName,
            final PgDatabase database) {
        if (parser.expectOptional("REPLICA")) {
            if (parser.expectOptional("TRIGGER")) {
                if (outputIgnoredStatements) {
                    database.addIgnoredStatement("ALTER TABLE " + tableName
                            + " ENABLE REPLICA TRIGGER "
                            + parser.parseIdentifier() + ';');
                } else {
                    parser.parseIdentifier();
                }
            } else if (parser.expectOptional("RULE")) {
                if (outputIgnoredStatements) {
                    database.addIgnoredStatement("ALTER TABLE " + tableName
                            + " ENABLE REPLICA RULE "
                            + parser.parseIdentifier() + ';');
                } else {
                    parser.parseIdentifier();
                }
            } else {
                parser.throwUnsupportedCommand();
            }
        } else if (parser.expectOptional("ALWAYS")) {
            if (parser.expectOptional("TRIGGER")) {
                if (outputIgnoredStatements) {
                    database.addIgnoredStatement("ALTER TABLE " + tableName
                            + " ENABLE ALWAYS TRIGGER "
                            + parser.parseIdentifier() + ';');
                } else {
                    parser.parseIdentifier();
                }
            } else if (parser.expectOptional("RULE")) {
                if (outputIgnoredStatements) {
                    database.addIgnoredStatement("ALTER TABLE " + tableName
                            + " ENABLE RULE " + parser.parseIdentifier() + ';');
                } else {
                    parser.parseIdentifier();
                }
            } else {
                parser.throwUnsupportedCommand();
            }
        }
    }

    /**
     * Parses DISABLE statements.
     *
     * @param parser                  parser
     * @param outputIgnoredStatements whether ignored statements should be
     *                                output in the diff
     * @param tableName               table name as it was specified in the
     *                                statement
     * @param database                database information
     *
     */
    private static void parseDisable(final Parser parser,
            final boolean outputIgnoredStatements, final String tableName,
            final PgDatabase database) {
        if (parser.expectOptional("TRIGGER")) {
            if (outputIgnoredStatements) {
                database.addIgnoredStatement("ALTER TABLE " + tableName
                        + " DISABLE TRIGGER " + parser.parseIdentifier() + ';');
            } else {
                parser.parseIdentifier();
            }
        } else if (parser.expectOptional("RULE")) {
            if (outputIgnoredStatements) {
                database.addIgnoredStatement("ALTER TABLE " + tableName
                        + " DISABLE RULE " + parser.parseIdentifier() + ';');
            } else {
                parser.parseIdentifier();
            }
        } else {
            parser.throwUnsupportedCommand();
        }
    }

    /**
     * Parses ADD CONSTRAINT action.
     *
     * @param parser parser
     * @param table  table
     * @param schema schema
     */
    private static void parseAddConstraint(final Parser parser,
            final PgTable table, final PgSchema schema) {
        final String constraintName =
                ParserUtils.getObjectName(parser.parseIdentifier());
        final PgConstraint constraint = new PgConstraint(constraintName);
        constraint.setTableName(table.getName());
        table.addConstraint(constraint);

        if (parser.expectOptional("PRIMARY", "KEY")) {
            schema.addPrimaryKey(constraint);
            constraint.setDefinition("PRIMARY KEY " + parser.getExpression());
        } else {
            constraint.setDefinition(parser.getExpression());
        }
    }

    /**
     * Parses ALTER COLUMN action.
     *
     * @param parser parser
     * @param rel    view/table
     */
    private static void parseAlterColumn(final Parser parser,
            final PgRelation rel) {
        parser.expectOptional("COLUMN");

        final String columnName =
                ParserUtils.getObjectName(parser.parseIdentifier());

        if (parser.expectOptional("SET")) {
            if (parser.expectOptional("STATISTICS")) {
                final PgColumn column = rel.getColumn(columnName);

                if (column == null) {
                    throw new RuntimeException(MessageFormat.format(
                            Resources.getString("CannotFindTableColumn"),
                            columnName, rel.getName(), parser.getString()));
                }

                column.setStatistics(parser.parseInteger());
            } else if (parser.expectOptional("NOT NULL")) {
                if (rel.containsColumn(columnName)) {
                    final PgColumn column = rel.getColumn(columnName);
                    if (column == null) {
                        throw new RuntimeException(MessageFormat.format(
                                Resources.getString("CannotFindTableColumn"),
                                columnName, rel.getName(), parser.getString()));
                    }
                    column.setNullValue(false);
                } else if (rel.containsInheritedColumn(columnName)) {
                    final PgInheritedColumn inheritedColumn = rel.getInheritedColumn(columnName);
                    if (inheritedColumn == null) {
                        throw new RuntimeException(MessageFormat.format(
                                Resources.getString("CannotFindTableColumn"),
                                columnName, rel.getName(), parser.getString()));
                    }

                    inheritedColumn.setNullValue(false);
                } else {
                    throw new ParserException(MessageFormat.format(
                            Resources.getString("CannotFindColumnInTable"),
                            columnName, rel.getName()));
                }
            } else if (parser.expectOptional("DEFAULT")) {
                final String defaultValue = parser.getExpression();

                if (rel.containsColumn(columnName)) {
                    final PgColumn column = rel.getColumn(columnName);

                if (column == null) {
                    throw new RuntimeException(MessageFormat.format(
                            Resources.getString("CannotFindTableColumn"),
                            columnName, rel.getName(),
                            parser.getString()));
                }

                column.setDefaultValue(defaultValue);
                } else if (rel.containsInheritedColumn(columnName)) {
                    final PgInheritedColumn column = rel.getInheritedColumn(columnName);

                    if (column == null) {
                        throw new RuntimeException(MessageFormat.format(
                                Resources.getString("CannotFindTableColumn"),
                                columnName, rel.getName(),
                                parser.getString()));
                    }

                    column.setDefaultValue(defaultValue);
                } else {
                    throw new ParserException(MessageFormat.format(
                            Resources.getString("CannotFindColumnInTable"),
                            columnName, rel.getName()));
                }
            } else if (parser.expectOptional("STORAGE")) {
                final PgColumn column = rel.getColumn(columnName);

                if (column == null) {
                    throw new RuntimeException(MessageFormat.format(
                            Resources.getString("CannotFindTableColumn"),
                            columnName, rel.getName(), parser.getString()));
                }

                if (parser.expectOptional("PLAIN")) {
                    column.setStorage("PLAIN");
                } else if (parser.expectOptional("EXTERNAL")) {
                    column.setStorage("EXTERNAL");
                } else if (parser.expectOptional("EXTENDED")) {
                    column.setStorage("EXTENDED");
                } else if (parser.expectOptional("MAIN")) {
                    column.setStorage("MAIN");
                } else {
                    parser.throwUnsupportedCommand();
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
     * @param table  pg table
     */
    private static void parseAddForeignKey(final Parser parser,
            final PgTable table) {
        final List<String> columnNames = new ArrayList<String>(1);
        parser.expect("(");

        while (!parser.expectOptional(")")) {
            columnNames.add(
                    ParserUtils.getObjectName(parser.parseIdentifier()));

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

    /**
     * Parses ALTER TABLE sequence.
     *
     * @param parser                  parser
     * @param sequence                sequence
     * @param outputIgnoredStatements whether ignored statements should be
     *                                output in the diff
     * @param sequenceName            sequence name as it was specified in the
     *                                statement
     * @param database                database information
     */
    private static void parseSequence(final Parser parser,
            final PgSequence sequence, final boolean outputIgnoredStatements,
            final String sequenceName, final PgDatabase database) {
        while (!parser.expectOptional(";")) {
            if (parser.expectOptional("OWNER", "TO")) {
                // we do not parse this one so we just consume the identifier
                if (outputIgnoredStatements) {
                    database.addIgnoredStatement("ALTER TABLE " + sequenceName
                            + " OWNER TO " + parser.parseIdentifier() + ';');
                } else {
                    parser.parseIdentifier();
                }
            } else {
                parser.throwUnsupportedCommand();
            }
        }
    }

    /**
     * Creates a new instance of AlterTableParser.
     */
    private AlterRelationParser() {
    }
}
