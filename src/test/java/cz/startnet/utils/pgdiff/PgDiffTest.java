/*
 * $Id$
 */
package cz.startnet.utils.pgdiff;

import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.util.Arrays;
import java.util.Collection;


/**
 * Tests for PgDiff class.
 *
 * @author fordfrog
 * @version $Id$
 */
@RunWith(value = Parameterized.class)
public class PgDiffTest {
    /**
     * Template name for file names that should be used for the test.
     * Testing method adds _original.sql, _new.sql and _diff.sql to the file
     * name template.
     */
    private final String fileNameTemplate;

    /**
     * Value for the same named command line argument.
     */
    private final boolean ignoreStartWith;

    /**
     * Creates a new PgDiffTest object.
     *
     * @param fileNameTemplate {@link #originalFileName originalFileName}
     * @param ignoreStartWith {@link #ignoreStartWith ignoreStartWith}
     */
    public PgDiffTest(
        final String fileNameTemplate,
        final boolean ignoreStartWith) {
        super();
        this.fileNameTemplate = fileNameTemplate;
        this.ignoreStartWith = ignoreStartWith;
    }

    /**
     * Provides parameters for running the tests.
     *
     * @return parameters for the tests
     */
    @Parameters
    public static Collection parameters() {
        return Arrays.asList(
                new Object[][] {
                    // Tests scenario where COLUMN type is modified.
                    {"modify_column_type", false },
                    // Tests scenario where CLUSTER is added to TABLE.
                    {"add_cluster", false },
                    // Tests scenario where CLUSTER is dropped from TABLE.
                    {"drop_cluster", false },
                    // Tests scenario where CLUSTER is changed on TABLE.
                    {"modify_cluster", false },
                    // Tests scenario where WITH OIDS is dropped from TABLE.
                    {"drop_with_oids", false },
                    // Tests scenario where INDEX is added.
                    {"add_index", false },
                    // Tests scenario where INDEX is dropped.
                    {"drop_index", false },
                    // Tests scenario where INDEX that TABLE CLUSTER is based
                // on is dropped.
                    {"drop_index_with_cluster", false },
                    // Tests scenario where INDEX definition is modified.
                    {"modify_index", false },
                    // Tests scenario where STATISTICS information is added
                // to COLUMN.
                    {"add_statistics", false },
                    // Tests scenario where STATISTICS information is modified.
                    {"modify_statistics", false },
                    // Tests scenario where STATISTICS information is dropped.
                    {"drop_statistics", false },
                    // Tests scenario where DEFAULT value is set on COLUMN.
                    {"add_default_value", false },
                    // Tests scenario where DEFAULT value is modified.
                    {"modify_default_value", false },
                    // Tests scenario where DEFAULT value is dropped from COLUMN.
                    {"drop_default_value", false },
                    // Tests scenario where NOT NULL constraint is set
                // on COLUMN.
                    {"add_not_null", false },
                    // Tests scenario where NOT NULL constraint is dropped
                // from COLUMN.
                    {"drop_not_null", false },
                    // Tests scenario where COLUMN is added to TABLE definition.
                    {"add_column", false },
                    // Tests scenario where COLUMN is dropped from TABLE.
                    {"drop_column", false },
                    // Tests scenario where new TABLE is added.
                    {"add_table", false },
                    // Tests scenario where TABLE is dropped.
                    {"drop_table", false },
                    // Tests scenario where TABLE CONSTRAINT is added.
                    {"add_constraint", false },
                    // Tests scenario where TABLE CONSTRAINT is modified.
                    {"modify_constraint", false },
                    // Tests scenario where TABLE CONSTRAINT is dropped.
                    {"drop_constraint", false },
                    // Tests scenario where UNIQUE TABLE CONSTRAINT is added.
                    {"add_unique_constraint", true },
                    // Tests reading of TABLE with INHERITS.
                    {"read_inherits", false },
                    // Tests scenario where TABLE with INHERITS is added.
                    {"add_inherits", false },
                    // Tests scenario where original and new TABLE contain
                //different INHERITS.
                    {"modify_inherits", false },
                    // Tests scenario where SEQUENCE is added.
                    {"add_sequence", false },
                    // Tests scenario where SEQUENCE is dropped.
                    {"drop_sequence", false },
                    // Tests scenario where INCREMENT BY is modified on SEQUENCE.
                    {"modify_sequence_increment", false },
                    // Tests scenario where START WITH is modified on SEQUENCE
                // (both with --ignore-start-with turned off and on).
                    {"modify_sequence_start_ignore_off", false },
                    { "modify_sequence_start_ignore_on", true },
                    // Tests scenario where MINVALUE is modified on SEQUENCE
                // (both setting and unsetting the value).
                    {"modify_sequence_minvalue_set", false },
                    { "modify_sequence_minvalue_unset", false },
                    // Tests scenario where MAXVALUE is modified on SEQUENCE
                // (both setting and unsetting the value).
                    {"modify_sequence_maxvalue_set", false },
                    { "modify_sequence_maxvalue_unset", false },
                    // Tests scenario where CACHE is modified on SEQUENCE.
                    {"modify_sequence_cache", false },
                    // Tests scenario where CYCLE is modified on SEQUENCE.
                    {"modify_sequence_cycle_on", false },
                    { "modify_sequence_cycle_off", false },
                    // Tests scenario where new FUNCTION without args is added.
                    {"add_function_noargs", false },
                    // Tests scenario where FUNCTION without args is dropped.
                    {"drop_function_noargs", false },
                    // Tests scenario where FUNCTION without args is modified.
                    {"modify_function_noargs", false },
                    // Tests scenario where new FUNCTION with args is added.
                    {"add_function_args", false },
                    // Tests scenario where FUNCTION with args is dropped.
                    {"drop_function_args", false },
                    // Tests scenario where FUNCTION with args is modified.
                    {"modify_function_args", false },
                    // Tests scenario where new FUNCTION with args is added.
                    {"add_function_args2", false },
                    // Tests scenario where FUNCTION with args is dropped.
                    {"drop_function_args2", false },
                    // Tests scenario where FUNCTION with args is modified.
                    {"modify_function_args2", false },
                    // Tests scenario where FUNCTION with same name but
                // different args is added.
                    {"add_function_similar", false },
                    // Tests scenario where FUNCTION with same name but
                // different args is dropped.
                    {"drop_function_similar", false },
                    // Tests scenario where FUNCTION with same name but
                // different args is modified.
                    {"modify_function_similar", false },
                    // Tests scenario where TRIGGER is added.
                    {"add_trigger", false },
                    // Tests scenario where TRIGGER is dropped.
                    {"drop_trigger", false },
                    // Tests scenario where TRIGGER is modified.
                    {"modify_trigger", false }
                });
    }

