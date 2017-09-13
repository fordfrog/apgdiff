/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.Resources;
import cz.startnet.utils.pgdiff.schema.*;

import java.text.MessageFormat;

public class CreatePolicyParser {

    public static void parse(final PgDatabase database,
            final String statement) {
        final Parser parser = new Parser(statement);

        final PgPolicy policy = new PgPolicy();

        parser.expect("CREATE", "POLICY");
        final String policyName = parser.parseIdentifier();

        parser.expect("ON");

        final String qualifiedTableName = parser.parseIdentifier();
        final String schemaName =
                ParserUtils.getSchemaName(qualifiedTableName, database);

        final PgSchema schema = database.getSchema(schemaName);
        if (schema == null) {
            throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindSchema"), schemaName,
                    statement));
        }

        final PgTable table = schema.getTable(ParserUtils.getObjectName(qualifiedTableName));
        if (table == null) {
            throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindTable"), qualifiedTableName,
                    statement));
        }

        if (parser.expectOptional("FOR")) {
            String command = parser.expectOptionalOneOf("ALL", "SELECT",
                "INSERT", "UPDATE", "DELETE");
            policy.setCommand(command);
        } else {
            policy.setCommand("ALL");
        }

        if (parser.expectOptional("TO")) {
            if (parser.expectOptional("PUBLIC")){
                policy.getRoles().add("PUBLIC");
            } else {
                String role = parser.parseIdentifier();
                policy.getRoles().add(role);
                while (role != null) {
                    if (parser.expectOptional(",")) {
                        parser.skipWhitespace();
                        role = parser.parseIdentifier();
                        policy.getRoles().add(role);
                    } else {
                        role = null;
                    }
                }
            }
        } else {
            policy.getRoles().add("PUBLIC");
        }

        if (parser.expectOptional("USING")){
            parser.expect("(");
            policy.setUsing(parser.getExpression());
            parser.expect(")");
        }

        if (parser.expectOptional("WITH", "CHECK")){
            parser.expect("(");
            policy.setWithCheck(parser.getExpression());
            parser.expect(")");
        }

        policy.setName(policyName);
        policy.setTableName(table.getName());
        table.addPolicy(policy);
    }

    private CreatePolicyParser() {
    }
}
