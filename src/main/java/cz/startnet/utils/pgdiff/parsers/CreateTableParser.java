/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgConstraint;
import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses CREATE TABLE commands.
 *
 * @author fordfrog
 * @version $Id$
 */
public class CreateTableParser {
    /**
     * Pattern for getting table name from CREATE TABLE.
     */
    private static final Pattern PATTERN_TABLE_NAME =
        Pattern.compile(
                "CREATE[\\s]+TABLE[\\s]+\"?([^\\s\"]+)\"?[\\s]*\\(",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for getting CONSTRAINT parameters.
     */
    private static final Pattern PATTERN_CONSTRAINT =
        Pattern.compile(
                "CONSTRAINT[\\s]+\"?([^\\s\"]+)\"?[\\s]+(.*)",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for parsing column definition.
     */
    private static final Pattern PATTERN_COLUMN =
        Pattern.compile(
                "\"?([^\\s\"]+)\"?[\\s]+(.*)",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for parsing INHERITS.
     */
    private static final Pattern PATTERN_INHERITS =
        Pattern.compile("INHERITS[\\s]+([^;]+)[;]?", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for checking whether string contains WITH OIDS string.
     */
    private static final Pattern PATTERN_WITH_OIDS =
        Pattern.compile(".*WITH[\\s]+OIDS.*", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for checking whether string contains WITHOUT OIDS
     * string.
     */
    private static final Pattern PATTERN_WITHOUT_OIDS =
        Pattern.compile(".*WITHOUT[\\s]+OIDS.*", Pattern.CASE_INSENSITIVE);

    /**
     * Creates a new instance of CreateTableParser.
     */
    private CreateTableParser() {
        super();
    }

    /**
     * Parses CREATE TABLE command.
     *
     * @param database database
     * @param command CREATE TABLE command
     *
     * @throws ParserException Thrown if problem occured while parsing DDL.
     * @throws RuntimeException DOCUMENT ME!
     */
    public static void parse(final PgDatabase database, final String command) {
        String line = command;
        final Matcher matcher = PATTERN_TABLE_NAME.matcher(line);
        final String tableName;

        if (matcher.find()) {
            tableName = matcher.group(1).trim();
            line =
                ParserUtils.removeSubString(
                        line,
                        matcher.start(),
                        matcher.end());
        } else {
            throw new ParserException(
                    ParserException.CANNOT_PARSE_COMMAND + line);
        }

        final PgTable table = new PgTable(ParserUtils.getObjectName(tableName));
        final String schemaName =
            ParserUtils.getSchemaName(tableName, database);
        final PgSchema schema = database.getSchema(schemaName);

        if (schema == null) {
            throw new RuntimeException(
                    "Cannot get schema '" + schemaName
                    + "'. Need to issue 'CREATE SCHEMA " + schemaName
                    + ";' before 'CREATE TABLE " + tableName + "...;'?");
        }

        schema.addTable(table);
        parseRows(table, ParserUtils.removeLastSemicolon(line));
    }

    /**
     * Parses COLUMN and other DDL within '(' and ')' in CREATE TABLE
     * definition.
     *
     * @param table table being parsed
     * @param line line being processed
     *
     * @throws ParserException Thrown if problem occured while parsing DDL.
     */
    private static void parseColumnDefs(final PgTable table, final String line) {
        if (line.length() > 0) {
            boolean matched = false;
            Matcher matcher = PATTERN_CONSTRAINT.matcher(line.trim());

            if (matcher.matches()) {
                final PgConstraint constraint =
                    new PgConstraint(matcher.group(1).trim());
                table.addConstraint(constraint);
                constraint.setDefinition(matcher.group(2).trim());
                constraint.setTableName(table.getName());
                matched = true;
            }

            if (!matched) {
                matcher = PATTERN_COLUMN.matcher(line);

                if (matcher.matches()) {
                    final PgColumn column =
                        new PgColumn(matcher.group(1).trim());
                    table.addColumn(column);
                    column.parseDefinition(matcher.group(2).trim());
                    matched = true;
                }
            }

            if (!matched) {
                throw new ParserException(
                        ParserException.CANNOT_PARSE_COMMAND + line);
            }
        }
    }

    /**
     * Parses definitions that are present after column definition is
     * closed with ')'.
     *
     * @param table table being parsed
     * @param commands commands being processed
     *
     * @return true if the command was the last command for CREATE TABLE,
     *         otherwise false
     */
    private static String parsePostColumns(
        final PgTable table,
        final String commands) {
        String line = commands;
        final Matcher matcher = PATTERN_INHERITS.matcher(line);

        if (matcher.find()) {
            table.setInherits(matcher.group(1).trim());
            line =
                ParserUtils.removeSubString(
                        line,
                        matcher.start(),
                        matcher.end());
        }

        if (PATTERN_WITH_OIDS.matcher(line).matches()) {
            table.setWithOIDS(true);
            line = ParserUtils.removeSubString(line, "WITH OIDS");
        } else if (PATTERN_WITHOUT_OIDS.matcher(line).matches()) {
            table.setWithOIDS(false);
            line = ParserUtils.removeSubString(line, "WITHOUT OIDS");
        }

        return line;
    }

    /**
     * Parses all rows in CREATE TABLE command.
     *
     * @param table table being parsed
     * @param command command without 'CREATE SEQUENCE ... (' string
     *
     * @throws ParserException Thrown if problem occured with parsing of DDL.
     */
    private static void parseRows(final PgTable table, final String command) {
        String line = command;
        boolean postColumns = false;

        try {
            while (line.length() > 0) {
                final int commandEnd = ParserUtils.getCommandEnd(line, 0);
                final String subCommand = line.substring(0, commandEnd).trim();

                if (postColumns) {
                    line = parsePostColumns(table, subCommand);

                    break;
                } else if (line.charAt(commandEnd) == ')') {
                    postColumns = true;
                }

                parseColumnDefs(table, subCommand);
                line =
                    (commandEnd >= line.length()) ? ""
                                                  : line.substring(
                            commandEnd + 1);
            }
        } catch (RuntimeException ex) {
            throw new ParserException(
                    ParserException.CANNOT_PARSE_COMMAND + "CREATE TABLE "
                    + table.getName() + " ( " + command,
                    ex);
        }

        line = line.trim();

        if (line.length() > 0) {
            throw new ParserException(
                    "Cannot parse CREATE TABLE '" + table.getName()
                    + "' - do not know how to parse '" + line + "'");
        }
    }
}
