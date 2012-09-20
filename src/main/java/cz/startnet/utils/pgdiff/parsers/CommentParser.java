/**
 * Copyright 2010 StartNet s.r.o.
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.Resources;
import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgConstraint;
import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgFunction;
import cz.startnet.utils.pgdiff.schema.PgIndex;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgSequence;
import cz.startnet.utils.pgdiff.schema.PgTable;
import cz.startnet.utils.pgdiff.schema.PgTrigger;
import cz.startnet.utils.pgdiff.schema.PgView;
import java.text.MessageFormat;

/**
 * COMMENT parser.
 *
 * @author fordfrog
 */
public class CommentParser {

    /**
     * Parses COMMENT statements.
     *
     * @param database                database
     * @param statement               COMMENT statement
     * @param outputIgnoredStatements whether ignored statements should be
     *                                output into the diff
     */
    public static void parse(final PgDatabase database,
            final String statement, final boolean outputIgnoredStatements) {
        final Parser parser = new Parser(statement);
        parser.expect("COMMENT", "ON");

        if (parser.expectOptional("TABLE")) {
            parseTable(parser, database);
        } else if (parser.expectOptional("COLUMN")) {
            parseColumn(parser, database);
        } else if (parser.expectOptional("CONSTRAINT")) {
            parseConstraint(parser, database);
        } else if (parser.expectOptional("DATABASE")) {
            parseDatabase(parser, database);
        } else if (parser.expectOptional("FUNCTION")) {
            parseFunction(parser, database);
        } else if (parser.expectOptional("INDEX")) {
            parseIndex(parser, database);
        } else if (parser.expectOptional("SCHEMA")) {
            parseSchema(parser, database);
        } else if (parser.expectOptional("SEQUENCE")) {
            parseSequence(parser, database);
        } else if (parser.expectOptional("TRIGGER")) {
            parseTrigger(parser, database);
        } else if (parser.expectOptional("VIEW")) {
            parseView(parser, database);
        } else if (outputIgnoredStatements) {
            database.addIgnoredStatement(statement);
        }
    }

    /**
     * Parses COMMENT ON TABLE.
     *
     * @param parser   parser
     * @param database database
     */
    private static void parseTable(final Parser parser,
            final PgDatabase database) {
        final String tableName = parser.parseIdentifier();
        final String objectName = ParserUtils.getObjectName(tableName);
        final String schemaName =
                ParserUtils.getSchemaName(tableName, database);

        final PgTable table =
                database.getSchema(schemaName).getTable(objectName);

        parser.expect("IS");
        table.setComment(getComment(parser));
        parser.expect(";");
    }

    /**
     * Parses COMMENT ON CONSTRAINT.
     *
     * @param parser   parser
     * @param database database
     */
    private static void parseConstraint(final Parser parser,
            final PgDatabase database) {
        final String constraintName =
                ParserUtils.getObjectName(parser.parseIdentifier());

        parser.expect("ON");

        final String tableName = parser.parseIdentifier();
        final String objectName = ParserUtils.getObjectName(tableName);
        final String schemaName =
                ParserUtils.getSchemaName(constraintName, database);

        final PgConstraint constraint = database.getSchema(schemaName).
                getTable(objectName).getConstraint(constraintName);

        parser.expect("IS");
        constraint.setComment(getComment(parser));
        parser.expect(";");
    }

    /**
     * Parses COMMENT ON DATABASE.
     *
     * @param parser   parser
     * @param database database
     */
    private static void parseDatabase(final Parser parser,
            final PgDatabase database) {
        parser.parseIdentifier();
        parser.expect("IS");
        database.setComment(getComment(parser));
        parser.expect(";");
    }

    /**
     * Parses COMMENT ON INDEX.
     *
     * @param parser   parser
     * @param database database
     */
    private static void parseIndex(final Parser parser,
            final PgDatabase database) {
        final String indexName = parser.parseIdentifier();
        final String objectName = ParserUtils.getObjectName(indexName);
        final String schemaName =
                ParserUtils.getSchemaName(indexName, database);
        final PgSchema schema = database.getSchema(schemaName);

        final PgIndex index = schema.getIndex(objectName);

        if (index == null) {
            final PgConstraint primaryKey = schema.getPrimaryKey(objectName);
            parser.expect("IS");
            primaryKey.setComment(getComment(parser));
            parser.expect(";");
        } else {
            parser.expect("IS");
            index.setComment(getComment(parser));
            parser.expect(";");
        }
    }

    /**
     * Parses COMMENT ON SCHEMA.
     *
     * @param parser   parser
     * @param database database
     */
    private static void parseSchema(final Parser parser,
            final PgDatabase database) {
        final String schemaName =
                ParserUtils.getObjectName(parser.parseIdentifier());
        final PgSchema schema = database.getSchema(schemaName);

        parser.expect("IS");
        schema.setComment(getComment(parser));
        parser.expect(";");
    }

