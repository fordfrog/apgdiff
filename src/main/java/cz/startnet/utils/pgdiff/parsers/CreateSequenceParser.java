package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgSequence;

/**
 * Parses CREATE SEQUENCE commands.
 *
 * @author fordfrog
 */
public class CreateSequenceParser {

    /**
     * Creates a new instance of CreateSequenceParser.
     */
    private CreateSequenceParser() {
    }

    /**
     * Parses CREATE SEQUENCE command.
     *
     * @param database database
     * @param command CREATE SEQUENCE command
     *
     * @throws ParserException Thrown if problem occured while parsing the
     *         command.
     */
    public static void parse(final PgDatabase database, final String command) {
        final Parser parser = new Parser(command);
        parser.expect("CREATE", "SEQUENCE");

        final String sequenceName = parser.parseIdentifier();
        final PgSequence sequence =
                new PgSequence(ParserUtils.getObjectName(sequenceName));
        database.getSchema(ParserUtils.getSchemaName(
                sequenceName, database)).addSequence(sequence);

        while (!parser.expectOptional(";")) {
            if (parser.expectOptional("INCREMENT")) {
                parser.expectOptional("BY");
                sequence.setIncrement(parser.parseString());
            } else if (parser.expectOptional("MINVALUE")) {
                sequence.setMinValue(parser.parseString());
            } else if (parser.expectOptional("MAXVALUE")) {
                sequence.setMaxValue(parser.parseString());
            } else if (parser.expectOptional("START")) {
                parser.expectOptional("WITH");
                sequence.setStartWith(parser.parseString());
            } else if (parser.expectOptional("CACHE")) {
                sequence.setCache(parser.parseString());
            } else if (parser.expectOptional("CYCLE")) {
                sequence.setCycle(true);
            } else if (parser.expectOptional("OWNED", "BY")) {
                if (parser.expectOptional("NONE")) {
                    sequence.setOwnedBy(null);
                } else {
                    sequence.setOwnedBy(parser.parseIdentifier());
                }
            } else if (parser.expectOptional("NO")) {
                if (parser.expectOptional("MINVALUE")) {
                    sequence.setMinValue(null);
                } else if (parser.expectOptional("MAXVALUE")) {
                    sequence.setMaxValue(null);
                } else if (parser.expectOptional("CYCLE")) {
                    sequence.setCycle(false);
                } else {
                    parser.throwUnsupportedCommand();
                }
            } else {
                parser.throwUnsupportedCommand();
            }
        }
    }
}
