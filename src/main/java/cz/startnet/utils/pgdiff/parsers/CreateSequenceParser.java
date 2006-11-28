/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.loader.FileException;
import cz.startnet.utils.pgdiff.schema.PgSchema;

import java.io.BufferedReader;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Parses CREATE SEQUENCE commands.
 *
 * @author fordfrog
 * @version $Id$
 */
public class CreateSequenceParser {
    /**
     * Pattern for getting sequence name.
     */
    private static final Pattern PATTERN_SEQUENCE_NAME =
        Pattern.compile("CREATE SEQUENCE ([^ ]+)");

    /**
     * Creates a new instance of CreateSequenceParser.
     */
    private CreateSequenceParser() {
        super();
    }

    /**
     * Parses CREATE SEQUENCE command.
     *
     * @param schema schema to be filled
     * @param reader reader of the dump file
     * @param line first line read
     *
     * @throws ParserException Thrown if problem occured while parsing the
     *         command.
     * @throws FileException Thrown if problem occured while reading dump file.
     */
    public static void parse(
        final PgSchema schema,
        final BufferedReader reader,
        final String line) {
        final Matcher matcher = PATTERN_SEQUENCE_NAME.matcher(line);
        final String sequenceName;

        if (matcher.matches()) {
            sequenceName = matcher.group(1);
        } else {
            throw new ParserException(
                    ParserException.CANNOT_PARSE_COMMAND + line);
        }

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
            throw new FileException(FileException.CANNOT_READ_FILE, ex);
        } catch (RuntimeException ex) {
            throw new ParserException(
                    "Cannot parse CREATE SEQUENCE '" + sequenceName
                    + "', line '" + origLine + "'",
                    ex);
        }

        schema.getSequence(sequenceName)
              .setDefinition(sbDefinition.toString().trim());
    }
}
