/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.loader;

import cz.startnet.utils.pgdiff.Resources;
import cz.startnet.utils.pgdiff.parsers.AlterSequenceParser;
import cz.startnet.utils.pgdiff.parsers.AlterRelationParser;
import cz.startnet.utils.pgdiff.parsers.CommentParser;
import cz.startnet.utils.pgdiff.parsers.CreateExtensionParser;
import cz.startnet.utils.pgdiff.parsers.CreateFunctionParser;
import cz.startnet.utils.pgdiff.parsers.CreateTypeParser;
import cz.startnet.utils.pgdiff.parsers.CreateIndexParser;
import cz.startnet.utils.pgdiff.parsers.CreateSchemaParser;
import cz.startnet.utils.pgdiff.parsers.CreateSequenceParser;
import cz.startnet.utils.pgdiff.parsers.CreateTableParser;
import cz.startnet.utils.pgdiff.parsers.CreateTriggerParser;
import cz.startnet.utils.pgdiff.parsers.CreateViewParser;
import cz.startnet.utils.pgdiff.parsers.GrantRevokeParser;
import cz.startnet.utils.pgdiff.parsers.CreatePolicyParser;
import cz.startnet.utils.pgdiff.parsers.CreateProcedureParser;
import cz.startnet.utils.pgdiff.parsers.CreateRuleParser;
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
            "^CREATE[\\s]+(UNLOGGED\\s|FOREIGN\\s)*TABLE[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is CREATE VIEW or CREATE MATERIALIZED
     * VIEW statement.
     */
    private static final Pattern PATTERN_CREATE_VIEW = Pattern.compile(
            "^CREATE[\\s]+(?:OR[\\s]+REPLACE[\\s]+)?(?:MATERIALIZED[\\s]+)?VIEW[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is ALTER TABLE statement.
     */
    private static final Pattern PATTERN_ALTER_TABLE =
            Pattern.compile("^ALTER[\\s](FOREIGN)*TABLE[\\s]+.*$",
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
     * Pattern for testing whether it is CREATE PROCEDURE or CREATE OR REPLACE
     * PROCEDURE statement.
     */
    private static final Pattern PATTERN_CREATE_PROCEDURE = Pattern.compile(
            "^CREATE[\\s]+(?:OR[\\s]+REPLACE[\\s]+)?PROCEDURE[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    
    /**
     * Pattern for testing whether it is ALTER VIEW statement.
     */
    private static final Pattern PATTERN_ALTER_VIEW = Pattern.compile(
            "^ALTER[\\s]+(?:MATERIALIZED[\\s]+)?VIEW[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is COMMENT statement.
     */
    private static final Pattern PATTERN_COMMENT = Pattern.compile(
            "^COMMENT[\\s]+ON[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is CREATE TYPE statement.
     */
    private static final Pattern PATTERN_CREATE_TYPE = Pattern.compile(
            "^CREATE[\\s]+TYPE[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is GRANT statement.
     */
    private static final Pattern PATTERN_GRANT = Pattern.compile(
            "^GRANT[\\s]+.*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is REVOKE statement.
     */
    private static final Pattern PATTERN_REVOKE = Pattern.compile(
            "^REVOKE[\\s]+.*$", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing a dollar quoting tag.
     */
    private static final Pattern PATTERN_DOLLAR_TAG= Pattern.compile(
            "[\"\\s]",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is CREATE EXTENSION statement.
     */
    private static final Pattern PATTERN_CREATE_EXTENSION = Pattern.compile(
            "^CREATE[\\s]+EXTENSION[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is CREATE POLICY statement.
     */
    private static final Pattern PATTERN_CREATE_POLICY = Pattern.compile(
            "^CREATE[\\s]+POLICY[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    
    /**
     * Pattern for testing whether it is CREATE POLICY statement.
     */
    private static final Pattern PATTERN_DISABLE_TRIGGER = Pattern.compile(
           "ALTER\\s+TABLE+\\s+\"?\\w+\"?.+\"?\\w+\"?\\s+DISABLE+\\s+TRIGGER+\\s+\\w+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    /**
     * Pattern for testing whether it is CREATE RULE  statement.
     */
    private static final Pattern PATTERN_CREATE_RULE = Pattern.compile(
            "^CREATE[\\s]+RULE[\\s]+.*$",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    
    /**
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
     * @param ignoreSchemaCreation    whether schema creation should be ignored
     *
     * @return database schema from dump file
     */
    public static PgDatabase loadDatabaseSchema(final InputStream inputStream,
            final String charsetName, final boolean outputIgnoredStatements,
            final boolean ignoreSlonyTriggers, final boolean ignoreSchemaCreation) {

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
            } else if (PATTERN_CREATE_EXTENSION.matcher(statement).matches()) {
                CreateExtensionParser.parse(database, statement);
            } else if (PATTERN_DEFAULT_SCHEMA.matcher(statement).matches()) {
                final Matcher matcher =
                        PATTERN_DEFAULT_SCHEMA.matcher(statement);
                matcher.matches();
                database.setDefaultSchema(matcher.group(1));
            } else if (PATTERN_CREATE_TABLE.matcher(statement).matches()) {
                CreateTableParser.parse(database, statement, ignoreSchemaCreation);
            } else if ((PATTERN_ALTER_TABLE.matcher(statement).matches()
                    || PATTERN_ALTER_VIEW.matcher(statement).matches())
                    && !PATTERN_DISABLE_TRIGGER.matcher(statement).matches()) {
                    AlterRelationParser.parse(
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
            } else if (PATTERN_CREATE_TRIGGER.matcher(statement).matches()) {
                CreateTriggerParser.parse(
                        database, statement, ignoreSlonyTriggers);
            } else if ( PATTERN_DISABLE_TRIGGER.matcher(statement).matches()) {
                CreateTriggerParser.parseDisable(database, statement);
            } else if (PATTERN_CREATE_FUNCTION.matcher(statement).matches()) {
                CreateFunctionParser.parse(database, statement);
            } else if (PATTERN_CREATE_PROCEDURE.matcher(statement).matches()) {
                CreateProcedureParser.parse(database, statement);
            } else if (PATTERN_CREATE_TYPE.matcher(statement).matches()) {
                CreateTypeParser.parse(database, statement);
            } else if (PATTERN_COMMENT.matcher(statement).matches()) {
                CommentParser.parse(
                        database, statement, outputIgnoredStatements);
            } else if (PATTERN_SELECT.matcher(statement).matches()
                    || PATTERN_INSERT_INTO.matcher(statement).matches()
                    || PATTERN_UPDATE.matcher(statement).matches()
                    || PATTERN_DELETE_FROM.matcher(statement).matches()) {
            } else if (PATTERN_GRANT.matcher(statement).matches()) {
                GrantRevokeParser.parse(database, statement,
                        outputIgnoredStatements);
            } else if (PATTERN_REVOKE.matcher(statement).matches()) {
                GrantRevokeParser.parse(database, statement,
                        outputIgnoredStatements);
            } else if (PATTERN_CREATE_POLICY.matcher(statement).matches()) {
                CreatePolicyParser.parse(database, statement);               
            } else if (PATTERN_CREATE_RULE.matcher(statement).matches()) {
                CreateRuleParser.parse(database, statement);
            }  else if (outputIgnoredStatements) {
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
     * @param ignoreSchemaCreation    whether Schema creation should be ignored
     *
     * @return database schema from dump file
     */
    public static PgDatabase loadDatabaseSchema(final String file,
            final String charsetName, final boolean outputIgnoredStatements,
            final boolean ignoreSlonyTriggers, final boolean ignoreSchemaCreation) {
        if (file.equals("-"))
            return loadDatabaseSchema(System.in, charsetName,
                    outputIgnoredStatements, ignoreSlonyTriggers, ignoreSchemaCreation);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            return loadDatabaseSchema(fis, charsetName,
                    outputIgnoredStatements, ignoreSlonyTriggers, ignoreSchemaCreation);
        } catch (final FileNotFoundException ex) {
            throw new FileException(MessageFormat.format(
                    Resources.getString("FileNotFound"), file), ex);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex){}
            }
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
                    sbStatement.append(System.getProperty("line.separator"));
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

        int endPos = sbStatement.indexOf("*/");
        while (endPos >= 0) {
            if (!isQuoted(sbStatement, endPos)) {
                int startPos = sbStatement.lastIndexOf("/*", endPos);
                if (startPos < endPos && !isQuoted(sbStatement, startPos)) {
                    sbStatement.replace(startPos, endPos + 2, "");
                }
            }
            endPos = sbStatement.indexOf("*/", endPos+2);
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
        boolean insideDoubleQuotes = false;
        boolean insideSingeQuote = false; // Determine if double quote is inside of a single quote.
        
        for (int curPos = 0; curPos < pos; curPos++) {
            // Check if the quote is inside of a double quotes
            if (sbString.charAt(curPos) == '\"' && !insideSingeQuote ){
                insideDoubleQuotes = !insideDoubleQuotes;
            }
            if (sbString.charAt(curPos) == '\'' && !insideDoubleQuotes ){
                insideSingeQuote = !insideSingeQuote;
            }
            if(!insideDoubleQuotes){
                if (sbString.charAt(curPos) == '\'') {
                    isQuoted = !isQuoted;

                    // if quote was escaped by backslash, it's like double quote
                    if (pos > 0 && sbString.charAt(pos - 1) == '\\') {
                        isQuoted = !isQuoted;
                    }
                } else if (sbString.charAt(curPos) == '$' && !isQuoted) {
                    final int endPos = sbString.indexOf("$", curPos + 1);

                    if (endPos == -1) {
                        return false;
                    }

                    final String tag = sbString.substring(curPos, endPos + 1);

                    if (!isCorrectTag(tag)) {
                        return false;
                    }

                    final int endTagPos = sbString.indexOf(tag, endPos + 1);

                    // if end tag was not found or it was found after the checked
                    // position, it's quoted
                    if (endTagPos == -1 || endTagPos > pos) {
                        return true;
                    }

                    curPos = endTagPos + tag.length() - 1;
                }
            }
        }

        return isQuoted;
    }

    /**
     * Checks whether dollar quoting tag is correct.
     *
     * @param tag tag to be checked
     *
     * @return true if the tag is correct, otherwise false
     */
    private static boolean isCorrectTag(final String tag) {
        return !PATTERN_DOLLAR_TAG.matcher(tag).find();
    }

    /**
     * Creates a new instance of PgDumpLoader.
     */
    private PgDumpLoader() {
    }
}
