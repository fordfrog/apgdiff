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
     * Parses CREATE [CONSTRAINT] TRIGGER statement.
     *
     * @param database            database
     * @param statement           CREATE [CONSTRAINT] TRIGGER statement
     * @param ignoreSlonyTriggers whether Slony triggers should be ignored
     */
    public static void parse(final PgDatabase database,
            final String statement, final boolean ignoreSlonyTriggers) {

    	final PgTrigger trigger = new PgTrigger();
        final Parser parser = new Parser(statement);
     
        parser.expect("CREATE");
        if(parser.expectOptional("CONSTRAINT"))
        	trigger.setConstraint(true);
        parser.expect("TRIGGER");
        
        final String triggerName = parser.parseIdentifier();
        final String objectName = ParserUtils.getObjectName(triggerName);

        trigger.setName(objectName);
        
        if(trigger.isConstraint()){
        	parser.expect("AFTER");
        	trigger.setEventTimeQualification(PgTrigger.EventTimeQualification.after);
        } else {
        	if (parser.expectOptional("BEFORE")) {
        		trigger.setEventTimeQualification(PgTrigger.EventTimeQualification.before);
        	} else if (parser.expectOptional("AFTER")) {
        		trigger.setEventTimeQualification(PgTrigger.EventTimeQualification.after);
        	} else if (parser.expectOptional("INSTEAD OF")) {
        		trigger.setEventTimeQualification(PgTrigger.EventTimeQualification.instead_of);
        	}
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
        
        if(trigger.isConstraint()){
        	if(parser.expectOptional("DEFERRABLE")){
        		trigger.setDeferrable(true);
        		if(parser.expectOptional("INITIALLY","DEFERRED")){
        			trigger.setDeferred(true);
        		}else if(parser.expectOptional("INITIALLY","IMMEDIATE")){
        			trigger.setDeferred(false);
        		}else{
        			trigger.setDeferred(false);
        		}
        	}else if(parser.expectOptional("NOT","DEFERRABLE")){
        		trigger.setDeferrable(false);
        	}else {
        		trigger.setDeferrable(false);
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
}
