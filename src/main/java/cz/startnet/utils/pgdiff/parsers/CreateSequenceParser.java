/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgSequence;

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
        Pattern.compile(
                "CREATE SEQUENCE \"?([^ \"]+)\"?",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for getting value of START WITH parameter.
     */
    private static final Pattern PATTERN_START_WITH =
        Pattern.compile(
                "START (?:WITH )?([-]?[\\d]+)",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for getting value of INCREMENT BY parameter.
     */
    private static final Pattern PATTERN_INCREMENT_BY =
        Pattern.compile(
                "INCREMENT (?:BY )?([-]?[\\d]+)",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for getting value of MAXVALUE parameter.
     */
    private static final Pattern PATTERN_MAXVALUE =
        Pattern.compile("MAXVALUE ([-]?[\\d]+)", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for getting value of MINVALUE parameter.
     */
    private static final Pattern PATTERN_MINVALUE =
        Pattern.compile("MINVALUE ([-]?[\\d]+)", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for getting value of CACHE parameter.
     */
    private static final Pattern PATTERN_CACHE =
        Pattern.compile("CACHE ([\\d]+)", Pattern.CASE_INSENSITIVE);

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
     * @param command CREATE SEQUENCE command
     *
     * @throws ParserException Thrown if problem occured while parsing the
     *         command.
     */
    public static void parse(final PgSchema schema, final String command) {
        String line = command;
        final Matcher matcher = PATTERN_SEQUENCE_NAME.matcher(line);
        final String sequenceName;

        if (matcher.find()) {
            sequenceName = matcher.group(1).trim();
            line =
                ParserUtils.removeSubString(
                        line,
                        matcher.start(),
                        matcher.end());
        } else {
            throw new ParserException(
                    ParserException.CANNOT_PARSE_COMMAND + line);
        }

        final PgSequence sequence = schema.getSequence(sequenceName);
        line = ParserUtils.removeLastSemicolon(line);
        line = processMaxValue(sequence, line);
        line = processMinValue(sequence, line);
        line = processCycle(sequence, line);
        line = processCache(sequence, line);
        line = processIncrement(sequence, line);
        line = processStartWith(sequence, line);
        line = line.trim();

        if (line.length() > 0) {
            throw new ParserException(
                    "Cannot parse commmand '" + command + "', string '" + line
                    + "'");
        }
    }

    /**
     * Processes CACHE instruction.
     *
     * @param sequence sequence
     * @param command command
     *
     * @return command without CACHE instruction
     */
    private static String processCache(
        final PgSequence sequence,
        final String command) {
        String line = command;
        final Matcher matcher = PATTERN_CACHE.matcher(line);

        if (matcher.find()) {
            sequence.setCache(matcher.group(1).trim());
            line =
                ParserUtils.removeSubString(
                        line,
                        matcher.start(),
                        matcher.end());
        }

        return line;
    }

    /**
     * Processes CYCLE and NO CYCLE instructions.
     *
     * @param sequence sequence
     * @param command command
     *
     * @return command without CYCLE instructions
     */
    private static String processCycle(
        final PgSequence sequence,
        final String command) {
        String line = command;

        if (line.contains("NO CYCLE")) {
            sequence.setCycle(false);
            line = ParserUtils.removeSubString(line, "NO CYCLE");
        } else if (line.contains("CYCLE")) {
            sequence.setCycle(true);
            line = ParserUtils.removeSubString(line, "CYCLE");
        }

        return line;
    }

    /**
     * Processes INCREMENT BY instruction.
     *
     * @param sequence sequence
     * @param command command
     *
     * @return command without INCREMENT BY instruction
     */
    private static String processIncrement(
        final PgSequence sequence,
        final String command) {
        String line = command;
        final Matcher matcher = PATTERN_INCREMENT_BY.matcher(line);

        if (matcher.find()) {
            sequence.setIncrement(matcher.group(1).trim());
            line =
                ParserUtils.removeSubString(
                        line,
                        matcher.start(),
                        matcher.end());
        }

        return line;
    }

    /**
     * Processes MAX VALUE and NO MAXVALUE instructions.
     *
     * @param sequence sequence
     * @param command command
     *
     * @return command without MAX VALUE instructions
     */
    private static String processMaxValue(
        final PgSequence sequence,
        final String command) {
        String line = command;

        if (line.contains("NO MAXVALUE")) {
            sequence.setMaxValue(null);
            line = ParserUtils.removeSubString(line, "NO MAXVALUE");
        } else {
            final Matcher matcher = PATTERN_MAXVALUE.matcher(line);

            if (matcher.find()) {
                sequence.setMaxValue(matcher.group(1).trim());
                line =
                    ParserUtils.removeSubString(
                            line,
                            matcher.start(),
                            matcher.end());
            }
        }

        return line;
    }

    /**
     * Processes MIN VALUE and NO MINVALUE instructions.
     *
     * @param sequence sequence
     * @param command command
     *
     * @return command without MIN VALUE instructions
     */
    private static String processMinValue(
        final PgSequence sequence,
        final String command) {
        String line = command;

        if (line.contains("NO MINVALUE")) {
            sequence.setMinValue(null);
            line = ParserUtils.removeSubString(line, "NO MINVALUE");
        } else {
            final Matcher matcher = PATTERN_MINVALUE.matcher(line);

            if (matcher.find()) {
                sequence.setMinValue(matcher.group(1).trim());
                line =
                    ParserUtils.removeSubString(
                            line,
                            matcher.start(),
                            matcher.end());
            }
        }

        return line;
    }

    /**
     * Processes START WITH instruction.
     *
     * @param sequence sequence
     * @param command command
     *
     * @return command without START WITH instruction
     */
    private static String processStartWith(
        final PgSequence sequence,
        final String command) {
        String line = command;
        final Matcher matcher = PATTERN_START_WITH.matcher(line);

        if (matcher.find()) {
            sequence.setStartWith(matcher.group(1).trim());
            line =
                ParserUtils.removeSubString(
                        line,
                        matcher.start(),
                        matcher.end());
        }

        return line;
    }
}
