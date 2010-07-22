package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgTrigger;

/**
 * Parses CREATE TRIGGER commands.
 *
 * @author fordfrog
 */
public class CreateTriggerParser {

    /**
     * Creates a new CreateTableParser object.
     */
    private CreateTriggerParser() {
    }

    /**
     * Parses CREATE TRIGGER command.
     *
     * @param database database
     * @param command CREATE TRIGGER command
     *
     * @throws ParserException Thrown if problem occured while parsing the
     *         command.
     */
    public static void parse(final PgDatabase database, final String command) {
        final Parser parser = new Parser(command);
        parser.expect("CREATE", "TRIGGER");

        final PgTrigger trigger = new PgTrigger();
        trigger.setName(parser.parseIdentifier());

        if (parser.expectOptional("BEFORE")) {
            trigger.setBefore(true);
        } else if (parser.expectOptional("AFTER")) {
            trigger.setBefore(false);
        }

        boolean first = true;

        while (true) {
            if (!first && !parser.expectOptional("OR")) {
                break;
            }
            if (parser.expectOptional("INSERT")) {
                trigger.setOnInsert(true);
            } else if (parser.expectOptional("UPDATE")) {
                trigger.setOnUpdate(true);
            } else if (parser.expectOptional("DELETE")) {
                trigger.setOnDelete(true);
            } else if (parser.expectOptional("TRUNCATE")) {
                trigger.setOnTruncate(true);
            } else if (first) {
                break;
            } else {
                parser.throwUnsupportedCommand();
            }

            first = false;
        }

        parser.expect("ON");

        trigger.setTableName(parser.parseIdentifier());

        if (parser.expectOptional("FOR")) {
            parser.expectOptional("EACH");

            if (parser.expectOptional("ROW")) {
                trigger.setForEachRow(true);
            } else if (parser.expectOptional("STATEMENT")) {
                trigger.setForEachRow(false);
            } else {
                parser.throwUnsupportedCommand();
            }
        }

        if (parser.expectOptional("WHEN")) {
            parser.expect("(");
            trigger.setWhen(parser.getExpression());
            parser.expect(")");
        }

        parser.expect("EXECUTE", "PROCEDURE");
        trigger.setFunction(parser.getRest());

        database.getDefaultSchema().getTable(
                trigger.getTableName()).addTrigger(trigger);
    }
}
