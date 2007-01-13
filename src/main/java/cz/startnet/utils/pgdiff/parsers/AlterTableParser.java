/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgConstraint;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses ALTER TABLE commands.
 *
 * @author fordfrog
 * @version $Id$
 */
public class AlterTableParser {
    /**
     * Pattern for matching ALTER TABLE ... OWNER TO ...;.
     */
    private static final Pattern PATTERN_OWNER =
        Pattern.compile("^ALTER TABLE .* OWNER TO .*;$");

    /**
     * Pattern for matching table name and optional definition.
     */
    private static final Pattern PATTERN_START =
        Pattern.compile("ALTER TABLE (?:ONLY )?([^ ]+)(?: )?(.+)?");

    /**
     * Pattern for matching of trailing definition of ALTER TABLE
     * command.
     */
    private static final Pattern PATTERN_TRAILING_DEF =
        Pattern.compile(
                "(CLUSTER ON|ALTER COLUMN) ([^ ;]+)"
                + "(?: SET STATISTICS )?(\\d+)?;?");

    /**
     * Creates a new instance of AlterTableParser.
     */
    private AlterTableParser() {
        super();
    }

    /**
     * Parses ALTER TABLE command.
     *
     * @param schema schema to be filled
     * @param command ALTER TABLE command
     *
     * @throws ParserException Thrown if problem occured while parsing DDL.
     */
    public static void parse(final PgSchema schema, final String command) {
        if (!PATTERN_OWNER.matcher(command).matches()) {
            String line = command;
            final Matcher matcher = PATTERN_START.matcher(line);
            final String tableName;

            if (matcher.find()) {
                tableName = matcher.group(1).trim();
                line =
                    ParserUtils.removeSubString(
                            line,
                            matcher.start(),
                            matcher.end());
            } else {
                throw new ParserException(
                        ParserException.CANNOT_PARSE_COMMAND + line);
            }

            final PgTable table = schema.getTable(tableName);
            final String traillingDef = matcher.group(2);

            if (traillingDef == null) {
                parseRows(table, line);
            } else {
                parseTraillingDef(table, traillingDef.trim());
            }
        }
    }

    /**
     * Parses all rows in ALTER TABLE command.
     *
     * @param table table being parsed
     * @param commands commands
     *
     * @throws ParserException Thrown if problem occured while parsing DDL.
     */
    private static void parseRows(final PgTable table, final String commands) {
        String line = commands;
        String subCommand = null;

        try {
            while (line.length() > 0) {
                final int commandEnd = ParserUtils.getCommandEnd(line, 0);
                subCommand = line.substring(0, commandEnd).trim();

                if (subCommand.startsWith("ADD CONSTRAINT ")) {
                    subCommand = subCommand.substring(
                                "ADD CONSTRAINT ".length()).trim();

                    final String constraintName =
                        subCommand.substring(0, subCommand.indexOf(' ')).trim();
                    final PgConstraint constraint =
                        table.getConstraint(constraintName);
                    constraint.setDefinition(
                            subCommand.substring(subCommand.indexOf(' ')).trim());
                    line =
                        (commandEnd >= line.length()) ? ""
                                                      : line.substring(
                                commandEnd + 1);
                }
            }
        } catch (RuntimeException ex) {
            throw new ParserException(
                    "Cannot parse ALTER TABLE '" + table.getName()
                    + "', line '" + subCommand + "'",
                    ex);
        }
    }

    /**
     * Parses trailling definition.
     *
     * @param table table being parsed
     * @param traillingDef trailling definition
     */
    private static void parseTraillingDef(
        final PgTable table,
        final String traillingDef) {
        final Matcher matcher = PATTERN_TRAILING_DEF.matcher(traillingDef);

        if (matcher.matches()) {
            if ("ALTER COLUMN".equals(matcher.group(1).trim())) {
                //Stats
                final String columnName = matcher.group(2).trim();
                final Integer value = Integer.valueOf(matcher.group(3).trim());
                final PgColumn col = table.getColumn(columnName);
                col.setStatistics(value);
            } else {
                //Cluster
                final String indexName = matcher.group(2).trim();
                table.setClusterIndexName(indexName);
            }
        }
    }
}
