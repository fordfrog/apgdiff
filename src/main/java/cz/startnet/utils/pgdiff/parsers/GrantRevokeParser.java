/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.parsers;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import cz.startnet.utils.pgdiff.Resources;
import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgColumnPrivilege;
import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgFunction;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgSequence;
import cz.startnet.utils.pgdiff.schema.PgSequencePrivilege;
import cz.startnet.utils.pgdiff.schema.PgTable;
import cz.startnet.utils.pgdiff.schema.PgRelationPrivilege;
import cz.startnet.utils.pgdiff.schema.PgView;

/**
 * Parses GRANT statements.
 * 
 * @author user
 */
public class GrantRevokeParser {

    /**
     * Parses GRANT statement.
     * 
     * @param database
     *            database
     * @param statement
     *            GRANT statement
     * @param outputIgnoredStatements
     *            whether ignored statements should be output in the diff
     */
    public static void parse(final PgDatabase database, final String statement,
            final boolean outputIgnoredStatements) {
        boolean grant;
        // Map<String, List<String>> privileges = new TreeMap<String,
        // List<String>>();
        List<String> privileges = new ArrayList<String>();
        List<List<String>> privilegesColumns = new ArrayList<List<String>>();
        List<String> identifiers = new ArrayList<String>();
        List<String> roles = new ArrayList<String>();
        boolean grantOption = false;
        String revokeMode;

        final Parser parser = new Parser(statement);

        grant = parser.expect("GRANT", true);
        if (!grant) {
            parser.expect("REVOKE");
            grantOption = parser.expect("GRANT OPTION FOR", true);
        }
        String privilege = parser.expectOptionalOneOf("ALL", "SELECT",
                "INSERT", "UPDATE", "DELETE", "TRUNCATE", "REFERENCES",
                "TRIGGER", "USAGE");
        List<String> columns = null;
        if (privilege == null) {
            // unknown privilege so unsupported object privilege
            // object role_name is using a different syntax so will always pass
            // here
            if (outputIgnoredStatements) {
                database.addIgnoredStatement(statement);
                return;
            } else {
                return;
            }
        }
        if (privilege != null && "ALL".equalsIgnoreCase(privilege)) {
            parser.expectOptional("PRIVILEGES");
        }
        if (privilege != null && ("ALL".equalsIgnoreCase(privilege))
                || ("SELECT".equalsIgnoreCase(privilege))
                || ("INSERT".equalsIgnoreCase(privilege))
                || ("UPDATE".equalsIgnoreCase(privilege))
                || ("REFERENCES".equalsIgnoreCase(privilege))) {
            columns = parseColumns(parser, database, statement,
                    outputIgnoredStatements);
        } else {
            columns = null;
        }
        privileges.add(privilege);
        privilegesColumns.add(columns);

        while (privilege != null) {
            if (parser.expectOptional(",")) {
                privilege = parser.expectOptionalOneOf("SELECT", "INSERT",
                        "UPDATE", "DELETE", "TRUNCATE", "REFERENCES",
                        "TRIGGER", "USAGE");
                if (privilege != null && ("ALL".equalsIgnoreCase(privilege))
                        || ("SELECT".equalsIgnoreCase(privilege))
                        || ("INSERT".equalsIgnoreCase(privilege))
                        || ("UPDATE".equalsIgnoreCase(privilege))
                        || ("REFERENCES".equalsIgnoreCase(privilege))) {
                    columns = parseColumns(parser, database, statement,
                            outputIgnoredStatements);
                } else {
                    columns = null;
                }
                privileges.add(privilege);
                privilegesColumns.add(columns);
            } else {
                privilege = null;
            }
        }
        boolean separator = parser.expectOptional("ON");
        if (!separator) {
            // column object
            if (outputIgnoredStatements) {
                database.addIgnoredStatement(statement);
                return;
            }
        }

        // TODO check 'ALL TABLES IN SCHEMA' may not work
        String objectType = parser.expectOptionalOneOf("TABLE",
                "ALL TABLES IN SCHEMA", "SEQUENCE", "ALL SEQUENCES IN SCHEMA",
                "DATABASE", "DOMAIN", "FOREIGN DATA WRAPPER", "FOREIGN SERVER",
                "FUNCTION", "ALL FUNCTIONS IN SCHEMA", "LANGUAGE",
                "LARGE OBJECT", "SCHEMA", "TABLESPACE", "TYPE");
        if (objectType == null) {
            objectType = "TABLE";
        }

        String identifier = parser.parseIdentifier();
        if ("FUNCTION".equalsIgnoreCase(objectType)
                || "ALL FUNCTIONS IN SCHEMA".equalsIgnoreCase(objectType)) {
            parseConsumeFunctionSignature(parser, database, statement,
                    outputIgnoredStatements);
        }
        identifiers.add(identifier);
        while (identifier != null) {
            if (parser.expectOptional(",")) {
                identifier = parser.parseIdentifier();
                if ("FUNCTION".equalsIgnoreCase(objectType)
                        || "ALL FUNCTIONS IN SCHEMA"
                                .equalsIgnoreCase(objectType)) {
                    parseConsumeFunctionSignature(parser, database, statement,
                            outputIgnoredStatements);
                }
                identifiers.add(identifier);
            } else {
                identifier = null;
            }
        }

        if (grant) {
            parser.expect("TO");
        } else {
            parser.expect("FROM");
        }

        parser.expectOptional("GROUP");
        String role = parser.parseIdentifier();
        roles.add(role);
        while (role != null) {
            if (parser.expectOptional(",")) {
                parser.expectOptional("GROUP");
                role = parser.parseIdentifier();
                roles.add(role);
            } else {
                role = null;
            }
        }

        if (grant) {
            grantOption = parser.expectOptional("WITH GRANT OPTION");
        } else {
            revokeMode = parser.expectOptionalOneOf("RESTRICT", "CASCADE");
            if ("CASCADE".equalsIgnoreCase(revokeMode)) {
                if (outputIgnoredStatements) {
                    database.addIgnoredStatement(statement);
                    return;
                }
            }
        }

        if ("TABLE".equalsIgnoreCase(objectType) && columns == null) {
            for (String name : identifiers) {
                final String schemaName = ParserUtils.getSchemaName(name,
                        database);
                final PgSchema schema = database.getSchema(schemaName);

                if (schema == null) {
                    throw new RuntimeException(MessageFormat.format(
                            Resources.getString("CannotFindSchema"),
                            schemaName, statement));
                }
                final String objectName = ParserUtils.getObjectName(name);
                final PgTable table = schema.getTable(objectName);
                final PgView view = schema.getView(objectName);

                if (table != null) {
                    for (String roleName : roles) {
                        PgRelationPrivilege tablePrivilege = table
                                .getPrivilege(roleName);
                        if (tablePrivilege == null) {
                            tablePrivilege = new PgRelationPrivilege(roleName);
                            table.addPrivilege(tablePrivilege);
                        }
                        for (String priv : privileges) {
                            tablePrivilege.setPrivileges(priv, grant,
                                    grantOption);
                        }
                    }
                } else if (view != null) {
                    for (String roleName : roles) {
                        PgRelationPrivilege viewPrivilege = view
                                .getPrivilege(roleName);
                        if (viewPrivilege == null) {
                            viewPrivilege = new PgRelationPrivilege(roleName);
                            view.addPrivilege(viewPrivilege);
                        }
                        for (String priv : privileges) {
                            viewPrivilege.setPrivileges(priv, grant,
                                    grantOption);
                        }
                    }
                } else {
                    throw new RuntimeException(MessageFormat.format(
                            Resources.getString("CannotFindObject"), name,
                            statement));

                }
            }
        } else if ("TABLE".equalsIgnoreCase(objectType) && columns != null) {
            for (String name : identifiers) {
                final String schemaName = ParserUtils.getSchemaName(name,
                        database);
                final PgSchema schema = database.getSchema(schemaName);

                if (schema == null) {
                    throw new RuntimeException(MessageFormat.format(
                            Resources.getString("CannotFindSchema"),
                            schemaName, statement));
                }
                final String objectName = ParserUtils.getObjectName(name);
                final PgTable table = schema.getTable(objectName);
                final PgView view = schema.getView(objectName);

                if (table != null) {

                    for (int i = 0; i < privileges.size(); i++) {
                        String privKey = privileges.get(i);
                        List<String> privValue = privilegesColumns.get(i);

                        for (String columnName : privValue) {
                            if (table.containsColumn(columnName)) {
                                final PgColumn column = table
                                        .getColumn(columnName);
                                if (column == null) {
                                    throw new RuntimeException(
                                            MessageFormat.format(
                                                    Resources
                                                            .getString("CannotFindTableColumn"),
                                                    columnName,
                                                    table.getName(), parser
                                                            .getString()));
                                }
                                for (String roleName : roles) {
                                    PgColumnPrivilege columnPrivilege = column
                                            .getPrivilege(roleName);
                                    if (columnPrivilege == null) {
                                        columnPrivilege = new PgColumnPrivilege(
                                                roleName);
                                        column.addPrivilege(columnPrivilege);
                                    }
                                    columnPrivilege.setPrivileges(privKey,
                                            grant, grantOption);
                                }
                            } else {
                                throw new ParserException(
                                        MessageFormat.format(
                                                Resources
                                                        .getString("CannotFindColumnInTable"),
                                                columnName, table.getName()));
                            }
                        }
                    }
                } else if (view != null) {

                    for (int i = 0; i < privileges.size(); i++) {
                        String privKey = privileges.get(i);
                        List<String> privValue = privilegesColumns.get(i);

                        for (String columnName : privValue) {
                            if (view.containsColumn(columnName)) {
                                final PgColumn column = view
                                        .getColumn(columnName);
                                if (column == null) {
                                    throw new RuntimeException(
                                            MessageFormat.format(
                                                    Resources
                                                            .getString("CannotFindTableColumn"),
                                                    columnName,
                                                    view.getName(), parser
                                                            .getString()));
                                }
                                for (String roleName : roles) {
                                    PgColumnPrivilege columnPrivilege = column
                                            .getPrivilege(roleName);
                                    if (columnPrivilege == null) {
                                        columnPrivilege = new PgColumnPrivilege(
                                                roleName);
                                        column.addPrivilege(columnPrivilege);
                                    }
                                    columnPrivilege.setPrivileges(privKey,
                                            grant, grantOption);
                                }
                            } else {
                                throw new ParserException(
                                        MessageFormat.format(
                                                Resources
                                                        .getString("CannotFindColumnInTable"),
                                                columnName, view.getName()));
                            }
                        }
                    }
                } 
                else {
                    throw new RuntimeException(MessageFormat.format(
                            Resources.getString("CannotFindObject"), name,
                            statement));
                }
            }
        } else if ("SEQUENCE".equalsIgnoreCase(objectType)) {
            for (String name : identifiers) {
                // final String sequenceName = parser.parseIdentifier();
                final String schemaName = ParserUtils.getSchemaName(name,
                        database);
                final PgSchema schema = database.getSchema(schemaName);

                if (schema == null) {
                    throw new RuntimeException(MessageFormat.format(
                            Resources.getString("CannotFindSchema"),
                            schemaName, statement));
                }

                final String objectName = ParserUtils.getObjectName(name);
                final PgSequence sequence = schema.getSequence(objectName);

                if (sequence == null) {
                    throw new RuntimeException(MessageFormat.format(
                            Resources.getString("CannotFindSequence"), name,
                            statement));
                }

                for (String roleName : roles) {
                    PgSequencePrivilege sequencePrivilege = sequence
                            .getPrivilege(roleName);
                    if (sequencePrivilege == null) {
                        sequencePrivilege = new PgSequencePrivilege(roleName);
                        sequence.addPrivilege(sequencePrivilege);
                    }
                    for (String priv : privileges) {
                        sequencePrivilege.setPrivileges(priv, grant,
                                grantOption);
                    }
                }
            }
        } else {
            if (outputIgnoredStatements) {
                database.addIgnoredStatement(statement);
            }
        }

    }

