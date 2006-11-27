/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.loader;

import cz.startnet.utils.pgdiff.schema.PgColumn;
import cz.startnet.utils.pgdiff.schema.PgConstraint;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import cz.startnet.utils.pgdiff.schema.PgTable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Loads PostgreSQL dump into classes.
 *
 * @author fordfrog
 * @version $Id$
 *
 * @todo Needs review.
 */
public class PgDumpLoader {
    /**
     * Info text for IO exception.
     */
    private static final String IO_EXCEPTION = "IO exception";

    /**
     * Creates a new instance of PgDumpLoader.
     */
    private PgDumpLoader() {
        super();
    }

    /**
     * Loads schema from dump file.
     *
     * @param inputStream input stream that should be read
     *
     * @return schema from dump fle
     *
     * @throws UnsupportedOperationException Thrown if unsupported encoding has
     *         been encountered.
     * @throws RuntimeException Thrown if problem occured while reading input
     *         stream.
     */
    public static PgSchema loadSchema(final InputStream inputStream) {
        final PgSchema schema = new PgSchema();
        BufferedReader reader = null;

        try {
            reader =
                new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new UnsupportedOperationException("Unsupported encoding", ex);
        }

        try {
            String line = reader.readLine();

            while (line != null) {
                line = line.trim();

                // '--' comments are ignored
                if (line.length() == 0) {
                    line = reader.readLine();

                    continue;
                } else if (line.startsWith("SET ")) {
                    processSet(reader, line);
                } else if (line.startsWith("COMMENT ")) {
                    processComment(reader, line);
                } else if (line.startsWith("CREATE TABLE ")) {
                    processCreateTable(schema, reader, line);
                } else if (line.startsWith("ALTER TABLE ")) {
                    processAlterTable(schema, reader, line);
                } else if (line.startsWith("CREATE SEQUENCE ")) {
                    processCreateSequence(schema, reader, line);
                } else if (line.startsWith("SELECT ")) {
                    processSelect(reader, line);
                } else if (line.startsWith("INSERT INTO ")) {
                    processInsertInto(reader, line);
                } else if (line.startsWith("CREATE INDEX ")) {
                    processCreateIndex(schema, line);
                } else if (line.startsWith("REVOKE ")) {
                    processRevoke(reader, line);
                } else if (line.startsWith("GRANT ")) {
                    processGrant(reader, line);
                }

                line = reader.readLine();
            }
        } catch (IOException ex) {
            throw new RuntimeException(IO_EXCEPTION, ex);
        }

        return schema;
    }

