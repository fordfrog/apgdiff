package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgTrigger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses CREATE TRIGGER commands.
 *
 * @author fordfrog
 */
public class CreateTriggerParser {

    /**
     * Pattern for parsing CREATE TRIGGER command.
     */
    private static final Pattern PATTERN = Pattern.compile(
            "^CREATE[\\s]+TRIGGER[\\s]+\"?([^\\s\"]+)\"?[\\s]+(BEFORE|AFTER)[\\s]+"
            + "(INSERT|UPDATE|DELETE)(?:[\\s]+OR[\\s]+)?(INSERT|UPDATE|DELETE)?"
            + "(?:[\\s]+OR[\\s]+)?(INSERT|UPDATE|DELETE)?[\\s]+"
            + "ON[\\s]+\"?([^\\s\"]+)\"?[\\s]+(?:FOR[\\s]+)?(?:EACH[\\s]+)?"
            + "(ROW|STATEMENT)?[\\s]+EXECUTE[\\s]+PROCEDURE[\\s]+([^;]+);$",
            Pattern.CASE_INSENSITIVE);

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
        final Matcher matcher = PATTERN.matcher(command.trim());

        if (matcher.matches()) {
            final String triggerName = matcher.group(1);
            final String when = matcher.group(2);
            final String[] events = new String[3];
            events[0] = matcher.group(3);
            events[1] = matcher.group(4);
            events[2] = matcher.group(5);

            final String tableName = matcher.group(6);
            final String fireOn = matcher.group(7);
            final String procedure = matcher.group(8);

            final PgTrigger trigger = new PgTrigger();
            trigger.setBefore("BEFORE".equalsIgnoreCase(when));
            trigger.setForEachRow(
                    (fireOn != null) && "ROW".equalsIgnoreCase(fireOn));
            trigger.setFunction(procedure.trim());
            trigger.setName(triggerName.trim());
            trigger.setOnDelete(isEventPresent(events, "DELETE"));
            trigger.setOnInsert(isEventPresent(events, "INSERT"));
            trigger.setOnUpdate(isEventPresent(events, "UPDATE"));
            trigger.setTableName(tableName.trim());

            database.getDefaultSchema().getTable(
                    trigger.getTableName()).addTrigger(trigger);
        } else {
            throw new ParserException(
                    ParserException.CANNOT_PARSE_COMMAND + command);
        }
    }

    /**
     * Returns true if <code>event</code> is present in
     * <code>events</code>, otherwise false.
     *
     * @param events array of events
     * @param event event to be searched
     *
     * @return true if <code>event</code> is present in <code>events</code>,
     *         otherwise false
     */
    private static boolean isEventPresent(final String[] events,
            final String event) {
        boolean present = false;

        for (String curEvent : events) {
            if (event.equalsIgnoreCase(curEvent)) {
                present = true;

                break;
            }
        }

        return present;
    }
}
