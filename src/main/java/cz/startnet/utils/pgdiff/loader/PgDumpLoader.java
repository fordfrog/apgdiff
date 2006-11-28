/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.loader;

import cz.startnet.utils.pgdiff.parsers.AlterTableParser;
import cz.startnet.utils.pgdiff.parsers.CreateIndexParser;
import cz.startnet.utils.pgdiff.parsers.CreateSequenceParser;
import cz.startnet.utils.pgdiff.parsers.CreateTableParser;
import cz.startnet.utils.pgdiff.schema.PgSchema;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;


/**
 * Loads PostgreSQL dump into classes.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgDumpLoader { //NOPMD

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
     * @throws FileException Thrown if problem occured while reading input
     *         stream.
     */
    public static PgSchema loadSchema(final InputStream inputStream) { //NOPMD

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
                    CreateTableParser.parse(schema, reader, line);
                } else if (line.startsWith("ALTER TABLE ")) {
                    AlterTableParser.parse(schema, reader, line);
                } else if (line.startsWith("CREATE SEQUENCE ")) {
                    CreateSequenceParser.parse(schema, reader, line);
                } else if (line.startsWith("SELECT ")) {
                    processSelect(reader, line);
                } else if (line.startsWith("INSERT INTO ")) {
                    processInsertInto(reader, line);
                } else if (line.startsWith("CREATE INDEX ")) {
                    CreateIndexParser.parse(schema, line);
                } else if (line.startsWith("REVOKE ")) {
                    processRevoke(reader, line);
                } else if (line.startsWith("GRANT ")) {
                    processGrant(reader, line);
                }

                line = reader.readLine();
            }
        } catch (IOException ex) {
            throw new FileException(FileException.CANNOT_READ_FILE, ex);
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
     * @throws FileException Thrown if file not found.
     */
    public static PgSchema loadSchema(final String file) {
        try {
            return loadSchema(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            throw new FileException("File '" + file + "' not found", ex);
        }
    }

    /**
     * Reads current reader till end of command is reached.
     *
     * @param reader reader to be read
     *
     * @throws FileException Thrown if problem occured while reading the file.
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
            throw new FileException(FileException.CANNOT_READ_FILE, ex);
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
