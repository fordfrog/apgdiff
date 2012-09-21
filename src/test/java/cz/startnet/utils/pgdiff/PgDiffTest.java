/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for PgDiff class.
 *
 * @author fordfrog
 */
@RunWith(value = Parameterized.class)
public class PgDiffTest {

    /**
     * Provides parameters for running the tests.
     *
     * @return parameters for the tests
     */
    @Parameters
    public static Collection<?> parameters() {
        return Arrays.asList(
                new Object[][]{
                    // Tests scenario where COLUMN type is modified.
                    {"modify_column_type", false, false, false, false},
                    // Tests scenario where CLUSTER is added to TABLE.
                    {"add_cluster", false, false, false, false},
                    // Tests scenario where CLUSTER is dropped from TABLE.
                    {"drop_cluster", false, false, false, false},
                    // Tests scenario where CLUSTER is changed on TABLE.
                    {"modify_cluster", false, false, false, false},
                    // Tests scenario where WITH OIDS is dropped from TABLE.
                    {"drop_with_oids", false, false, false, false},
                    // Tests scenario where INDEX is added.
                    {"add_index", false, false, false, false},
                    // Tests scenario where INDEX is dropped.
                    {"drop_index", false, false, false, false},
                    // Tests scenario where INDEX that TABLE CLUSTER is based
                    // on is dropped.
                    {"drop_index_with_cluster", false, false, false, false},
                    // Tests scenario where INDEX definition is modified.
                    {"modify_index", false, false, false, false},
                    // Tests scenario where STATISTICS information is added
                    // to COLUMN.
                    {"add_statistics", false, false, false, false},
                    // Tests scenario where STATISTICS information is modified.
                    {"modify_statistics", false, false, false, false},
                    // Tests scenario where STATISTICS information is dropped.
                    {"drop_statistics", false, false, false, false},
                    // Tests scenario where DEFAULT value is set on COLUMN.
                    {"add_default_value", false, false, false, false},
                    // Tests scenario where DEFAULT value is modified.
                    {"modify_default_value", false, false, false, false},
                    // Tests scenario where DEFAULT value is dropped from COLUMN.
                    {"drop_default_value", false, false, false, false},
                    // Tests scenario where NOT NULL constraint is set on COLUMN.
                    {"add_not_null", false, false, false, false},
                    // Tests scenario where NOT NULL constraint is dropped
                    // from COLUMN.
                    {"drop_not_null", false, false, false, false},
                    // Tests scenario where COLUMN is added to TABLE definition.
                    {"add_column", false, false, false, false},
                    // Tests scenario where COLUMN is dropped from TABLE.
                    {"drop_column", false, false, false, false},
                    // Tests scenario where new TABLE is added.
                    {"add_table", false, false, false, false},
                    // Tests scenario where TABLE is dropped.
                    {"drop_table", false, false, false, false},
                    // Tests scenario where TABLE CONSTRAINT is added.
                    {"add_constraint", false, false, false, false},
                    // Tests scenario where TABLE CONSTRAINT is modified.
                    {"modify_constraint", false, false, false, false},
                    // Tests scenario where TABLE CONSTRAINT is dropped.
                    {"drop_constraint", false, false, false, false},
                    // Tests scenario where UNIQUE TABLE CONSTRAINT is added.
                    {"add_unique_constraint", false, false, false, true},
                    // Tests reading of TABLE with INHERITS.
                    {"read_inherits", false, false, false, false},
                    // Tests scenario where TABLE with INHERITS is added.
                    {"add_inherits", false, false, false, false},
                    // Tests scenario where original and new TABLE contain
                    //different INHERITS.
                    {"modify_inherits", false, false, false, false},
                    // Tests scenario where SEQUENCE is added.
                    {"add_sequence", false, false, false, false},
                    // Tests scenario where SEQUENCE is dropped.
                    {"drop_sequence", false, false, false, false},
                    // Tests scenario where INCREMENT BY is modified on SEQUENCE.
                    {"modify_sequence_increment", false, false, false, false},
                    // Tests scenario where START WITH is modified on SEQUENCE
                    // (both with --ignore-start-with turned off and on).
                    {"modify_sequence_start_ignore_off", false, false, false,
                        false
                    },
                    {"modify_sequence_start_ignore_on", false, false, false,
                        true
                    },
                    // Tests scenario where MINVALUE is modified on SEQUENCE
                    // (both setting and unsetting the value).
                    {"modify_sequence_minvalue_set", false, false, false, false},
                    {"modify_sequence_minvalue_unset", false, false, false,
                        false
                    },
                    // Tests scenario where MAXVALUE is modified on SEQUENCE
                    // (both setting and unsetting the value).
                    {"modify_sequence_maxvalue_set", false, false, false, false},
                    {"modify_sequence_maxvalue_unset", false, false, false,
                        false
                    },
                    // Tests scenario where CACHE is modified on SEQUENCE.
                    {"modify_sequence_cache", false, false, false, false},
                    // Tests scenario where CYCLE is modified on SEQUENCE.
                    {"modify_sequence_cycle_on", false, false, false, false},
                    {"modify_sequence_cycle_off", false, false, false, false},
                    // Tests correct finding of function end.
                    {"modify_function_end_detection", false, false, false, false},
                    // Tests scenario where new FUNCTION without args is added.
                    {"add_function_noargs", false, false, false, false},
                    // Tests scenario where FUNCTION without args is dropped.
                    {"drop_function_noargs", false, false, false, false},
                    // Tests scenario where FUNCTION without args is modified.
                    {"modify_function_noargs", false, false, false, false},
                    // Tests scenario where new FUNCTION with args is added.
                    {"add_function_args", false, false, false, false},
                    // Tests scenario where FUNCTION with args is dropped.
                    {"drop_function_args", false, false, false, false},
                    // Tests scenario where FUNCTION with args is modified.
                    {"modify_function_args", false, false, false, false},
                    // Tests scenario where new FUNCTION with args is added.
                    {"add_function_args2", false, false, false, false},
                    // Tests scenario where FUNCTION with args is dropped.
                    {"drop_function_args2", false, false, false, false},
                    // Tests scenario where FUNCTION with args is modified.
                    {"modify_function_args2", false, false, false, false},
                    // Tests scenario where FUNCTION with same name but
                    // different args is added.
                    {"add_function_similar", false, false, false, false},
                    // Tests scenario where FUNCTION with same name but
                    // different args is dropped.
                    {"drop_function_similar", false, false, false, false},
                    // Tests scenario where FUNCTION with same name but
                    // different args is modified.
                    {"modify_function_similar", false, false, false, false},
                    // Tests different whitespace formatting in functions
                    {"function_equal_whitespace", false, false, true, false},
                    // Tests scenario where TRIGGER is added.
                    {"add_trigger", false, false, false, false},
                    // Tests scenario where TRIGGER is dropped.
                    {"drop_trigger", false, false, false, false},
                    // Tests scenario where TRIGGER is modified.
                    {"modify_trigger", false, false, false, false},
                    // Tests scenario where VIEW is added.
                    {"add_view", false, false, false, false},
                    // Tests scenario where VIEW is dropped.
                    {"drop_view", false, false, false, false},
                    // Tests scenario where VIEW is modified.
                    {"modify_view", false, false, false, false},
                    // Tests scenario where --add-defaults is specified.
                    {"add_defaults", true, false, false, false},
                    // Tests scenario where multiple schemas are in the dumps.
                    {"multiple_schemas", false, false, false, false},
                    // Tests scenario where --add-transaction is specified.
                    {"multiple_schemas", false, true, false, false},
                    // Tests dropping view default value
                    {"alter_view_drop_default", false, true, false, false},
                    // Tests adding view default value
                    {"alter_view_add_default", false, true, false, false},
                    // Tests adding of comments
                    {"add_comments", false, true, false, false},
                    // Tests dropping of comments
                    {"drop_comments", false, true, false, false},
                    // Tests altering of comments
                    {"alter_comments", false, true, false, false},
                    // Tests changing view default value
                    {"alter_view_change_default", false, true, false, false},
                    // Tests creation of sequence with bug in MINVALUE value
                    {"add_sequence_bug2100013", false, true, false, false},
                    // Tests view with default value
                    {"view_bug3080388", false, true, false, false},
                    // Tests function arguments beginning with in_
                    {"function_bug3084274", false, true, false, false},
                    // Tests addition of comment when new column has been added
                    {"add_comment_new_column", false, true, false, false},
                    // Tests handling of quoted schemas in search_path
                    {"quoted_schema", false, true, false, false},
                    // Tests adding new column with add defaults turned on
                    {"add_column_add_defaults", true, true, false, false},
                    // Tests adding new sequence that is owned by table
                    {"add_owned_sequence", false, true, false, false},
                    // Tests adding empty table
                    {"add_empty_table", false, false, false, false}
                });
    }
    /**
     * Template name for file names that should be used for the test. Testing
     * method adds _original.sql, _new.sql and _diff.sql to the file name
     * template.
     */
    private final String fileNameTemplate;
    /**
     * Value for the same named command line argument.
     */
    private final boolean addDefaults;
    /**
     * Value for the same named command line argument.
     */
    private final boolean addTransaction;
    /**
     * Value for the same named command line argument.
     */
    private final boolean ignoreFunctionWhitespace;
    /**
     * Value for the same named command line argument.
     */
    private final boolean ignoreStartWith;

