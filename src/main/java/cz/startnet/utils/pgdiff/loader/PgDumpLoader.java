/*
 * $CVSHeader$
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
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


/**
 * Loads PostgreSQL dump into classes.
 *
 * @author fordfrog
 * @version $CVSHeader$
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
     * @param file name of file containing the dump
     *
     * @return schema from dump file
     *
     * @throws UnsupportedOperationException Thrown if encoding is not
     *         supported.
     * @throws RuntimeException Thrown if file not found or problem occured
     *         while reading the file.
     */
    public static PgSchema loadSchema(final String file) {
        final PgSchema schema = new PgSchema();
        BufferedReader reader = null;

        try {
            reader =
                new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(file),
                                "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new UnsupportedOperationException("Unsupported encoding", ex);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException("File '" + file + "' not found", ex);
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
        if (line.matches("^ALTER TABLE .* OWNER TO .*;$")) {
            return;
        }

        final String tableName;

        if (line.startsWith("ALTER TABLE ONLY ")) {
            tableName = line.substring("ALTER TABLE ONLY ".length()).trim();
        } else {
            tableName = line.substring("ALTER TABLE ".length()).trim();
        }

        final PgTable table = schema.getTable(tableName);
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
            throw new RuntimeException(IO_EXCEPTION, ex);
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Cannot parse ALTER TABLE '" + tableName + "', line '"
                    + origLine + "'",
                    ex);
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

        try {
            String newLine = reader.readLine();

            while (newLine != null) {
                boolean last = false;
                origLine = newLine;
                newLine = newLine.trim();

                if (newLine.contentEquals(");")) {
                    break;
                } else if (newLine.endsWith(",")) {
                    newLine = newLine.substring(0, newLine.length() - 1).trim();
                } else if (newLine.endsWith(");")) {
                    newLine = newLine.substring(0, newLine.length() - 2).trim();
                    last = true;
                }

                if (newLine.length() == 0) {
                    newLine = reader.readLine();

                    continue;
                }

                final String columnName =
                    newLine.substring(0, newLine.indexOf(' '));
                final PgColumn column = table.getColumn(columnName);
                column.parseDefinition(
                        newLine.substring(newLine.indexOf(' ')).trim());

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
