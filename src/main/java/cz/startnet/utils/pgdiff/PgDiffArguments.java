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
     * Whether DEFAULT ... should be added in case new column has NOT
     * NULL constraint. The default value is dropped later.
     */
    private boolean addDefaults = false;

    /**
     * Whether to ignore START WITH on SEQUENCEs.
     */
    private boolean ignoreStartWith = false;

    /**
     * Whether to quote names when creating the diff SQL commands.
     */
    private boolean quoteNames = false;

    /**
     * Setter for {@link #addDefaults}.
     *
     * @param addDefaults {@link #addDefaults}
     */
    public void setAddDefaults(final boolean addDefaults) {
        this.addDefaults = addDefaults;
    }

    /**
     * Getter for {@link #addDefaults}.
     *
     * @return {@link #addDefaults}
     */
    public boolean isAddDefaults() {
        return addDefaults;
    }

    /**
     * Setter for {@link #ignoreStartWith}.
     *
     * @param ignoreStartWith {@link #ignoreStartWith}
     */
    public void setIgnoreStartWith(final boolean ignoreStartWith) {
        this.ignoreStartWith = ignoreStartWith;
    }

    /**
     * Getter for {@link #ignoreStartWith}.
     *
     * @return {@link #ignoreStartWith}
     */
    public boolean isIgnoreStartWith() {
        return ignoreStartWith;
    }

    /**
     * Setter for {@link #newDumpFile}.
     *
     * @param newDumpFile {@link #newDumpFile}
     */
    public void setNewDumpFile(final String newDumpFile) {
        this.newDumpFile = newDumpFile;
    }

    /**
     * Getter for {@link #newDumpFile}.
     *
     * @return {@link #newDumpFile}
     */
    public String getNewDumpFile() {
        return newDumpFile;
    }

    /**
     * Setter for {@link #oldDumpFile}.
     *
     * @param oldDumpFile {@link #oldDumpFile}
     */
    public void setOldDumpFile(final String oldDumpFile) {
        this.oldDumpFile = oldDumpFile;
    }

    /**
     * Getter for {@link #oldDumpFile}.
     *
     * @return {@link #oldDumpFile}
     */
    public String getOldDumpFile() {
        return oldDumpFile;
    }

    /**
     * Setter for {@link #quoteNames}.
     *
     * @param quoteNames {@link #quoteNames}
     */
    public void setQuoteNames(final boolean quoteNames) {
        this.quoteNames = quoteNames;
    }

    /**
     * Getter for {@link #quoteNames}.
     *
     * @return {@link #quoteNames}
     */
    public boolean isQuoteNames() {
        return quoteNames;
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
                if ("--add-defaults".equals(args[i])) {
                    setAddDefaults(true);
                } else if ("--ignore-start-with".equals(args[i])) {
                    setIgnoreStartWith(true);
                } else if ("--quote-names".equals(args[i])) {
                    setQuoteNames(true);
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
        writer.println("--add-defaults: adds DEFAULT ... in case new column");
        writer.println("     NOT NULL constraint but no default value");
        writer.println("     (the default value is dropped later)");
        writer.println("--ignore-start-with: ignores START WITH modifications");
        writer.println("     on SEQUENCEs (default is not to ignore these");
        writer.println("     changes)");
        writer.println("--quote-names: adds quotes to names");
    }
}
