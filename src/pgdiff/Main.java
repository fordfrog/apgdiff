/*
 * Main.java
 *
 * Created on 22. bøezen 2006, 20:54
 */
package pgdiff;

import cz.startnet.utils.pgdiff.PgDiff;


/**
 * Compares two PostgreSQL dumps and outputs information about differences
 * in the schemas.
 * @author fordfrog
 * @license MIT license
 */
public class Main {
    /**
     * Creates a new instance of Main.
     */
    public Main() {
    }

    /**
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