    /**
     * Parses COMMENT ON SEQUENCE.
     *
     * @param parser   parser
     * @param database database
     */
    private static void parseSequence(final Parser parser,
            final PgDatabase database) {
        final String sequenceName = parser.parseIdentifier();
        final String objectName = ParserUtils.getObjectName(sequenceName);
        final String schemaName =
                ParserUtils.getSchemaName(sequenceName, database);

        final PgSequence sequence =
                database.getSchema(schemaName).getSequence(objectName);

        parser.expect("IS");
        sequence.setComment(getComment(parser));
        parser.expect(";");
    }

    /**
     * Parses COMMENT ON TRIGGER.
     *
     * @param parser   parser
     * @param database database
     */
    private static void parseTrigger(final Parser parser,
            final PgDatabase database) {
        final String triggerName =
                ParserUtils.getObjectName(parser.parseIdentifier());

        parser.expect("ON");

        final String tableName = parser.parseIdentifier();
        final String objectName = ParserUtils.getObjectName(tableName);
        final String schemaName =
                ParserUtils.getSchemaName(triggerName, database);

        final PgTrigger trigger = database.getSchema(schemaName).
                getTable(objectName).getTrigger(triggerName);

        parser.expect("IS");
        trigger.setComment(getComment(parser));
        parser.expect(";");
    }

    /**
     * Parses COMMENT ON VIEW.
     *
     * @param parser   parser
     * @param database database
     */
    private static void parseView(final Parser parser,
            final PgDatabase database) {
        final String viewName = parser.parseIdentifier();
        final String objectName = ParserUtils.getObjectName(viewName);
        final String schemaName =
                ParserUtils.getSchemaName(viewName, database);

        final PgView view = database.getSchema(schemaName).getView(objectName);

        parser.expect("IS");
        view.setComment(getComment(parser));
        parser.expect(";");
    }

    /**
     * Parses COMMENT ON COLUMN.
     *
     * @param parser   parser
     * @param database database
     */
    private static void parseColumn(final Parser parser,
            final PgDatabase database) {
        final String columnName = parser.parseIdentifier();
        final String objectName = ParserUtils.getObjectName(columnName);
        final String tableName = ParserUtils.getSecondObjectName(columnName);
        final String schemaName = ParserUtils.getThirdObjectName(columnName);
        final PgSchema schema = database.getSchema(schemaName);

        final PgTable table = schema.getTable(tableName);

        if (table == null) {
            final PgView view = schema.getView(tableName);
            parser.expect("IS");

            final String comment = getComment(parser);

            if (comment == null) {
                view.removeColumnComment(objectName);
            } else {
                view.addColumnComment(objectName, comment);
            }
            parser.expect(";");
        } else {
            final PgColumn column = table.getColumn(objectName);

            if (column == null) {
                throw new ParserException(MessageFormat.format(
                        Resources.getString("CannotFindColumnInTable"),
                        columnName, table.getName()));
            }

            parser.expect("IS");
            column.setComment(getComment(parser));
            parser.expect(";");
        }
    }

    /**
     * Parses COMMENT ON FUNCTION.
     *
     * @param parser   parser
     * @param database database
     */
    private static void parseFunction(final Parser parser,
            final PgDatabase database) {
        final String functionName = parser.parseIdentifier();
        final String objectName = ParserUtils.getObjectName(functionName);
        final String schemaName =
                ParserUtils.getSchemaName(functionName, database);
        final PgSchema schema = database.getSchema(schemaName);

        parser.expect("(");

        final PgFunction tmpFunction = new PgFunction();
        tmpFunction.setName(objectName);

        while (!parser.expectOptional(")")) {
            final String mode;

            if (parser.expectOptional("IN")) {
                mode = "IN";
            } else if (parser.expectOptional("OUT")) {
                mode = "OUT";
            } else if (parser.expectOptional("INOUT")) {
                mode = "INOUT";
            } else if (parser.expectOptional("VARIADIC")) {
                mode = "VARIADIC";
            } else {
                mode = null;
            }

            final int position = parser.getPosition();
            String argumentName = null;
            String dataType = parser.parseDataType();

            final int position2 = parser.getPosition();

            if (!parser.expectOptional(")") && !parser.expectOptional(",")) {
                parser.setPosition(position);
                argumentName =
                        ParserUtils.getObjectName(parser.parseIdentifier());
                dataType = parser.parseDataType();
            } else {
                parser.setPosition(position2);
            }

            final PgFunction.Argument argument = new PgFunction.Argument();
            argument.setDataType(dataType);
            argument.setMode(mode);
            argument.setName(argumentName);
            tmpFunction.addArgument(argument);

            if (parser.expectOptional(")")) {
                break;
            } else {
                parser.expect(",");
            }
        }

        final PgFunction function =
                schema.getFunction(tmpFunction.getSignature());

        parser.expect("IS");
        function.setComment(getComment(parser));
        parser.expect(";");
    }

    /**
     * Parses comment from parser. If comment is "null" string then null is
     * returned, otherwise the parsed string is returned.
     *
     * @param parser parser
     *
     * @return string or null
     */
    private static String getComment(final Parser parser) {
        final String comment = parser.parseString();

        if ("null".equalsIgnoreCase(comment)) {
            return null;
        }

        return comment;
    }

    /**
     * Creates new instance of CommentParser.
     */
    private CommentParser() {
    }
}
