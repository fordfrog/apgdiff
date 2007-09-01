/*
 * $Id$
 */
package cz.startnet.utils.pgdiff;

import java.io.PrintWriter;


/**
 * Compares two PostgreSQL dumps and outputs information about differences
 * in the database schemas.
 *
 * @author fordfrog
 * @version $Id$
 */
public class Main {
    /**
     * Creates a new Main object.
     */
    private Main() {
        super();
    }

    /**
     * APgDiff main method.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        final PrintWriter writer = new PrintWriter(System.out, true);
        final PgDiffArguments arguments = new PgDiffArguments();

        if (arguments.parse(writer, args)) {
            PgDiff.createDiff(writer, arguments);
        }
    }
}
