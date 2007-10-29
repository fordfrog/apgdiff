/*
 * $Id$
 */
package cz.startnet.utils.pgdiff;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private String newDumpFile;

    /**
     * Path to the original dump file.
     */
    private String oldDumpFile;

    /**
     * Whether DEFAULT ... should be added in case new column has NOT
     * NULL constraint. The default value is dropped later.
     */
    private boolean addDefaults;

    /**
     * Whether to enclose all commands in transaction.
     */
    private boolean addTransaction;

    /**
     * Whether to ignore START WITH on SEQUENCEs.
     */
    private boolean ignoreStartWith;

    /**
     * Whether to quote names when creating the diff SQL commands.
     */
    private boolean quoteNames;

    /**
     * Whether to display apgdiff version.
     */
    private boolean version;

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
     * Setter for {@link #addTransaction}.
     *
     * @param addTransaction {@link #addTransaction}
     */
    public void setAddTransaction(final boolean addTransaction) {
        this.addTransaction = addTransaction;
    }

    /**
     * Getter for {@link #addTransaction}.
     *
     * @return {@link #addTransaction}
     */
    public boolean isAddTransaction() {
        return addTransaction;
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
     * Setter for {@link #version}.
     *
     * @param version {@link #version}
     */
    public void setVersion(final boolean version) {
        this.version = version;
    }

    /**
     * Getter for {@link #version}.
     *
     * @return {@link #version}
     */
    public boolean isVersion() {
        return version;
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
        final int argsLength;

        if (args.length >= 2) {
            argsLength = args.length - 2;
        } else {
            argsLength = args.length;
        }

        for (int i = 0; i < argsLength; i++) {
            if ("--add-defaults".equals(args[i])) {
                setAddDefaults(true);
            } else if ("--add-transaction".equals(args[i])) {
                setAddTransaction(true);
            } else if ("--ignore-start-with".equals(args[i])) {
                setIgnoreStartWith(true);
            } else if ("--quote-names".equals(args[i])) {
                setQuoteNames(true);
            } else if ("--version".equals(args[i])) {
                setVersion(true);
            } else {
                writer.println("ERROR: Unknown option: " + args[i]);
                success = false;

                break;
            }
        }

        if ((args.length == 1) && isVersion()) {
            printVersion(writer);
            success = false;
        } else if (args.length < 2) {
            printUsage(writer);
            success = false;
        } else if (success) {
            setOldDumpFile(args[args.length - 2]);
            setNewDumpFile(args[args.length - 1]);
        }

        return success;
    }

    /**
     * Prints program usage.
     *
     * @param writer writer to print the usage to
     *
     * @throws RuntimeException Thrown if problem occured while reading usage
     *         info.
     */
    private void printUsage(final PrintWriter writer) {
        final BufferedReader reader =
            new BufferedReader(
                    new InputStreamReader(
                            getClass().getResourceAsStream("usage.txt")));

        try {
            String line = reader.readLine();

            while (line != null) {
                writer.println(line);
                line = reader.readLine();
            }
        } catch (final IOException ex) {
            throw new RuntimeException(
                    "Problem occured while reading usage file",
                    ex);
        } finally {
            try {
                reader.close();
            } catch (final IOException ex) {
                throw new RuntimeException(
                        "Problem occured while closing reader",
                        ex);
            }
        }
    }

    /**
     * Prints program version.
     *
     * @param writer writer to print the usage to
     *
     * @throws RuntimeException Thrown if problem occured while reading program
     *         version.
     */
    private void printVersion(final PrintWriter writer) {
        final BufferedReader reader =
            new BufferedReader(
                    new InputStreamReader(
                            getClass().getResourceAsStream("build_info")));
        writer.print("Version: ");

        try {
            writer.println(reader.readLine());
        } catch (final IOException ex) {
            throw new RuntimeException("Cannot read program version", ex);
        } finally {
            try {
                reader.close();
            } catch (final IOException ex) {
                throw new RuntimeException(
                        "Problem occured while closing reader",
                        ex);
            }
        }
    }
}
