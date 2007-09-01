/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.loader;

import cz.startnet.utils.pgdiff.parsers.AlterTableParser;
import cz.startnet.utils.pgdiff.parsers.CreateFunctionParser;
import cz.startnet.utils.pgdiff.parsers.CreateIndexParser;
import cz.startnet.utils.pgdiff.parsers.CreateSchemaParser;
import cz.startnet.utils.pgdiff.parsers.CreateSequenceParser;
import cz.startnet.utils.pgdiff.parsers.CreateTableParser;
import cz.startnet.utils.pgdiff.parsers.CreateTriggerParser;
import cz.startnet.utils.pgdiff.schema.PgDatabase;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Loads PostgreSQL dump into classes.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgDumpLoader { //NOPMD

    /**
     * Pattern for testing whether command is CREATE SCHEMA command.
     */
    private static final Pattern PATTERN_CREATE_SCHEMA =
        Pattern.compile(
                "^CREATE[\\s]+SCHEMA[\\s]+.*$",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for parsing default schema (search_path).
     */
    private static final Pattern PATTERN_DEFAULT_SCHEMA =
        Pattern.compile(
                "^SET[\\s]+search_path[\\s]*=[\\s]*([^,\\s]+)(?:,[\\s]+.*)?;$",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for testing whether command is CREATE TABLE command.
     */
    private static final Pattern PATTERN_CREATE_TABLE =
        Pattern.compile(
                "^CREATE[\\s]+TABLE[\\s]+.*$",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for testing whether command is ALTER TABLE command.
     */
    private static final Pattern PATTERN_ALTER_TABLE =
        Pattern.compile("^ALTER[\\s]+TABLE[\\s]+.*$", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for testing whether command is CREATE SEQUENCE command.
     */
    private static final Pattern PATTERN_CREATE_SEQUENCE =
        Pattern.compile(
                "^CREATE[\\s]+SEQUENCE[\\s]+.*$",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for testing whether command is CREATE INDEX command.
     */
    private static final Pattern PATTERN_CREATE_INDEX =
        Pattern.compile(
                "^CREATE[\\s]+INDEX[\\s]+.*$",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for testing whether command is SET command.
     */
    private static final Pattern PATTERN_SET =
        Pattern.compile("^SET[\\s]+.*$", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for testing whether command is COMMENT command.
     */
    private static final Pattern PATTERN_COMMENT =
        Pattern.compile("^COMMENT[\\s]+.*$", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for testing whether command is SELECT command.
     */
    private static final Pattern PATTERN_SELECT =
        Pattern.compile("^SELECT[\\s]+.*$", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for testing whether command is INSERT INTO command.
     */
    private static final Pattern PATTERN_INSERT_INTO =
        Pattern.compile("^INSERT[\\s]+INTO[\\s]+.*$", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for testing whether command is REVOKE command.
     */
    private static final Pattern PATTERN_REVOKE =
        Pattern.compile("^REVOKE[\\s]+.*$", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for testing whether command is GRANT command.
     */
    private static final Pattern PATTERN_GRANT =
        Pattern.compile("^GRANT[\\s]+.*$", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for testing whether command is CREATE TRIGGER command.
     */
    private static final Pattern PATTERN_CREATE_TRIGGER =
        Pattern.compile(
                "^CREATE[\\s]+TRIGGER[\\s]+.*$",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for testing whether command is CREATE FUNCTION or CREATE
     * OR REPLACE FUNCTION command.
     */
    private static final Pattern PATTERN_CREATE_FUNCTION =
        Pattern.compile(
                "^CREATE[\\s]+(?:OR[\\s]+REPLACE[\\s]+)?FUNCTION[\\s]+.*$",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for getting the string that is used to end the function
     * or the function definition itself.
     */
    private static final Pattern PATTERN_END_OF_FUNCTION =
        Pattern.compile(
                "^(?:.*[\\s]+)?AS[\\s]+(['$][^\\s]*).*$",
                Pattern.CASE_INSENSITIVE);

    /**
     * Creates a new instance of PgDumpLoader.
     */
    private PgDumpLoader() {
        super();
    }

    /**
     * Loads database schema from dump file.
     *
     * @param inputStream input stream that should be read
     *
     * @return database schema from dump fle
     *
     * @throws UnsupportedOperationException Thrown if unsupported encoding has
     *         been encountered.
     * @throws FileException Thrown if problem occured while reading input
     *         stream.
     */
    public static PgDatabase loadDatabaseSchema(final InputStream inputStream) { //NOPMD

        final PgDatabase database = new PgDatabase();
        BufferedReader reader = null;

        try {
            reader =
                new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new UnsupportedOperationException("Unsupported encoding", ex);
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
                            database,
                            getWholeCommand(reader, line));
                } else if (PATTERN_DEFAULT_SCHEMA.matcher(line).matches()) {
                    final Matcher matcher =
                        PATTERN_DEFAULT_SCHEMA.matcher(line);
                    matcher.matches();
                    database.setDefaultSchema(matcher.group(1));
                } else if (PATTERN_CREATE_TABLE.matcher(line).matches()) {
                    CreateTableParser.parse(
                            database,
                            getWholeCommand(reader, line));
                } else if (PATTERN_ALTER_TABLE.matcher(line).matches()) {
                    AlterTableParser.parse(
                            database,
                            getWholeCommand(reader, line));
                } else if (PATTERN_CREATE_SEQUENCE.matcher(line).matches()) {
                    CreateSequenceParser.parse(
                            database,
                            getWholeCommand(reader, line));
                } else if (PATTERN_CREATE_INDEX.matcher(line).matches()) {
                    CreateIndexParser.parse(
                            database,
                            getWholeCommand(reader, line));
                } else if (PATTERN_CREATE_TRIGGER.matcher(line).matches()) {
                    CreateTriggerParser.parse(
                            database,
                            getWholeCommand(reader, line));
                } else if (PATTERN_CREATE_FUNCTION.matcher(line).matches()) {
                    CreateFunctionParser.parse(
                            database,
                            getWholeFunction(reader, line));
                } else if (
                    PATTERN_SET.matcher(line).matches()
                        || PATTERN_COMMENT.matcher(line).matches()
                        || PATTERN_SELECT.matcher(line).matches()
                        || PATTERN_INSERT_INTO.matcher(line).matches()
                        || PATTERN_REVOKE.matcher(line).matches()
                        || PATTERN_GRANT.matcher(line).matches()) {
                    getWholeCommand(reader, line);
                }

                line = reader.readLine();
            }
        } catch (IOException ex) {
            throw new FileException(FileException.CANNOT_READ_FILE, ex);
        }

        return database;
    }

    /**
     * Loads database schema from dump file.
     *
     * @param file name of file containing the dump
     *
     * @return database schema from dump file
     *
     * @throws FileException Thrown if file not found.
     */
    public static PgDatabase loadDatabaseSchema(final String file) {
        try {
            return loadDatabaseSchema(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            throw new FileException("File '" + file + "' not found", ex);
        }
    }

    /**
     * Reads whole command from the reader into single-line string.
     *
     * @param reader reader to be read
     * @param line first line read
     *
     * @return whole command from the reader into single-line string
     *
     * @throws FileException Thrown if problem occured while reading string
     *         from <code>reader</code>.
     */
    private static String getWholeCommand(
        final BufferedReader reader,
        final String line) {
        String newLine = line.trim();
        final StringBuilder sbCommand = new StringBuilder(newLine);

        while (!newLine.trim().endsWith(";")) {
            try {
                newLine = stripComment(reader.readLine()).trim();
            } catch (IOException ex) {
                throw new FileException(FileException.CANNOT_READ_FILE, ex);
            }

            if (newLine.length() > 0) {
                sbCommand.append(' ');
                sbCommand.append(newLine);
            }
        }

        return sbCommand.toString();
    }

    /**
     * Reads whole CREATE FUNCTION DDL from the reader into multi-line
     * string.
     *
     * @param reader reader to be read
     * @param line first line read
     *
     * @return whole CREATE FUNCTION DDL from the reader into multi-line string
     *
     * @throws FileException Thrown if problem occured while reading string
     *         from<code>reader</code>.
     * @throws RuntimeException Thrown if cannot find end of function.
     */
    private static String getWholeFunction(
        final BufferedReader reader,
        final String line) {
        final String firstLine = line;
        final StringBuilder sbCommand = new StringBuilder();
        String newLine = line;
        Pattern endOfFunctionPattern = null;
        boolean searchForSemicolon = false;

        while (newLine != null) {
            if (!searchForSemicolon && (endOfFunctionPattern == null)) {
                final Matcher matcher =
                    PATTERN_END_OF_FUNCTION.matcher(newLine);

                if (matcher.matches()) {
                    String endOfFunction = matcher.group(1);

                    if (endOfFunction.charAt(0) == '\'') {
                        endOfFunction = "'";
                    } else {
                        endOfFunction =
                            endOfFunction.substring(
                                    0,
                                    endOfFunction.indexOf('$', 1) + 1);
                    }

                    if ("'".equals(endOfFunction)) {
                        endOfFunctionPattern =
                            Pattern.compile(
                                    "(?:.*[^\\\\]'.*|^.*[\\s]*'[\\s]*.*$)");
                    } else {
                        endOfFunctionPattern =
                            Pattern.compile(
                                    ".*\\Q" + endOfFunction + "\\E.*$",
                                    Pattern.CASE_INSENSITIVE);
                    }

                    final String stripped =
                        newLine.replaceAll(
                                "[\\s]+AS[\\s]+\\Q" + endOfFunction + "\\E",
                                " ");
                    searchForSemicolon = endOfFunctionPattern.matcher(stripped)
                                                             .matches();
                }
            }

            sbCommand.append(newLine);
            sbCommand.append('\n');

            if (searchForSemicolon && newLine.trim().endsWith(";")) {
                break;
            }

            try {
                newLine = reader.readLine();
            } catch (IOException ex) {
                throw new FileException(FileException.CANNOT_READ_FILE, ex);
            }

            if (newLine == null) {
                throw new RuntimeException(
                        "Cannot find end of function: " + firstLine);
            }

            if (
                !searchForSemicolon
                    && (endOfFunctionPattern != null)
                    && endOfFunctionPattern.matcher(newLine).matches()) {
                searchForSemicolon = true;
            }
        }

        return sbCommand.toString();
    }

    /**
     * Strips comment from command line.
     *
     * @param command command
     *
     * @return if comment was found then command without the comment, otherwise
     *         the original command
     */
    private static String stripComment(final String command) {
        String result = command;
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