    /**
     * Creates a new PgDiffTest object.
     *
     * @param fileNameTemplate         {@link #fileNameTemplate}
     * @param addDefaults              {@link #addDefaults}
     * @param addTransaction           {@link #addTransaction}
     * @param ignoreFunctionWhitespace {@link #ignoreFunctionWhitespace}
     * @param ignoreStartWith          {@link #ignoreStartWith}
     */
    public PgDiffTest(final String fileNameTemplate,
            final boolean addDefaults, final boolean addTransaction,
            final boolean ignoreFunctionWhitespace,
            final boolean ignoreStartWith) {
        super();
        this.fileNameTemplate = fileNameTemplate;
        this.addDefaults = addDefaults;
        this.addTransaction = addTransaction;
        this.ignoreFunctionWhitespace = ignoreFunctionWhitespace;
        this.ignoreStartWith = ignoreStartWith;
        Locale.setDefault(Locale.ENGLISH);
    }

    /**
     * Runs single test on original schema.
     *
     * @throws FileNotFoundException Thrown if expected diff file was not found.
     * @throws IOException           Thrown if problem occurred while reading
     *                               expected diff.
     */
    @Test(timeout = 1000)
    public void runDiffSameOriginal() throws FileNotFoundException, IOException {
        final ByteArrayOutputStream diffInput = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(diffInput, true);
        final PgDiffArguments arguments = new PgDiffArguments();
        PgDiff.createDiff(writer, arguments,
                PgDiffTest.class.getResourceAsStream(
                fileNameTemplate + "_original.sql"),
                PgDiffTest.class.getResourceAsStream(
                fileNameTemplate + "_original.sql"));
        writer.flush();

        Assert.assertEquals("File name template: " + fileNameTemplate,
                "", diffInput.toString().trim());
    }

