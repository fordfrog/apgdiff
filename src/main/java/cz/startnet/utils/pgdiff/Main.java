/*
 * $CVSHeader$
 */
package cz.startnet.utils.pgdiff;

/**
 * Compares two PostgreSQL dumps and outputs information about differences
 * in the schemas.
 *
 * @author fordfrog
 * @version $CVSHeader$
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
        if (args.length == 2) {
            PgDiff.createDiff(args[0], args[1]);
        } else {
            System.out.println("Usage: apgdiff <old_dump> <new_dump>");
        }
    }
}
