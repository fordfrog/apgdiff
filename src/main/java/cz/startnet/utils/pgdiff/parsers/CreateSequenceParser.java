/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.loader.FileException;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgSequence;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses CREATE SEQUENCE commands.
 *
 * @author fordfrog
 * @version $Id$
 */
public class CreateSequenceParser {
    /**
     * Pattern for getting sequence name.
     */
    private static final Pattern PATTERN_SEQUENCE_NAME =
        Pattern.compile("CREATE SEQUENCE ([^ ]+)");

    /**
     * Pattern for getting value of START WITH parameter.
     */
    private static final Pattern PATTERN_START_WITH =
        Pattern.compile("START (?:WITH )?(.+)");

    /**
     * Pattern for getting value of INCREMENT BY parameter.
     */
    private static final Pattern PATTERN_INCREMENT_BY =
        Pattern.compile("INCREMENT (?:BY )?(.+)");

    /**
     * Pattern for getting value of MAXVALUE parameter.
     */
    private static final Pattern PATTERN_MAXVALUE =
        Pattern.compile("MAXVALUE (.+)");

    /**
     * Pattern for getting value of MINVALUE parameter.
     */
    private static final Pattern PATTERN_MINVALUE =
        Pattern.compile("MINVALUE (.+)");

    /**
     * Pattern for getting value of CACHE parameter.
     */
    private static final Pattern PATTERN_CACHE = Pattern.compile("CACHE (.+)");

    /**
     * Creates a new instance of CreateSequenceParser.
     */
    private CreateSequenceParser() {
        super();
    }

    /**
     * Parses CREATE SEQUENCE command.
     *
     * @param schema schema to be filled
     * @param reader reader of the dump file
     * @param line first line read
     *
     * @throws ParserException Thrown if problem occured while parsing the
     *         command.
     * @throws FileException Thrown if problem occured while reading dump file.
     */
    public static void parse(
        final PgSchema schema,
        final BufferedReader reader,
        final String line) {
        Matcher matcher = PATTERN_SEQUENCE_NAME.matcher(line);
        final String sequenceName;

        if (matcher.matches()) {
            sequenceName = matcher.group(1).trim();
        } else {
            throw new ParserException(
                    ParserException.CANNOT_PARSE_COMMAND + line);
        }

        final PgSequence sequence = schema.getSequence(sequenceName);
        String origLine = null;

        try {
            String newLine = reader.readLine();

            while (newLine != null) {
                boolean last = false;
                origLine = newLine;
                newLine = newLine.trim();

                if (newLine.endsWith(";")) {
                    last = true;
                    newLine = newLine.substring(0, newLine.length() - 1).trim();
                }

                if ("NO MAXVALUE".equals(newLine)) {
                    sequence.setMaxValue(null);
                } else if ("NO MINVALUE".equals(newLine)) {
                    sequence.setMinValue(null);
                } else if ("CYCLE".equals(newLine)) {
                    sequence.setCycle(true);
                } else if ("NO CYCLE".equals(newLine)) {
                    sequence.setCycle(false);
                } else if (PATTERN_CACHE.matcher(newLine).matches()) {
                    matcher = PATTERN_CACHE.matcher(newLine);
                    matcher.matches();
                    sequence.setCache(matcher.group(1).trim());
                } else if (PATTERN_INCREMENT_BY.matcher(newLine).matches()) {
                    matcher = PATTERN_INCREMENT_BY.matcher(newLine);
                    matcher.matches();
                    sequence.setIncrement(matcher.group(1).trim());
                } else if (PATTERN_MAXVALUE.matcher(newLine).matches()) {
                    matcher = PATTERN_MAXVALUE.matcher(newLine);
                    matcher.matches();
                    sequence.setMaxValue(matcher.group(1).trim());
                } else if (PATTERN_MINVALUE.matcher(newLine).matches()) {
                    matcher = PATTERN_MINVALUE.matcher(newLine);
                    matcher.matches();
                    sequence.setMinValue(matcher.group(1).trim());
                } else if (PATTERN_START_WITH.matcher(newLine).matches()) {
                    matcher = PATTERN_START_WITH.matcher(newLine);
                    matcher.matches();
                    sequence.setStartWith(matcher.group(1).trim());
                } else {
                    throw new ParserException(
                            "Cannot parse CREATE SEQUENCE '" + sequenceName
                            + "', line '" + origLine + "'");
                }

                if (last) {
                    break;
                }

                newLine = reader.readLine();
            }
        } catch (IOException ex) {
            throw new FileException(FileException.CANNOT_READ_FILE, ex);
        } catch (IllegalStateException ex) {
            throw new ParserException(
                    "Cannot parse CREATE SEQUENCE '" + sequenceName
                    + "', line '" + origLine + "'",
                    ex);
        }
    }
}
