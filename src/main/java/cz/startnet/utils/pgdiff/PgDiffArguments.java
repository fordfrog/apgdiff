/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.SortedMap;

/**
 * Contains parsed command line arguments.
 *
 * @author fordfrog
 */
public class PgDiffArguments {

    /**
     * Input file charset name.
     */
    private String inCharsetName = "UTF-8";
    /**
     * Path to the new dump file.
     */
    private String newDumpFile;
    /**
     * Path to the original dump file.
     */
    private String oldDumpFile;
    /**
     * Output file charset name.
     */
    private String outCharsetName = "UTF-8";
    /**
     * Whether DEFAULT ... should be added in case new column has NOT NULL
     * constraint. The default value is dropped later.
     */
    private boolean addDefaults;
    /**
     * Whether to enclose all statements in transaction.
     */
    private boolean addTransaction;
    /**
     * Whether to ignore whitespace while comparing content of functions.
     */
    private boolean ignoreFunctionWhitespace;
    /**
     * Whether to ignore START WITH on SEQUENCEs.
     */
    private boolean ignoreStartWith;
    /**
     * Whether to display apgdiff version.
     */
    private boolean version;
    /**
     * Whether to output information about ignored statements.
     */
    private boolean outputIgnoredStatements;
    /**
     * Whether to list supported charsets.
     */
    private boolean listCharsets;
    /**
     * Whether Slony triggers should be ignored.
     */
    private boolean ignoreSlonyTriggers;

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
     * Setter for {@link #ignoreFunctionWhitespace}.
     *
     * @param ignoreFunctionWhitespace {@link #ignoreFunctionWhitespace}
     */
    public void setIgnoreFunctionWhitespace(
            final boolean ignoreFunctionWhitespace) {
        this.ignoreFunctionWhitespace = ignoreFunctionWhitespace;
    }

    /**
     * Getter for {@link #ignoreFunctionWhitespace}.
     *
     * @return {@link #ignoreFunctionWhitespace}
     */
    public boolean isIgnoreFunctionWhitespace() {
        return ignoreFunctionWhitespace;
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
     * Getter for {@link #outputIgnoredStatements}.
     *
     * @return {@link #outputIgnoredStatements}
     */
    public boolean isOutputIgnoredStatements() {
        return outputIgnoredStatements;
    }

    /**
     * Setter for {@link #outputIgnoredStatements}.
     *
     * @param outputIgnoredStatements {@link #outputIgnoredStatements}
     */
    public void setOutputIgnoredStatements(
            final boolean outputIgnoredStatements) {
        this.outputIgnoredStatements = outputIgnoredStatements;
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
     * @param args   array of arguments
     *
     * @return true if arguments were parsed and execution can continue,
     *         otherwise false
     */
    @SuppressWarnings("AssignmentToForLoopParameter")
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
            } else if ("--ignore-function-whitespace".equals(args[i])) {
                setIgnoreFunctionWhitespace(true);
            } else if ("--ignore-slony-triggers".equals(args[i])) {
                setIgnoreSlonyTriggers(true);
            } else if ("--ignore-start-with".equals(args[i])) {
                setIgnoreStartWith(true);
            } else if ("--in-charset-name".equals(args[i])) {
                setInCharsetName(args[i + 1]);
                i++;
            } else if ("--list-charsets".equals(args[i])) {
                setListCharsets(true);
            } else if ("--out-charset-name".equals(args[i])) {
                setOutCharsetName(args[i + 1]);
                i++;
            } else if ("--output-ignored-statements".equals(args[i])) {
                setOutputIgnoredStatements(true);
            } else if ("--version".equals(args[i])) {
                setVersion(true);
            } else {
                writer.print(Resources.getString("ErrorUnknownOption"));
                writer.print(": ");
                writer.println(args[i]);
                success = false;

                break;
            }
        }

        if (args.length == 1 && isVersion()) {
            printVersion(writer);
            success = false;
        } else if (args.length == 1 && isListCharsets()) {
            listCharsets(writer);
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
     */
    private void printUsage(final PrintWriter writer) {
        writer.println(
                Resources.getString("UsageHelp").replace("${tab}", "\t"));
    }

    /**
     * Prints program version.
     *
     * @param writer writer to print the usage to
     */
    private void printVersion(final PrintWriter writer) {
        writer.print(Resources.getString("Version"));
        writer.print(": ");
        writer.println(Resources.getString("VersionNumber"));
    }

    /**
     * Getter for {@link #inCharsetName}.
     *
     * @return {@link #inCharsetName}
     */
    public String getInCharsetName() {
        return inCharsetName;
    }

    /**
     * Setter for {@link #inCharsetName}.
     *
     * @param inCharsetName {@link #inCharsetName}
     */
    public void setInCharsetName(final String inCharsetName) {
        this.inCharsetName = inCharsetName;
    }

    /**
     * Getter for {@link #outCharsetName}.
     *
     * @return {@link #outCharsetName}
     */
    public String getOutCharsetName() {
        return outCharsetName;
    }

    /**
     * Setter for {@link #outCharsetName}.
     *
     * @param outCharsetName {@link #outCharsetName}
     */
    public void setOutCharsetName(final String outCharsetName) {
        this.outCharsetName = outCharsetName;
    }

    /**
     * Getter for {@link #listCharsets}.
     *
     * @return {@link #listCharsets}
     */
    public boolean isListCharsets() {
        return listCharsets;
    }

    /**
     * Setter for {@link #listCharsets}.
     *
     * @param listCharsets {@link #listCharsets}
     */
    public void setListCharsets(final boolean listCharsets) {
        this.listCharsets = listCharsets;
    }

    /**
     * Lists supported charsets.
     *
     * @param writer writer
     */
    private void listCharsets(final PrintWriter writer) {
        final SortedMap<String, Charset> charsets = Charset.availableCharsets();

        for (final String name : charsets.keySet()) {
            writer.println(name);
        }
    }

    /**
     * Getter for {@link #ignoreSlonyTriggers}.
     *
     * @return {@link #ignoreSlonyTriggers}
     */
    public boolean isIgnoreSlonyTriggers() {
        return ignoreSlonyTriggers;
    }

    /**
     * Setter for {@link #ignoreSlonyTriggers}.
     *
     * @param ignoreSlonyTriggers {@link #ignoreSlonyTriggers}
     */
    public void setIgnoreSlonyTriggers(final boolean ignoreSlonyTriggers) {
        this.ignoreSlonyTriggers = ignoreSlonyTriggers;
    }
}