    /**
     * Runs single test on new schema.
     *
     * @throws FileNotFoundException Thrown if expected diff file was not found.
     * @throws IOException           Thrown if problem occurred while reading
     *                               expected diff.
     */
    @Test(timeout = 1000)
    public void runDiffSameNew() throws FileNotFoundException, IOException {
        final ByteArrayOutputStream diffInput = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(diffInput, true);
        final PgDiffArguments arguments = new PgDiffArguments();
        PgDiff.createDiff(writer, arguments,
                PgDiffTest.class.getResourceAsStream(
                fileNameTemplate + "_new.sql"),
                PgDiffTest.class.getResourceAsStream(
                fileNameTemplate + "_new.sql"));
        writer.flush();

        Assert.assertEquals("File name template: " + fileNameTemplate,
                "", diffInput.toString().trim());
    }

    /**
     * Runs single test using class member variables.
     *
     * @throws FileNotFoundException Thrown if expected diff file was not found.
     * @throws IOException           Thrown if problem occurred while reading
     *                               expected diff.
     */
    @Test(timeout = 1000)
    public void runDiff() throws FileNotFoundException, IOException {
        final ByteArrayOutputStream diffInput = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(diffInput, true);
        final PgDiffArguments arguments = new PgDiffArguments();
        arguments.setAddDefaults(addDefaults);
        arguments.setIgnoreFunctionWhitespace(ignoreFunctionWhitespace);
        arguments.setIgnoreStartWith(ignoreStartWith);
        PgDiff.createDiff(writer, arguments,
                PgDiffTest.class.getResourceAsStream(
                fileNameTemplate + "_original.sql"),
                PgDiffTest.class.getResourceAsStream(
                fileNameTemplate + "_new.sql"));
        writer.flush();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                PgDiffTest.class.getResourceAsStream(
                fileNameTemplate + "_diff.sql")));
        final char[] part = new char[1024];
        final StringBuilder sbExpDiff = new StringBuilder(1024);

        while (reader.read(part) != -1) {
            sbExpDiff.append(part);
        }

        reader.close();

        Assert.assertEquals("File name template: " + fileNameTemplate,
                sbExpDiff.toString().trim(),
                diffInput.toString().trim());
    }
}
