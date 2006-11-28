/*
 * $Id$
 */
package cz.startnet.utils.pgdiff;

import java.io.PrintWriter;


/**
 * Contains parsed command line arguments.
 *
 * @author fordfrog
 * @version $Id$
 */
public class PgDiffArguments {
    /**
     * Path to the new dump file.
     */
    private String newDumpFile = null;

    /**
     * Path to the original dump file.
     */
    private String oldDumpFile = null;

    /**
     * Whether to ignore START WITH on SEQUENCEs.
     */
    private boolean ignoreStartWith = false;

    /**
     * Setter for {@link #ignoreStartWith ignoreStartWith}.
     *
     * @param ignoreStartWith {@link #ignoreStartWith ignoreStartWith}
     */
    public void setIgnoreStartWith(final boolean ignoreStartWith) {
        this.ignoreStartWith = ignoreStartWith;
    }

    /**
     * Getter for {@link #ignoreStartWith ignoreStartWith}.
     *
     * @return {@link #ignoreStartWith ignoreStartWith}
     */
    public boolean isIgnoreStartWith() {
        return ignoreStartWith;
    }

    /**
     * Setter for {@link #newDumpFile newDumpFile}.
     *
     * @param newDumpFile {@link #newDumpFile newDumpFile}
     */
    public void setNewDumpFile(final String newDumpFile) {
        this.newDumpFile = newDumpFile;
    }

    /**
     * Getter for {@link #newDumpFile newDumpFile}.
     *
     * @return {@link #newDumpFile newDumpFile}
     */
    public String getNewDumpFile() {
        return newDumpFile;
    }

    /**
     * Setter for {@link #oldDumpFile oldDumpFile}.
     *
     * @param oldDumpFile {@link #oldDumpFile oldDumpFile}
     */
    public void setOldDumpFile(final String oldDumpFile) {
        this.oldDumpFile = oldDumpFile;
    }

    /**
     * Getter for {@link #oldDumpFile oldDumpFile}.
     *
     * @return {@link #oldDumpFile oldDumpFile}
     */
    public String getOldDumpFile() {
        return oldDumpFile;
    }

    /**
     * Parses command line arguments or outputs instructions.
     *
     * @param writer writer to be used for info output
     * @param args array of arguments
     *
     * @return true if arguments were parsed and execution can continue,
     *         otherwise false
     */
    public boolean parse(final PrintWriter writer, final String[] args) {
        boolean success = true;

        if (args.length < 2) {
            printUsage(writer);
            success = false;
        } else {
            for (int i = 0; i < (args.length - 2); i++) {
                if ("--ignore-start-with".equals(args[i])) {
                    setIgnoreStartWith(true);
                } else {
                    writer.println("ERROR: Unknown option: " + args[i]);
                    success = false;

                    break;
                }
            }
        }

        if (success) {
            setOldDumpFile(args[args.length - 2]);
            setNewDumpFile(args[args.length - 1]);
        }

        return success;
    }

    /**
     * Prints program usage.
     *
     * @param writer writer to print the usage to
     */
    private void printUsage(final PrintWriter writer) {
        writer.println("Usage: apgdiff [options] <old_dump> <new_dump>");
        writer.println();
        writer.println("Options:");
        writer.println("--ignore-start-with: ignores START WITH modifications");
        writer.println("     on SEQUENCEs (default is not to ignore these");
        writer.println("     changes)");
    }
}
