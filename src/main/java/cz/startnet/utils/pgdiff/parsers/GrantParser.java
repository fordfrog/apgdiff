/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.Resources;
import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgGrant;
import java.text.MessageFormat;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Parses CREATE TABLE statements.
 *
 * @author fordfrog
 */
public class GrantParser {

    /**
     * Parses CREATE TYPE statement.
     *
     * @param database database
     * @param statement CREATE TYPE statement
     */
    public static void parse(final PgDatabase database,
            final String statement) {

        final String[] privList = new String[]{"SELECT", "INSERT", "UPDATE",
            "DELETE", "TRUNCATE", "REFERENCES", "TRIGGER",
            "CREATE", "CONNECT", "TEMPORARY", "TEMP", "ALL", "USAGE"};
        final Parser parser = new Parser(statement);
        PgGrant grant = new PgGrant();
        parser.expect("GRANT");

        final String schemaName
                = ParserUtils.getSchemaName("", database);
        final PgSchema schema = database.getSchema(schemaName);

        if (schema == null) {
            throw new RuntimeException(MessageFormat.format(
                    Resources.getString("CannotFindSchema"), schemaName,
                    statement));
        }

        schema.addGrant(grant);

        String priv;
        do {
            priv = parser.expectOptionalOneOf(privList);
            if (priv == null) {
                break;
            }
            grant.addPrivilege(priv);
            parser.expectOptional(",");
        } while (true);

        parser.expect("ON");
        String object = "";
        String type = parser.parseIdentifier();
        if (type.equals("function")) {

            String functionName = parser.parseIdentifier();
            object += type + " " + functionName + "(";
            boolean first = true;
            parser.expect("(");
            while (!parser.expectOptional(")")) {
                String arg = parser.parseDataType();
                parser.expectOptional(",");

                if (!first) {
                    object += ", ";
                }
                else
                {
                    first = false;
                }
                object += arg;
            }
            object += ")";
        } else {
            object = type + " ";
            object += parser.parseIdentifier();
        }

        parser.expect("TO");
        grant.setObject(object);

        final String role = parser.parseIdentifier();
        grant.setRole(role);

        while (!parser.expectOptional(";")) {

        }

    }

    /**
     * Creates a new instance of CreateTableParser.
     */
    private GrantParser() {
    }
}
