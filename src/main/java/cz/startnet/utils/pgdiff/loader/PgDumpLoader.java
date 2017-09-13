/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.loader;

import cz.startnet.utils.pgdiff.Resources;
import cz.startnet.utils.pgdiff.parsers.AlterSequenceParser;
import cz.startnet.utils.pgdiff.parsers.AlterTableParser;
import cz.startnet.utils.pgdiff.parsers.AlterViewParser;
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
            "^CREATE[\\s]+SCHEMA[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for parsing default schema (search_path).
     */
    private static final Pattern PATTERN_DEFAULT_SCHEMA = Pattern.compile(
            "^SET[\\s]+search_path[\\s]*=[\\s]*\"?([^,\\s\"]+)\"?"
            + "(?:,[\\s]+.*)?;$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is CREATE TABLE statement.
     */
    private static final Pattern PATTERN_CREATE_TABLE = Pattern.compile(
            "^CREATE[\\s]+TABLE[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is CREATE VIEW statement.
     */
    private static final Pattern PATTERN_CREATE_VIEW = Pattern.compile(
            "^CREATE[\\s]+(?:OR[\\s]+REPLACE[\\s]+)?VIEW[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is ALTER TABLE statement.
     */
    private static final Pattern PATTERN_ALTER_TABLE =
            Pattern.compile("^ALTER[\\s]+TABLE[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is CREATE SEQUENCE statement.
     */
    private static final Pattern PATTERN_CREATE_SEQUENCE = Pattern.compile(
            "^CREATE[\\s]+SEQUENCE[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is ALTER SEQUENCE statement.
     */
    private static final Pattern PATTERN_ALTER_SEQUENCE =
            Pattern.compile("^ALTER[\\s]+SEQUENCE[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is CREATE INDEX statement.
     */
    private static final Pattern PATTERN_CREATE_INDEX = Pattern.compile(
            "^CREATE[\\s]+(?:UNIQUE[\\s]+)?INDEX[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is SELECT statement.
     */
    private static final Pattern PATTERN_SELECT = Pattern.compile(
            "^SELECT[\\s]+.*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is INSERT INTO statement.
     */
    private static final Pattern PATTERN_INSERT_INTO = Pattern.compile(
            "^INSERT[\\s]+INTO[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is UPDATE statement.
     */
    private static final Pattern PATTERN_UPDATE = Pattern.compile(
            "^UPDATE[\\s].*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is DELETE FROM statement.
     */
    private static final Pattern PATTERN_DELETE_FROM = Pattern.compile(
            "^DELETE[\\s]+FROM[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is CREATE TRIGGER statement.
     */
    private static final Pattern PATTERN_CREATE_TRIGGER = Pattern.compile(
            "^CREATE[\\s]+TRIGGER[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is CREATE FUNCTION or CREATE OR REPLACE
     * FUNCTION statement.
     */
    private static final Pattern PATTERN_CREATE_FUNCTION = Pattern.compile(
            "^CREATE[\\s]+(?:OR[\\s]+REPLACE[\\s]+)?FUNCTION[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is ALTER VIEW statement.
     */
    private static final Pattern PATTERN_ALTER_VIEW = Pattern.compile(
            "^ALTER[\\s]+VIEW[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is COMMENT statement.
     */
    private static final Pattern PATTERN_COMMENT = Pattern.compile(
            "^COMMENT[\\s]+ON[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Storage of unprocessed line part.
     */
    private static String lineBuffer;

    /**
     * Loads database schema from dump file.
     *
     * @param inputStream             input stream that should be read
     * @param charsetName             charset that should be used to read the
     *                                file
     * @param outputIgnoredStatements whether ignored statements should be
     *                                included in the output
     * @param ignoreSlonyTriggers     whether Slony triggers should be ignored
     *
     * @return database schema from dump file
     */
    public static PgDatabase loadDatabaseSchema(final InputStream inputStream,
            final String charsetName, final boolean outputIgnoredStatements,
            final boolean ignoreSlonyTriggers) {

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

        String statement = getWholeStatement(reader);

        while (statement != null) {
            if (PATTERN_CREATE_SCHEMA.matcher(statement).matches()) {
                CreateSchemaParser.parse(database, statement);
            } else if (PATTERN_DEFAULT_SCHEMA.matcher(statement).matches()) {
                final Matcher matcher =
                        PATTERN_DEFAULT_SCHEMA.matcher(statement);
                matcher.matches();
                database.setDefaultSchema(matcher.group(1));
            } else if (PATTERN_CREATE_TABLE.matcher(statement).matches()) {
                CreateTableParser.parse(database, statement);
            } else if (PATTERN_ALTER_TABLE.matcher(statement).matches()) {
                AlterTableParser.parse(
                        database, statement, outputIgnoredStatements);
            } else if (PATTERN_CREATE_SEQUENCE.matcher(statement).matches()) {
                CreateSequenceParser.parse(database, statement);
            } else if (PATTERN_ALTER_SEQUENCE.matcher(statement).matches()) {
                AlterSequenceParser.parse(
                        database, statement, outputIgnoredStatements);
            } else if (PATTERN_CREATE_INDEX.matcher(statement).matches()) {
                CreateIndexParser.parse(database, statement);
            } else if (PATTERN_CREATE_VIEW.matcher(statement).matches()) {
                CreateViewParser.parse(database, statement);
            } else if (PATTERN_ALTER_VIEW.matcher(statement).matches()) {
                AlterViewParser.parse(
                        database, statement, outputIgnoredStatements);
            } else if (PATTERN_CREATE_TRIGGER.matcher(statement).matches()) {
                CreateTriggerParser.parse(
                        database, statement, ignoreSlonyTriggers);
            } else if (PATTERN_CREATE_FUNCTION.matcher(statement).matches()) {
                CreateFunctionParser.parse(database, statement);
            } else if (PATTERN_COMMENT.matcher(statement).matches()) {
                CommentParser.parse(
                        database, statement, outputIgnoredStatements);
            } else if (PATTERN_SELECT.matcher(statement).matches()
                    || PATTERN_INSERT_INTO.matcher(statement).matches()
                    || PATTERN_UPDATE.matcher(statement).matches()
                    || PATTERN_DELETE_FROM.matcher(statement).matches()) {
                // we just ignore these statements
            } else if (outputIgnoredStatements) {
                database.addIgnoredStatement(statement);
            } else {
                // these statements are ignored if outputIgnoredStatements
                // is false
            }

            statement = getWholeStatement(reader);
        }

        return database;
    }

    /**
     * Loads database schema from dump file.
     *
     * @param file                    name of file containing the dump
     * @param charsetName             charset that should be used to read the
     *                                file
     * @param outputIgnoredStatements whether ignored statements should be
     *                                included in the output
     * @param ignoreSlonyTriggers     whether Slony triggers should be ignored
     *
     * @return database schema from dump file
     */
    public static PgDatabase loadDatabaseSchema(final String file,
            final String charsetName, final boolean outputIgnoredStatements,
            final boolean ignoreSlonyTriggers) {
        try {
            return loadDatabaseSchema(new FileInputStream(file), charsetName,
                    outputIgnoredStatements, ignoreSlonyTriggers);
        } catch (final FileNotFoundException ex) {
            throw new FileException(MessageFormat.format(
                    Resources.getString("FileNotFound"), file), ex);
        }
    }

    /**
     * Reads whole statement from the reader into single-line string.
     *
     * @param reader reader to be read
     *
     * @return whole statement from the reader into single-line string
     */
    private static String getWholeStatement(final BufferedReader reader) {
        final StringBuilder sbStatement = new StringBuilder(1024);

        if (lineBuffer != null) {
            sbStatement.append(lineBuffer);
            lineBuffer = null;
            stripComment(sbStatement);
        }

        int pos = sbStatement.indexOf(";");

        while (true) {
            if (pos == -1) {
                final String newLine;

                try {
                    newLine = reader.readLine();
                } catch (IOException ex) {
                    throw new FileException(
                            Resources.getString("CannotReadFile"), ex);
                }

                if (newLine == null) {
                    if (sbStatement.toString().trim().length() == 0) {
                        return null;
                    } else {
                        throw new RuntimeException(MessageFormat.format(
                                Resources.getString("EndOfStatementNotFound"),
                                sbStatement.toString()));
                    }
                }

                if (sbStatement.length() > 0) {
                    sbStatement.append('\n');
                }

                pos = sbStatement.length();
                sbStatement.append(newLine);
                stripComment(sbStatement);

                pos = sbStatement.indexOf(";", pos);
            } else {
                if (!isQuoted(sbStatement, pos)) {
                    if (pos == sbStatement.length() - 1) {
                        lineBuffer = null;
                    } else {
                        lineBuffer = sbStatement.substring(pos + 1);
                        sbStatement.setLength(pos + 1);
                    }

                    return sbStatement.toString().trim();
                }

                pos = sbStatement.indexOf(";", pos + 1);
            }
        }
    }

    /**
     * Strips comment from statement line.
     *
     * @param sbStatement string builder containing statement
     */
    private static void stripComment(final StringBuilder sbStatement) {
        int pos = sbStatement.indexOf("--");

        while (pos >= 0) {
            if (pos == 0) {
                sbStatement.setLength(0);

                return;
            } else {
                if (!isQuoted(sbStatement, pos)) {
                    sbStatement.setLength(pos);

                    return;
                }
            }

            pos = sbStatement.indexOf("--", pos + 1);
        }
    }

    /**
     * Checks whether specified position in the string builder is quoted. It
     * might be quoted either by single quote or by dollar sign quoting.
     *
     * @param sbString string builder
     * @param pos      position to be checked
     *
     * @return true if the specified position is quoted, otherwise false
     */
    @SuppressWarnings("AssignmentToForLoopParameter")
    private static boolean isQuoted(final StringBuilder sbString,
            final int pos) {
        boolean isQuoted = false;

        for (int curPos = 0; curPos < pos; curPos++) {
            if (sbString.charAt(curPos) == '\'') {
                isQuoted = !isQuoted;

                // if quote was escaped by backslash, it's like double quote
                if (pos > 0 && sbString.charAt(pos - 1) == '\\') {
                    isQuoted = !isQuoted;
                }
            } else if (sbString.charAt(curPos) == '$' && !isQuoted) {
                final int endPos = sbString.indexOf("$", curPos + 1);

                if (endPos == -1) {
                    return true;
                }

                final String tag = sbString.substring(curPos, endPos + 1);
                final int endTagPos = sbString.indexOf(tag, endPos + 1);

                // if end tag was not found or it was found after the checked
                // position, it's quoted
                if (endTagPos == -1 || endTagPos > pos) {
                    return true;
                }

                curPos = endTagPos + tag.length() - 1;
            }
        }

        return isQuoted;
    }

    /**
     * Creates a new instance of PgDumpLoader.
     */
    private PgDumpLoader() {
    }
}
