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
     * Parses CREATE TRIGGER statement.
     *
     * @param database            database
     * @param statement           CREATE TRIGGER statement
     * @param ignoreSlonyTriggers whether Slony triggers should be ignored
     */
    public static void parse(final PgDatabase database,
            final String statement, final boolean ignoreSlonyTriggers) {
        final Parser parser = new Parser(statement);
        parser.expect("CREATE", "TRIGGER");

        final String triggerName = parser.parseIdentifier();
        final String objectName = ParserUtils.getObjectName(triggerName);

        final PgTrigger trigger = new PgTrigger();
        trigger.setName(objectName);

        if (parser.expectOptional("BEFORE")) {
            trigger.setEventTimeQualification(PgTrigger.EventTimeQualification.before);
        } else if (parser.expectOptional("AFTER")) {
            trigger.setEventTimeQualification(PgTrigger.EventTimeQualification.after);
        } else if (parser.expectOptional("INSTEAD OF")) {
            trigger.setEventTimeQualification(PgTrigger.EventTimeQualification.instead_of);
        }

        boolean first = true;

        while (true) {
            if (!first && !parser.expectOptional("OR")) {
                break;
            } else if (parser.expectOptional("INSERT")) {
                trigger.setOnInsert(true);
            } else if (parser.expectOptional("UPDATE")) {
                trigger.setOnUpdate(true);

                if (parser.expectOptional("OF")) {
                    do {
                        trigger.addUpdateColumn(parser.parseIdentifier());
                    } while (parser.expectOptional(","));
                }
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

        final String relationName = parser.parseIdentifier();

        trigger.setRelationName(ParserUtils.getObjectName(relationName));
        
        String referencing="REFERENCING";
        if (parser.expectOptional(referencing)) {            
             trigger.setReferencing("\t"+referencing);             

            while( !parser.getSubString(parser.getPosition()-5, parser.getPosition()-4).equals(System.getProperty("line.separator"))){ 
                parseReferencing(parser,trigger);
               
            } 
        }

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

        final boolean ignoreSlonyTrigger = ignoreSlonyTriggers
                && ("_slony_logtrigger".equals(trigger.getName())
                || "_slony_denyaccess".equals(trigger.getName()));

        if (!ignoreSlonyTrigger) {
            final PgSchema schema = database.getSchema(
                    ParserUtils.getSchemaName(relationName, database));
            schema.getRelation(trigger.getRelationName()).addTrigger(trigger);
        }
    }

    /**
     * Creates a new CreateTableParser object.
     */
    private CreateTriggerParser() {
    }
    
    private static void parseReferencing(Parser parser, PgTrigger trigger) {

        if (parser.expectOptional("NEW")) {
            trigger.setReferencing(trigger.getReferencing() + " NEW ");
        }
        if (parser.expectOptional("OLD")) {
            trigger.setReferencing(trigger.getReferencing() + " OLD ");
        }
        parser.expect("TABLE");
        parser.expect("AS");
        trigger.setReferencing(trigger.getReferencing() + "TABLE AS " + parser.parseString());
    }
    
     public static void parseDisable(final PgDatabase database,
            final String statement) {
        final Parser parser = new Parser(statement);
        parser.expect("ALTER", "TABLE");

        final String tableName = parser.parseIdentifier();
        parser.expect("DISABLE","TRIGGER");
        final String objectName = parser.parseIdentifier();

        final PgTrigger trigger = new PgTrigger();
        trigger.setName(objectName);
        
        trigger.setRelationName(ParserUtils.getObjectName(tableName));
        
         final PgSchema schema = database.getSchema(
                    ParserUtils.getSchemaName(tableName, database));
            schema.getRelation(trigger.getRelationName()).getTrigger(objectName).setDisable(true);
     }
}
