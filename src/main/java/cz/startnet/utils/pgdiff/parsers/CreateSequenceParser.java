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
                "CREATE[\\s]+SEQUENCE[\\s]+\"?([^\\s\"]+)\"?",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for getting value of START WITH parameter.
     */
    private static final Pattern PATTERN_START_WITH =
        Pattern.compile(
                "START[\\s]+(?:WITH[\\s]+)?([-]?[\\d]+)",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for getting value of INCREMENT BY parameter.
     */
    private static final Pattern PATTERN_INCREMENT_BY =
        Pattern.compile(
                "INCREMENT[\\s]+(?:BY[\\s]+)?([-]?[\\d]+)",
                Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for getting value of MAXVALUE parameter.
     */
    private static final Pattern PATTERN_MAXVALUE =
        Pattern.compile("MAXVALUE[\\s]+([-]?[\\d]+)", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for getting value of MINVALUE parameter.
     */
    private static final Pattern PATTERN_MINVALUE =
        Pattern.compile("MINVALUE[\\s]+([-]?[\\d]+)", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for getting value of CACHE parameter.
     */
    private static final Pattern PATTERN_CACHE =
        Pattern.compile("CACHE[\\s]+([\\d]+)", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for checking whether string contains NO CYCLE string.
     */
    private static final Pattern PATTERN_NO_CYCLE =
        Pattern.compile(".*NO[\\s]+CYCLE.*", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for checking whether string contains CYCLE string.
     */
    private static final Pattern PATTERN_CYCLE =
        Pattern.compile(".*CYCLE.*", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for checking whether string contains NO MAXVALUE string.
     */
    private static final Pattern PATTERN_NO_MAXVALUE =
        Pattern.compile(".*NO[\\s]+MAXVALUE.*", Pattern.CASE_INSENSITIVE);

    /**
     * Pattern for checking whether string contains NO MINVALUE string.
     */
    private static final Pattern PATTERN_NO_MINVALUE =
        Pattern.compile(".*NO[\\s]+MINVALUE.*", Pattern.CASE_INSENSITIVE);

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

        final PgSequence sequence = new PgSequence(sequenceName);
        schema.addSequence(sequence);
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

        if (PATTERN_NO_CYCLE.matcher(line).matches()) {
            sequence.setCycle(false);
            line = ParserUtils.removeSubString(line, "NO CYCLE");
        } else if (PATTERN_CYCLE.matcher(line).matches()) {
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

        if (PATTERN_NO_MAXVALUE.matcher(line).matches()) {
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

        if (PATTERN_NO_MINVALUE.matcher(line).matches()) {
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
