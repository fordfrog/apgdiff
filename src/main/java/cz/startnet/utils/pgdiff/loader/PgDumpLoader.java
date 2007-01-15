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
                line = stripComment(line).trim();
                line = line.trim();

                if (line.length() == 0) {
                    line = reader.readLine();

                    continue;
                } else if (
                    line.startsWith("SET ")
                        || line.startsWith("COMMENT ")
                        || line.startsWith("SELECT ")
                        || line.startsWith("INSERT INTO ")
                        || line.startsWith("REVOKE ")
                        || line.startsWith("GRANT ")) {
                    getWholeCommand(reader, line);
                } else if (line.startsWith("CREATE TABLE ")) {
                    CreateTableParser.parse(
                            schema,
                            getWholeCommand(reader, line));
                } else if (line.startsWith("ALTER TABLE ")) {
                    AlterTableParser.parse(
                            schema,
                            getWholeCommand(reader, line));
                } else if (line.startsWith("CREATE SEQUENCE ")) {
                    CreateSequenceParser.parse(
                            schema,
                            getWholeCommand(reader, line));
                } else if (line.startsWith("CREATE INDEX ")) {
                    CreateIndexParser.parse(
                            schema,
                            getWholeCommand(reader, line));
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
     * Reads whole command from the reader into single-line string.
     *
     * @param reader reader to be read
     * @param line first line read
     *
     * @return whole command from the reader into single-line string
     *
     * @throws FileException Thrown if problem occured while reading string
     *         from <code>reader</code>.
     */
    private static String getWholeCommand(
        final BufferedReader reader,
        final String line) {
        String newLine = line.trim();
        final StringBuilder sbCommand = new StringBuilder(newLine);

        while (!newLine.trim().endsWith(";")) {
            try {
                newLine = stripComment(reader.readLine()).trim();
            } catch (IOException ex) {
                throw new FileException(FileException.CANNOT_READ_FILE, ex);
            }

            if (newLine.length() > 0) {
                sbCommand.append(' ');
                sbCommand.append(newLine);
            }
        }

        return sbCommand.toString();
    }

    /**
     * Strips comment from command line.
     *
     * @param command command
     *
     * @return if comment was found then command without the comment, otherwise
     *         the original command
     */
    private static String stripComment(final String command) {
        String result = command;
        int pos = result.indexOf("--");

        while (pos >= 0) {
            if (pos == 0) {
                result = "";

                break;
            } else {
                int count = 0;

                for (int chr = 0; chr < pos; chr++) {
                    if (chr == '\'') {
                        count++;
                    }
                }

                if ((count % 2) == 0) {
                    result = result.substring(0, pos).trim();

                    break;
                }
            }

            pos = result.indexOf("--", pos + 1);
        }

        return result;
    }
}
