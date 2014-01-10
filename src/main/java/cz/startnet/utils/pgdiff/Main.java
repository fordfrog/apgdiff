/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Compares two PostgreSQL dumps and outputs information about differences in
 * the database schemas.
 * 
 * @author fordfrog
 */
public class Main {

    /**
     * APgDiff main method.
     * 
     * @param args
     *            the command line arguments
     * 
     * @throws UnsupportedEncodingException
     *             Thrown if unsupported output encoding has been encountered.
     */
    public static void main(final String[] args)
            throws UnsupportedEncodingException {
        @SuppressWarnings("UseOfSystemOutOrSystemErr")
        final PrintWriter writer = new PrintWriter(System.out, true);
        final PgDiffArguments arguments = new PgDiffArguments();

        if (arguments.parse(writer, args)) {
            // localvar in case of print
            @SuppressWarnings("UseOfSystemOutOrSystemErr")
            final PrintWriter encodedWriter = new PrintWriter(
                    new OutputStreamWriter(System.out,
                            arguments.getOutCharsetName()) {
                        @Override
                        public void write(int c) throws IOException {
                            PgDiff.hasPrint = true;
                            super.write(c);
                        }

                        @Override
                        public void write(char cbuf[], int off, int len)
                                throws IOException {
                            PgDiff.hasPrint = true;
                            super.write(cbuf, off, len);
                        }

                        @Override
                        public void write(String str, int off, int len)
                                throws IOException {
                            PgDiff.hasPrint = true;
                            super.write(str, off, len);
                        }
                    });
            PgDiff.createDiff(encodedWriter, arguments);
            encodedWriter.close();
        }

        writer.close();
        if (PgDiff.isDifferent) {
            System.exit(1);
        }
    }

    /**
     * Creates a new Main object.
     */
    private Main() {
    }
}