    /**
     * Runs single test using class member variables.
     *
     * @throws FileNotFoundException Thrown if expected diff file was not
     *         found.
     * @throws IOException Thrown if problem occured while reading expected
     *         diff.
     */
    @Test(timeout = 1000)
    public void runDiff() throws FileNotFoundException, IOException {
        final ByteArrayOutputStream diffInput = new ByteArrayOutputStream();
        final PrintWriter writer = new PrintWriter(diffInput, true);
        final PgDiffArguments arguments = new PgDiffArguments();
        arguments.setIgnoreStartWith(ignoreStartWith);
        PgDiff.createDiff(
                writer,
                arguments,
                PgDiffTest.class.getResourceAsStream(
                        fileNameTemplate + "_original.sql"),
                PgDiffTest.class.getResourceAsStream(
                        fileNameTemplate + "_new.sql"));
        writer.flush();

        final BufferedReader reader =
            new BufferedReader(
                    new InputStreamReader(
                            PgDiffTest.class.getResourceAsStream(
                                    fileNameTemplate + "_diff.sql")));
        final char[] part = new char[1024];
        final StringBuilder sbExpDiff = new StringBuilder();

        while (reader.read(part) != -1) {
            sbExpDiff.append(part);
        }

        reader.close();

        Assert.assertEquals(
                sbExpDiff.toString().trim(),
                diffInput.toString().trim());
    }
}
