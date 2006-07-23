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
     * Creates a new instance of Main.
     */
    public Main() {
    }

    /**
     * APgDiff main method.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: apgdiff <old_dump> <new_dump>");
        } else {
            PgDiff.createDiff(args[0], args[1]);
        }
    }
}
