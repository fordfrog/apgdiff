/*
 * $CVSHeader$
 */
package cz.startnet.utils.pgdiff;

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
 */
public class PgDumpLoader {
    /**
     * Creates a new instance of PgDumpLoader.
     */
    private PgDumpLoader() {
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
    public static PgSchema loadSchema(String file) {
        PgSchema schema = new PgSchema();
        BufferedReader reader = null;

        try {
            reader =
                new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(file),
                                "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            throw new UnsupportedOperationException("Unsupported encoding");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            throw new RuntimeException("File '" + file + "' not found");
        }

        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.length() == 0) {
                    continue;
                } else if (line.startsWith("--")) {
                    processDashComment(schema, reader, line);
                } else if (line.startsWith("SET ")) {
                    processSet(schema, reader, line);
                } else if (line.startsWith("COMMENT ")) {
                    processComment(schema, reader, line);
                } else if (line.startsWith("CREATE TABLE ")) {
                    processCreateTable(schema, reader, line);
                } else if (line.startsWith("ALTER TABLE ")) {
                    processAlterTable(schema, reader, line);
                } else if (line.startsWith("CREATE SEQUENCE ")) {
                    processCreateSequence(schema, reader, line);
                } else if (line.startsWith("SELECT ")) {
                    processSelect(schema, reader, line);
                } else if (line.startsWith("INSERT INTO ")) {
                    processInsertInto(schema, reader, line);
                } else if (line.startsWith("CREATE INDEX ")) {
                    processCreateIndex(schema, reader, line);
                } else if (line.startsWith("REVOKE ")) {
                    processRevoke(schema, reader, line);
                } else if (line.startsWith("GRANT ")) {
                    processGrant(schema, reader, line);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("IO exception");
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
    private static void moveToEndOfCommand(BufferedReader reader) {
        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                if (line.trim().endsWith(";")) {
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("IO exception");
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
        PgSchema schema,
        BufferedReader reader,
        String line) {
        if (line.matches("^ALTER TABLE .* OWNER TO .*;$")) {
            return;
        }

        String tableName = null;

        if (line.startsWith("ALTER TABLE ONLY ")) {
            tableName = line.substring("ALTER TABLE ONLY ".length()).trim();
        } else {
            tableName = line.substring("ALTER TABLE ".length()).trim();
        }

        PgTable table = schema.getTable(tableName);
        String origLine = null;

        try {
            while ((line = reader.readLine()) != null) {
                boolean last = false;
                origLine = line;
                line = line.trim();

                if (line.endsWith(";")) {
                    last = true;
                    line = line.substring(0, line.length() - 1);
                }

                if (line.startsWith("ADD CONSTRAINT ")) {
                    line = line.substring("ADD CONSTRAINT ".length()).trim();

                    String constraintName =
                        line.substring(0, line.indexOf(" ")).trim();
                    PgConstraint constraint =
                        table.getConstraint(constraintName);
                    constraint.setDefinition(
                            line.substring(line.indexOf(" ")).trim());
                }

                if (last) {
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("IO exception");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(
                    "Cannot parse ALTER TABLE '" + tableName + "', line '"
                    + origLine + "'");
        }
    }

    /**
     * Processes COMMENT command.
     *
     * @param schema schema to be filled
     * @param reader reader of the dump file
     * @param line first line read
     */
    private static void processComment(
        PgSchema schema,
        BufferedReader reader,
        String line) {
        if (!line.endsWith(";")) {
            moveToEndOfCommand(reader);
        }
    }

    /**
     * Processes CREATE INDEX command.
     *
     * @param schema schema to be filled
     * @param reader reader of the dump file
     * @param line first line read
     *
     * @throws RuntimeException Thrown if problem occured while parsing the
     *         command.
     */
    private static void processCreateIndex(
        PgSchema schema,
        BufferedReader reader,
        String line) {
        String origLine = line;
        String indexName = null;

        try {
            line = line.substring("CREATE INDEX ".length()).trim();

            if (line.endsWith(";")) {
                line = line.substring(0, line.length() - 1).trim();
            }

            indexName = line.substring(0, line.indexOf(" ")).trim();
            line = line.substring(indexName.length()).trim();

            if (line.startsWith("ON ")) {
                line = line.substring("ON ".length()).trim();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(
                    "Cannot parse CREATE INDEX '" + indexName + "', line '"
                    + origLine + "'");
        }

        String tableName = line.substring(0, line.indexOf(" ")).trim();
        String definition = line.substring(tableName.length()).trim();
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
        PgSchema schema,
        BufferedReader reader,
        String line) {
        String sequenceName =
            line.substring("CREATE SEQUENCE ".length()).trim();
        StringBuilder sbDefinition = new StringBuilder();
        String origLine = null;

        try {
            while ((line = reader.readLine()) != null) {
                boolean last = false;
                origLine = line;
                line = line.trim();

                if (line.endsWith(";")) {
                    last = true;
                    line = line.substring(0, line.length() - 1);
                }

                sbDefinition.append(line + " ");

                if (last) {
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("IO exception");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(
                    "Cannot parse CREATE SEQUENCE '" + sequenceName
                    + "', line '" + origLine + "'");
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
        PgSchema schema,
        BufferedReader reader,
        String line) {
        String tableName = line.substring("CREATE TABLE ".length()).trim();

        if (tableName.endsWith("(")) {
            tableName = tableName.substring(0, tableName.length() - 1).trim();
        }

        PgTable table = schema.getTable(tableName);
        String origLine = null;

        try {
            while ((line = reader.readLine()) != null) {
                boolean last = false;
                origLine = line;
                line = line.trim();

                if (line.contentEquals(");")) {
                    break;
                } else if (line.endsWith(",")) {
                    line = line.substring(0, line.length() - 1).trim();
                } else if (line.endsWith(");")) {
                    line = line.substring(0, line.length() - 2).trim();
                    last = true;
                }

                if (line.length() == 0) {
                    continue;
                }

                String columnName = line.substring(0, line.indexOf(" "));
                PgColumn column = table.getColumn(columnName);
                column.parseDefinition(
                        line.substring(line.indexOf(" ")).trim());

                if (last) {
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("IO exception");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(
                    "Cannot parse CREATE TABLE '" + tableName + "', line '"
                    + origLine + "'");
        }
    }

    /**
     * Processes '--' comment.
     *
     * @param schema schema to be filled
     * @param reader reader of the dump file
     * @param line first line read
     */
    private static void processDashComment(
        PgSchema schema,
        BufferedReader reader,
        String line) {
    }

    /**
     * Processes GRANT command.
     *
     * @param schema schema to be filled
     * @param reader reader of the dump file
     * @param line first line read
     */
    private static void processGrant(
        PgSchema schema,
        BufferedReader reader,
        String line) {
        if (!line.endsWith(";")) {
            moveToEndOfCommand(reader);
        }
    }

    /**
     * Processes INSERT INTO command.
     *
     * @param schema schema to be filled
     * @param reader reader of the dump file
     * @param line first line read
     */
    private static void processInsertInto(
        PgSchema schema,
        BufferedReader reader,
        String line) {
        if (!line.endsWith(";")) {
            moveToEndOfCommand(reader);
        }
    }

    /**
     * Processes REVOKE command.
     *
     * @param schema schema to be filled
     * @param reader reader of the dump file
     * @param line first line read
     */
    private static void processRevoke(
        PgSchema schema,
        BufferedReader reader,
        String line) {
        if (!line.endsWith(";")) {
            moveToEndOfCommand(reader);
        }
    }

    /**
     * Processes SELECT command.
     *
     * @param schema schema to be filled
     * @param reader reader of the dump file
     * @param line first line read
     */
    private static void processSelect(
        PgSchema schema,
        BufferedReader reader,
        String line) {
        if (!line.endsWith(";")) {
            moveToEndOfCommand(reader);
        }
    }

    /**
     * Processes SET command.
     *
     * @param schema schema to be filled
     * @param reader reader of the dump file
     * @param line first line read
     */
    private static void processSet(
        PgSchema schema,
        BufferedReader reader,
        String line) {
        if (!line.endsWith(";")) {
            moveToEndOfCommand(reader);
        }
    }
}
