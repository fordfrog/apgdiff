/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTrigger;

/**
 * Parses CREATE TRIGGER statements.
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
     * Parses CREATE TRIGGER statement.
     *
     * @param database database
     * @param statement CREATE TRIGGER statement
     */
    public static void parse(final PgDatabase database,
            final String statement) {
        final Parser parser = new Parser(statement);
        parser.expect("CREATE", "TRIGGER");

        final String triggerName = parser.parseIdentifier();
        final String schemaName =
                ParserUtils.getSchemaName(triggerName, database);
        final String objectName = ParserUtils.getObjectName(triggerName);

        final PgTrigger trigger = new PgTrigger();
        trigger.setName(objectName);

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

        final String tableName = parser.parseIdentifier();

        trigger.setTableName(ParserUtils.getObjectName(tableName));

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

        final PgSchema tableSchema = database.getSchema(
                ParserUtils.getSchemaName(tableName, database));
        tableSchema.getTable(trigger.getTableName()).addTrigger(trigger);
    }
}