    private static void parseConsumeFunctionSignature(final Parser parser,
            final PgDatabase database, final String statement,
            final boolean outputIgnoredStatements) {
        parser.expect("(");

        while (!parser.expectOptional(")")) {
            final String mode;

            if (parser.expectOptional("IN")) {
                mode = "IN";
            } else if (parser.expectOptional("OUT")) {
                mode = "OUT";
            } else if (parser.expectOptional("INOUT")) {
                mode = "INOUT";
            } else if (parser.expectOptional("VARIADIC")) {
                mode = "VARIADIC";
            } else {
                mode = null;
            }

            final int position = parser.getPosition();
            String argumentName = null;
            String dataType = parser.parseDataType();

            final int position2 = parser.getPosition();

            if (!parser.expectOptional(")") && !parser.expectOptional(",")) {
                parser.setPosition(position);
                argumentName = ParserUtils.getObjectName(parser
                        .parseIdentifier());
                dataType = parser.parseDataType();
            } else {
                parser.setPosition(position2);
            }

            final PgFunction.Argument argument = new PgFunction.Argument();
            argument.setDataType(dataType);
            argument.setMode(mode);
            argument.setName(argumentName);

            if (parser.expectOptional(")")) {
                break;
            } else {
                parser.expect(",");
            }
        }

    }

    private static List<String> parseColumns(final Parser parser,
            final PgDatabase database, final String statement,
            final boolean outputIgnoredStatements) {
        List<String> result = new ArrayList<String>();
        boolean present = parser.expectOptional("(");
        if (!present) {
            return null;
        }
        String identifier = parser.parseIdentifier();
        result.add(identifier);

        String separator = parser.expectOptionalOneOf(",", ")");
        while (separator != null && ",".equalsIgnoreCase(separator)) {
            identifier = parser.parseIdentifier();
            result.add(identifier);
            separator = parser.expectOptionalOneOf(",", ")");
        }

        return result;
    }

    /**
     * Creates a new GrantParser object.
     */
    private GrantRevokeParser() {
    }
}
