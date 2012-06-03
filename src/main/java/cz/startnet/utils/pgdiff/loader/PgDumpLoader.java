/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.loader;

import cz.startnet.utils.pgdiff.Resources;
import cz.startnet.utils.pgdiff.parsers.AlterTableParser;
import cz.startnet.utils.pgdiff.parsers.AlterViewParser;
import cz.startnet.utils.pgdiff.parsers.AlterSequenceParser;
import cz.startnet.utils.pgdiff.parsers.CommentParser;
import cz.startnet.utils.pgdiff.parsers.CreateFunctionParser;
import cz.startnet.utils.pgdiff.parsers.CreateIndexParser;
import cz.startnet.utils.pgdiff.parsers.CreateSchemaParser;
import cz.startnet.utils.pgdiff.parsers.CreateSequenceParser;
import cz.startnet.utils.pgdiff.parsers.CreateTableParser;
import cz.startnet.utils.pgdiff.parsers.CreateTriggerParser;
import cz.startnet.utils.pgdiff.parsers.CreateViewParser;
import cz.startnet.utils.pgdiff.schema.PgDatabase;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads PostgreSQL dump into classes.
 *
 * @author fordfrog
 */
public class PgDumpLoader { //NOPMD

    /**
     * Pattern for testing whether it is CREATE SCHEMA statement.
     */
    private static final Pattern PATTERN_CREATE_SCHEMA = Pattern.compile(
            "^CREATE[\\s]+SCHEMA[\\s]+.*$", Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for parsing default schema (search_path).
     */
    private static final Pattern PATTERN_DEFAULT_SCHEMA =
            Pattern.compile(
            "^SET[\\s]+search_path[\\s]*=[\\s]*\"?([^,\\s\"]+)\"?(?:,[\\s]+.*)?;$",
            Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for testing whether it is CREATE TABLE statement.
     */
    private static final Pattern PATTERN_CREATE_TABLE = Pattern.compile(
            "^CREATE[\\s]+TABLE[\\s]+.*$", Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for testing whether it is CREATE VIEW statement.
     */
    private static final Pattern PATTERN_CREATE_VIEW = Pattern.compile(
            "^CREATE[\\s]+(?:OR[\\s]+REPLACE[\\s]+)?VIEW[\\s]+.*$",
            Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for testing whether it is ALTER TABLE statement.
     */
    private static final Pattern PATTERN_ALTER_TABLE =
            Pattern.compile("^ALTER[\\s]+TABLE[\\s]+.*$",
            Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for testing whether it is CREATE SEQUENCE statement.
     */
    private static final Pattern PATTERN_CREATE_SEQUENCE = Pattern.compile(
            "^CREATE[\\s]+SEQUENCE[\\s]+.*$", Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for testing whether it is ALTER SEQUENCE statement.
     */
    private static final Pattern PATTERN_ALTER_SEQUENCE =
            Pattern.compile("^ALTER[\\s]+SEQUENCE[\\s]+.*$",
            Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for testing whether it is CREATE INDEX statement.
     */
    private static final Pattern PATTERN_CREATE_INDEX = Pattern.compile(
            "^CREATE[\\s]+(?:UNIQUE[\\s]+)?INDEX[\\s]+.*$",
            Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for testing whether it is SELECT statement.
     */
    private static final Pattern PATTERN_SELECT =
            Pattern.compile("^SELECT[\\s]+.*$", Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for testing whether it is INSERT INTO statement.
     */
    private static final Pattern PATTERN_INSERT_INTO =
            Pattern.compile("^INSERT[\\s]+INTO[\\s]+.*$",
            Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for testing whether it is UPDATE statement.
     */
    private static final Pattern PATTERN_UPDATE =
            Pattern.compile("^UPDATE[\\s].*$", Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for testing whether it is DELETE FROM statement.
     */
    private static final Pattern PATTERN_DELETE_FROM =
            Pattern.compile("^DELETE[\\s]+FROM[\\s]+.*$",
            Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for testing whether it is CREATE TRIGGER statement.
     */
    private static final Pattern PATTERN_CREATE_TRIGGER = Pattern.compile(
            "^CREATE[\\s]+TRIGGER[\\s]+.*$", Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for testing whether it is CREATE FUNCTION or CREATE OR REPLACE
     * FUNCTION statement.
     */
    private static final Pattern PATTERN_CREATE_FUNCTION = Pattern.compile(
            "^CREATE[\\s]+(?:OR[\\s]+REPLACE[\\s]+)?FUNCTION[\\s]+.*$",
            Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for testing whether it is ALTER VIEW statement.
     */
    private static final Pattern PATTERN_ALTER_VIEW = Pattern.compile(
            "^ALTER[\\s]+VIEW[\\s]+.*$", Pattern.CASE_INSENSITIVE);
    /**
     * Pattern for testing whether it is COMMENT statement.
     */
    private static final Pattern PATTERN_COMMENT = Pattern.compile(
            "^COMMENT[\\s]+ON[\\s]+.*$", Pattern.CASE_INSENSITIVE);

    /**
     * Creates a new instance of PgDumpLoader.
     */
    private PgDumpLoader() {
    }

    /**
     * Loads database schema from dump file.
     *
     * @param inputStream input stream that should be read
     * @param charsetName charset that should be used to read the file
     * @param outputIgnoredStatements whether ignored statements should be
     * included in the output
     *
     * @return database schema from dump file
     */
    public static PgDatabase loadDatabaseSchema(final InputStream inputStream,
            final String charsetName, final boolean outputIgnoredStatements) {

        final PgDatabase database = new PgDatabase();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(inputStream, charsetName));
        } catch (final UnsupportedEncodingException ex) {
            throw new UnsupportedOperationException(
                    Resources.getString("UnsupportedEncoding") + ": "
                    + charsetName, ex);
        }

        try {
            String line = reader.readLine();

            while (line != null) {
                line = stripComment(line).trim();
                line = line.trim();

                if (line.length() == 0) {
                    line = reader.readLine();

                    continue;
                } else if (PATTERN_CREATE_SCHEMA.matcher(line).matches()) {
                    CreateSchemaParser.parse(
                            database, getWholeStatement(reader, line));
                } else if (PATTERN_DEFAULT_SCHEMA.matcher(line).matches()) {
                    final Matcher matcher =
                            PATTERN_DEFAULT_SCHEMA.matcher(line);
                    matcher.matches();
                    database.setDefaultSchema(matcher.group(1));
                } else if (PATTERN_CREATE_TABLE.matcher(line).matches()) {
                    CreateTableParser.parse(
                            database, getWholeStatement(reader, line));
                } else if (PATTERN_ALTER_TABLE.matcher(line).matches()) {
                    AlterTableParser.parse(database,
                            getWholeStatement(reader, line),
                            outputIgnoredStatements);
                } else if (PATTERN_CREATE_SEQUENCE.matcher(line).matches()) {
                    CreateSequenceParser.parse(
                            database, getWholeStatement(reader, line));
                } else if (PATTERN_ALTER_SEQUENCE.matcher(line).matches()) {
                    AlterSequenceParser.parse(database,
                            getWholeStatement(reader, line),
                            outputIgnoredStatements);
                } else if (PATTERN_CREATE_INDEX.matcher(line).matches()) {
                    CreateIndexParser.parse(
                            database, getWholeStatement(reader, line));
                } else if (PATTERN_CREATE_VIEW.matcher(line).matches()) {
                    CreateViewParser.parse(
                            database, getWholeStatement(reader, line));
                } else if (PATTERN_ALTER_VIEW.matcher(line).matches()) {
                    AlterViewParser.parse(database,
                            getWholeStatement(reader, line),
                            outputIgnoredStatements);
                } else if (PATTERN_CREATE_TRIGGER.matcher(line).matches()) {
                    CreateTriggerParser.parse(
                            database, getWholeStatement(reader, line));
                } else if (PATTERN_CREATE_FUNCTION.matcher(line).matches()) {
                    CreateFunctionParser.parse(
                            database, getWholeFunction(reader, line));
                } else if (PATTERN_COMMENT.matcher(line).matches()) {
                    CommentParser.parse(database,
                            getWholeStatement(reader, line),
                            outputIgnoredStatements);
                } else if (PATTERN_SELECT.matcher(line).matches()
                        || PATTERN_INSERT_INTO.matcher(line).matches()
                        || PATTERN_UPDATE.matcher(line).matches()
                        || PATTERN_DELETE_FROM.matcher(line).matches()) {
                    getWholeStatement(reader, line);
                } else if (outputIgnoredStatements) {
                    database.addIgnoredStatement(
                            getWholeStatement(reader, line));
                } else {
                    getWholeStatement(reader, line);
                }

                line = reader.readLine();
            }
        } catch (final IOException ex) {
            throw new FileException(Resources.getString("CannotReadFile"), ex);
        }

        return database;
    }

    /**
     * Loads database schema from dump file.
     *
     * @param file name of file containing the dump
     * @param charsetName charset that should be used to read the file
     * @param outputIgnoredStatements whether ignored statements should be
     * included in the output
     *
     * @return database schema from dump file
     */
    public static PgDatabase loadDatabaseSchema(final String file,
            final String charsetName, final boolean outputIgnoredStatements) {
        try {
            return loadDatabaseSchema(new FileInputStream(file), charsetName,
                    outputIgnoredStatements);
        } catch (final FileNotFoundException ex) {
            throw new FileException(MessageFormat.format(
                    Resources.getString("FileNotFound"), file), ex);
        }
    }

    /**
     * Reads whole statement from the reader into single-line string.
     *
     * @param reader reader to be read
     * @param line first line read
     *
     * @return whole statement from the reader into single-line string
     */
    private static String getWholeStatement(final BufferedReader reader,
            final String line) {
        String newLine = line.trim();
        final StringBuilder sbStatement = new StringBuilder(newLine);

        while (!newLine.trim().endsWith(";")) {
            try {
                newLine = stripComment(reader.readLine()).trim();
            } catch (IOException ex) {
                throw new FileException(
                        Resources.getString("CannotReadFile"), ex);
            }

            if (newLine.length() > 0) {
                sbStatement.append(' ');
                sbStatement.append(newLine);
            }
        }

        return sbStatement.toString();
    }

    /**
     * Reads whole CREATE FUNCTION DDL from the reader into multi-line
     * string.
     *
     * @param reader reader to be read
     * @param line first line read
     *
     * @return whole CREATE FUNCTION DDL from the reader into multi-line string
     */
    private static String getWholeFunction(final BufferedReader reader,
            final String line) {
        final String firstLine = line;
        final StringBuilder sbStatement = new StringBuilder(1000);
        String newLine = line;
        String endOfFunction = null;
        boolean ignoreFirstOccurence = true;
        boolean searchForSemicolon = false;
        boolean nextIsSeparator = false;

        while (newLine != null) {
            if (endOfFunction == null) {
                boolean previousWasWhitespace = true;

                for (int i = 0; i < newLine.length(); i++) {
                    final char chr = newLine.charAt(i);

                    if (Character.isWhitespace(chr)) {
                        previousWasWhitespace = true;
                        continue;
                    } else if (nextIsSeparator) {
                        if (chr == '\'') {
                            endOfFunction = "'";
                        } else {
                            endOfFunction = newLine.substring(
                                    i, newLine.indexOf('$', i + 1) + 1);
                        }

                        break;
                    } else if (previousWasWhitespace
                            && Character.toUpperCase(chr) == 'A'
                            && Character.toUpperCase(newLine.charAt(i + 1)) == 'S'
                            && (i + 2 == newLine.length()
                            || Character.isWhitespace(newLine.charAt(i + 2)))) {
                        i += 2;
                        nextIsSeparator = true;
                    } else {
                        previousWasWhitespace = false;
                    }
                }
            }

            sbStatement.append(newLine);
            sbStatement.append('\n');

            if (endOfFunction != null) {
                // count occurences
                if (!searchForSemicolon) {
                    int count = ignoreFirstOccurence ? -1 : 0;
                    int pos = newLine.indexOf(endOfFunction);
                    ignoreFirstOccurence = false;

                    while (pos != -1) {
                        count++;
                        pos = newLine.indexOf(
                                endOfFunction, pos + endOfFunction.length());
                    }

                    if (count % 2 == 1) {
                        searchForSemicolon = true;
                    }
                }

                if (searchForSemicolon && newLine.trim().endsWith(";")) {
                    break;
                }
            }

            try {
                newLine = reader.readLine();
            } catch (final IOException ex) {
                throw new FileException(
                        Resources.getString("CannotReadFile"), ex);
            }

            if (newLine == null) {
                throw new RuntimeException(
                        Resources.getString("CannotFindEndOfFunction") + ": "
                        + firstLine);
            }
        }

        return sbStatement.toString();
    }

    /**
     * Strips comment from statement line.
     *
     * @param statement statement
     *
     * @return if comment was found then statement without the comment,
     * otherwise the original statement
     */
    private static String stripComment(final String statement) {
        String result = statement;
        int pos = result.indexOf("--");

        while (pos >= 0) {
            if (pos == 0) {
                result = "";

                break;
            } else {
                int count = 0;

                for (int chr = 0; chr < pos; chr++) {
                    if (chr == '\'') {
                        count++;
                    }
                }

                if ((count % 2) == 0) {
                    result = result.substring(0, pos).trim();

                    break;
                }
            }

            pos = result.indexOf("--", pos + 1);
        }

        return result;
    }
}
