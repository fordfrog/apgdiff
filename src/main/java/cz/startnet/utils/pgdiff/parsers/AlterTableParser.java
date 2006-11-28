/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.loader.FileException;
import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgConstraint;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;

import java.io.BufferedReader;
import java.io.IOException;

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
     * @param reader reader of the dump file
     * @param line first line read
     *
     * @throws ParserException Thrown if problem occured while parsing DDL.
     */
    public static void parse(
        final PgSchema schema,
        final BufferedReader reader,
        final String line) {
        if (!PATTERN_OWNER.matcher(line).matches()) {
            final Matcher matcher = PATTERN_START.matcher(line);

            final String tableName;

            if (matcher.matches()) {
                tableName = matcher.group(1).trim();
            } else {
                throw new ParserException(
                        ParserException.CANNOT_PARSE_COMMAND + line);
            }

            final PgTable table = schema.getTable(tableName);
            final String traillingDef = matcher.group(2);

            if (traillingDef == null) {
                parseRows(table, reader);
            } else {
                parseTraillingDef(table, traillingDef.trim());
            }
        }
    }

    /**
     * Parses all rows in ALTER TABLE command.
     *
     * @param table table being parsed
     * @param reader reader for reading the dump
     *
     * @throws FileException Thrown if problem occured while reading the dump
     *         file.
     * @throws ParserException Thrown if problem occured while parsing DDL.
     */
    private static void parseRows(
        final PgTable table,
        final BufferedReader reader) {
        String origLine = null;

        try {
            String newLine = reader.readLine();

            while (newLine != null) {
                boolean last = false;
                origLine = newLine;
                newLine = newLine.trim();

                if (newLine.endsWith(";")) {
                    last = true;
                    newLine = newLine.substring(0, newLine.length() - 1);
                }

                if (newLine.startsWith("ADD CONSTRAINT ")) {
                    newLine = newLine.substring("ADD CONSTRAINT ".length())
                                     .trim();

                    final String constraintName =
                        newLine.substring(0, newLine.indexOf(' ')).trim();
                    final PgConstraint constraint =
                        table.getConstraint(constraintName);
                    constraint.setDefinition(
                            newLine.substring(newLine.indexOf(' ')).trim());
                }

                if (last) {
                    break;
                }

                newLine = reader.readLine();
            }
        } catch (IOException ex) {
            throw new FileException(FileException.CANNOT_READ_FILE, ex);
        } catch (RuntimeException ex) {
            throw new ParserException(
                    "Cannot parse ALTER TABLE '" + table.getName()
                    + "', line '" + origLine + "'",
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
