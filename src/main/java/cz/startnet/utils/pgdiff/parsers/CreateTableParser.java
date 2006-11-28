/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.loader.FileException;
import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;

import java.io.BufferedReader;
import java.io.IOException;

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
        Pattern.compile("CREATE TABLE ([^ ]+) \\(");

    /**
     * Pattern for getting CONSTRAINT parameters.
     */
    private static final Pattern PATTERN_CONSTRAINT =
        Pattern.compile("CONSTRAINT ([^ ]+) (.*)");

    /**
     * Pattern for parsing column definition.
     */
    private static final Pattern PATTERN_COLUMN =
        Pattern.compile("([^ ]+) (.*)");

    /**
     * Pattern for parsing INHERITS.
     */
    private static final Pattern PATTERN_INHERITS =
        Pattern.compile("INHERITS ([^;]+)[;]?");

    /**
     * Creates a new instance of CreateTableParser.
     */
    private CreateTableParser() {
        super();
    }

    /**
     * Parses CREATE TABLE command.
     *
     * @param schema schema to be filled
     * @param reader reader of the dump file
     * @param line first line read
     *
     * @throws ParserException Thrown if problem occured while parsing DDL.
     */
    public static void parse(
        final PgSchema schema,
        final BufferedReader reader,
        final String line) {
        final Matcher matcher = PATTERN_TABLE_NAME.matcher(line);
        final String tableName;

        if (matcher.matches()) {
            tableName = matcher.group(1);
        } else {
            throw new ParserException(
                    ParserException.CANNOT_PARSE_COMMAND + line);
        }

        final PgTable table = schema.getTable(tableName);
        parseRows(table, reader);
    }

    /**
     * Parses COLUMN and other DDL within '(' and ')' in CREATE TABLE
     * definition.
     *
     * @param table table being parsed
     * @param line line being processed
     *
     * @return true if the command was the last command for CREATE TABLE,
     *         otherwise false
     *
     * @throws ParserException Thrown if problem occured while parsing DDL.
     */
    private static boolean parseColumnDefs(
        final PgTable table,
        final String line) {
        boolean last = false;
        String adjLine = line;

        if (adjLine.endsWith(",")) {
            adjLine = adjLine.substring(0, adjLine.length() - 1).trim();
        } else if (adjLine.endsWith(");")) {
            adjLine = adjLine.substring(0, adjLine.length() - 2).trim();
            last = true;
        }

        if (adjLine.length() > 0) {
            if (adjLine.startsWith("CONSTRAINT ")) {
                final Matcher matcher =
                    PATTERN_CONSTRAINT.matcher(adjLine.trim());

                if (matcher.matches()) {
                    table.getConstraint(matcher.group(1))
                         .setDefinition(matcher.group(2));
                } else {
                    throw new ParserException(
                            ParserException.CANNOT_PARSE_COMMAND + line);
                }
            } else {
                final Matcher matcher = PATTERN_COLUMN.matcher(adjLine);

                if (matcher.matches()) {
                    final PgColumn column = table.getColumn(matcher.group(1));
                    column.parseDefinition(matcher.group(2));
                } else {
                    throw new ParserException(
                            ParserException.CANNOT_PARSE_COMMAND + line);
                }
            }
        }

        return last;
    }

    /**
     * Parses definitions that are present after column definition is
     * closed with ')'.
     *
     * @param table table being parsed
     * @param line line being processed
     *
     * @return true if the command was the last command for CREATE TABLE,
     *         otherwise false
     *
     * @throws ParserException Thrown if problem occured while parsing INHERITS
     *         command.
     */
    private static boolean parsePostColumns(
        final PgTable table,
        final String line) {
        boolean last = false;

        if (line.endsWith(";")) {
            last = true;
        }

        final Matcher matcher = PATTERN_INHERITS.matcher(line);

        if (matcher.matches()) {
            table.setInherits(matcher.group(1));
        } else {
            throw new ParserException(
                    ParserException.CANNOT_PARSE_COMMAND + line);
        }

        return last;
    }

    /**
     * Parses all rows in CREATE TABLE command.
     *
     * @param table table being parsed
     * @param reader dump file reader
     *
     * @throws FileException Thrown if problem occured while reading dump file.
     * @throws ParserException Thrown if problem occured with parsing of DDL.
     */
    private static void parseRows(
        final PgTable table,
        final BufferedReader reader) {
        String origLine = null;
        boolean postColumns = false;

        try {
            String newLine = reader.readLine();

            while (newLine != null) {
                boolean last = false;
                origLine = newLine;
                newLine = newLine.trim();

                if (postColumns) {
                    last = parsePostColumns(table, newLine);
                } else {
                    if (")".equals(newLine)) {
                        postColumns = true;
                    } else if (");".equals(newLine)) {
                        break;
                    } else {
                        last = parseColumnDefs(table, newLine);
                    }
                }

                if (last) {
                    break;
                }

                newLine = reader.readLine();
            }
        } catch (IOException ex) {
            throw new FileException(FileException.CANNOT_READ_FILE, ex);
        } catch (RuntimeException ex) {
            throw new ParserException(
                    "Cannot parse CREATE TABLE '" + table.getName()
                    + "', line '" + origLine + "'",
                    ex);
        }
    }
}