    /**
     * Loads schema from dump file.
     *
     * @param file name of file containing the dump
     *
     * @return schema from dump file
     *
     * @throws RuntimeException Thrown if file not found.
     */
    public static PgSchema loadSchema(final String file) {
        try {
            return loadSchema(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("File '" + file + "' not found", ex);
        }
    }

    /**
     * Reads current reader till end of command is reached.
     *
     * @param reader reader to be read
     *
     * @throws RuntimeException Thrown if problem occured while reading the
     *         file.
     */
    private static void moveToEndOfCommand(final BufferedReader reader) {
        String line = null;

        try {
            line = reader.readLine();

            while (line != null) {
                if (line.trim().endsWith(";")) {
                    break;
                }

                line = reader.readLine();
            }
        } catch (IOException ex) {
            throw new RuntimeException(IO_EXCEPTION, ex);
        }
    }

    /**
     * Processes ALTER TABLE command.
     *
     * @param schema schema to be filled
     * @param reader reader of the dump file
     * @param line first line read
     *
     * @throws RuntimeException Thrown if problem occured while reading the
     *         file or while parsing the file.
     */
    private static void processAlterTable(
        final PgSchema schema,
        final BufferedReader reader,
        final String line) {
        if (!line.matches("^ALTER TABLE .* OWNER TO .*;$")) {
            Pattern pattern =
                Pattern.compile("ALTER TABLE (?:ONLY )?([^ ]+)(?: )?(.+)?");
            Matcher matcher = pattern.matcher(line);

            final String tableName;

            if (matcher.matches()) {
                tableName = matcher.group(1);
            } else {
                throw new RuntimeException("Cannot parse command: " + line);
            }

            final PgTable table = schema.getTable(tableName);
            String origLine = null;
            final String traillingDef = matcher.group(2);

            if (traillingDef == null) {
                try {
                    String newLine = reader.readLine();

                    while (newLine != null) {
                        boolean last = false;
                        origLine = newLine;
                        newLine = newLine.trim();

                        if (newLine.endsWith(";")) {
                            last = true;
                            newLine = newLine.substring(
                                        0,
                                        newLine.length() - 1);
                        }

                        if (newLine.startsWith("ADD CONSTRAINT ")) {
                            newLine =
                                newLine.substring("ADD CONSTRAINT ".length())
                                       .trim();

                            final String constraintName =
                                newLine.substring(0, newLine.indexOf(' ')).trim();
                            final PgConstraint constraint =
                                table.getConstraint(constraintName);
                            constraint.setDefinition(
                                    newLine.substring(newLine.indexOf(' '))
                                           .trim());
                        }

                        if (last) {
                            break;
                        }

                        newLine = reader.readLine();
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(IO_EXCEPTION, ex);
                } catch (Exception ex) {
                    throw new RuntimeException(
                            "Cannot parse ALTER TABLE '" + tableName
                            + "', line '" + origLine + "'",
                            ex);
                }
            } else {
                pattern =
                    Pattern.compile(
                            "(CLUSTER ON|ALTER COLUMN) ([^ ;]+)"
                            + "(?: SET STATISTICS )?(\\d+)?;?");
                matcher = pattern.matcher(traillingDef);

                if (matcher.matches()) {
                    if ("ALTER COLUMN".equals(matcher.group(1))) {
                        //Stats
                        final String columnName = matcher.group(2);
                        final Integer value = Integer.valueOf(matcher.group(3));
                        final PgColumn col = table.getColumn(columnName);
                        col.setStatistics(value);
                    } else {
                        //Cluster
                        final String indexName = matcher.group(2);
                        table.setClusterIndexName(indexName);
                    }
                }
            }
        }
    }

    /**
     * Processes COMMENT command.
     *
     * @param reader reader of the dump file
     * @param line first line read
     */
    private static void processComment(
        final BufferedReader reader,
        final String line) {
        if (!line.endsWith(";")) {
            moveToEndOfCommand(reader);
        }
    }

    /**
     * Processes CREATE INDEX command.
     *
     * @param schema schema to be filled
     * @param line first line read
     *
     * @throws RuntimeException Thrown if problem occured while parsing the
     *         command.
     */
    private static void processCreateIndex(
        final PgSchema schema,
        final String line) {
        final String origLine = line;
        String indexName = null;
        String newLine = line;

        try {
            newLine = newLine.substring("CREATE INDEX ".length()).trim();

            if (newLine.endsWith(";")) {
                newLine = newLine.substring(0, newLine.length() - 1).trim();
            }

            indexName = newLine.substring(0, newLine.indexOf(' ')).trim();
            newLine = newLine.substring(indexName.length()).trim();

            if (newLine.startsWith("ON ")) {
                newLine = newLine.substring("ON ".length()).trim();
            }
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Cannot parse CREATE INDEX '" + indexName + "', line '"
                    + origLine + "'",
                    ex);
        }

        final String tableName =
            newLine.substring(0, newLine.indexOf(' ')).trim();
        final String definition = newLine.substring(tableName.length()).trim();
        schema.getTable(tableName).getIndex(indexName).setDefinition(
                definition);
    }

    /**
     * Processes CREATE SEQUENCE command.
     *
     * @param schema schema to be filled
     * @param reader reader of the dump file
     * @param line first line read
     *
     * @throws RuntimeException Thrown if problem occured while parsing the
     *         command.
     */
    private static void processCreateSequence(
        final PgSchema schema,
        final BufferedReader reader,
        final String line) {
        final String sequenceName =
            line.substring("CREATE SEQUENCE ".length()).trim();
        final StringBuilder sbDefinition = new StringBuilder();
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

                sbDefinition.append(newLine + " ");

                if (last) {
                    break;
                }

                newLine = reader.readLine();
            }
        } catch (IOException ex) {
            throw new RuntimeException(IO_EXCEPTION, ex);
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Cannot parse CREATE SEQUENCE '" + sequenceName
                    + "', line '" + origLine + "'",
                    ex);
        }

        schema.getSequence(sequenceName)
              .setDefinition(sbDefinition.toString().trim());
    }

    /**
     * Processes CREATE TABLE command.
     *
     * @param schema schema to be filled
     * @param reader reader of the dump file
     * @param line first line read
     *
     * @throws RuntimeException Thrown if problem occured while parsing the
     *         command.
     */
    private static void processCreateTable(
        final PgSchema schema,
        final BufferedReader reader,
        final String line) {
        String tableName = line.substring("CREATE TABLE ".length()).trim();

        if (tableName.endsWith("(")) {
            tableName = tableName.substring(0, tableName.length() - 1).trim();
        }

        final PgTable table = schema.getTable(tableName);
        String origLine = null;
        boolean postColumns = false;

        try {
            String newLine = reader.readLine();

            while (newLine != null) {
                boolean last = false;
                origLine = newLine;
                newLine = newLine.trim();

                if (postColumns) {
                    if (newLine.endsWith(";")) {
                        newLine = newLine.substring(0, newLine.length() - 1);
                        last = true;
                    }

                    if (newLine.startsWith("INHERITS ")) {
                        table.setInherits(
                                newLine.substring("INHERITS ".length()).trim());
                    }
                } else {
                    if (")".equals(newLine)) {
                        postColumns = true;

                        continue;
                    } else if (");".equals(newLine)) {
                        break;
                    } else if (newLine.endsWith(",")) {
                        newLine = newLine.substring(0, newLine.length() - 1)
                                         .trim();
                    } else if (newLine.endsWith(");")) {
                        newLine = newLine.substring(0, newLine.length() - 2)
                                         .trim();
                        last = true;
                    }

                    if (newLine.length() == 0) {
                        newLine = reader.readLine();

                        continue;
                    }

                    if (newLine.startsWith("CONSTRAINT ")) {
                        newLine = newLine.substring("CONSTRAINT ".length())
                                         .trim();

                        final String constraintName =
                            newLine.substring(0, newLine.indexOf(' '));
                        table.getConstraint(constraintName).setDefinition(
                                newLine.substring(newLine.indexOf(' ')).trim());
                    } else {
                        final String columnName =
                            newLine.substring(0, newLine.indexOf(' '));
                        final PgColumn column = table.getColumn(columnName);
                        column.parseDefinition(
                                newLine.substring(newLine.indexOf(' ')).trim());
                    }
                }

                if (last) {
                    break;
                }

                newLine = reader.readLine();
            }
        } catch (IOException ex) {
            throw new RuntimeException(IO_EXCEPTION, ex);
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Cannot parse CREATE TABLE '" + tableName + "', line '"
                    + origLine + "'",
                    ex);
        }
    }

    /**
     * Processes GRANT command.
     *
     * @param reader reader of the dump file
     * @param line first line read
     */
    private static void processGrant(
        final BufferedReader reader,
        final String line) {
        if (!line.endsWith(";")) {
            moveToEndOfCommand(reader);
        }
    }

    /**
     * Processes INSERT INTO command.
     *
     * @param reader reader of the dump file
     * @param line first line read
     */
    private static void processInsertInto(
        final BufferedReader reader,
        final String line) {
        if (!line.endsWith(";")) {
            moveToEndOfCommand(reader);
        }
    }

    /**
     * Processes REVOKE command.
     *
     * @param reader reader of the dump file
     * @param line first line read
     */
    private static void processRevoke(
        final BufferedReader reader,
        final String line) {
        if (!line.endsWith(";")) {
            moveToEndOfCommand(reader);
        }
    }

    /**
     * Processes SELECT command.
     *
     * @param reader reader of the dump file
     * @param line first line read
     */
    private static void processSelect(
        final BufferedReader reader,
        final String line) {
        if (!line.endsWith(";")) {
            moveToEndOfCommand(reader);
        }
    }

    /**
     * Processes SET command.
     *
     * @param reader reader of the dump file
     * @param line first line read
     */
    private static void processSet(
        final BufferedReader reader,
        final String line) {
        if (!line.endsWith(";")) {
            moveToEndOfCommand(reader);
        }
    }
}
