/*
 * $CVSHeader$
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
 * @version $CVSHeader$
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
     * Creates a new PgDiffTest object.
     *
     * @param fileNameTemplate {@link #originalFileName originalFileName}
     */
    public PgDiffTest(final String fileNameTemplate) {
        super();
        this.fileNameTemplate = fileNameTemplate;
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
                    {"modify_column_type" },
                    // Tests scenario where CLUSTER is added to TABLE.
                    {"add_cluster" },
                    // Tests scenario where CLUSTER is dropped from TABLE.
                    {"drop_cluster" },
                    // Tests scenario where CLUSTER is changed on TABLE.
                    {"modify_cluster" },
                    // Tests scenario where INDEX is added.
                    {"add_index" },
                    // Tests scenario where INDEX is dropped.
                    {"drop_index" },
                    // Tests scenario where INDEX that TABLE CLUSTER is based
                // on is dropped.
                    {"drop_index_with_cluster" },
                    // Tests scenario where INDEX definition is modified.
                    {"modify_index" },
                    // Tests scenario where SEQUENCE is added.
                    {"add_sequence" },
                    // Tests scenario where SEQUENCE is modified.
                    {"modify_sequence" },
                    // Tests scenario where SEQUENCE is dropped.
                    {"drop_sequence" },
                    // Tests scenario where STATISTICS information is added
                // to COLUMN.
                    {"add_statistics" },
                    // Tests scenario where STATISTICS information is modified.
                    {"modify_statistics" },
                    // Tests scenario where STATISTICS information is dropped.
                    {"drop_statistics" },
                    // Tests scenario where DEFAULT value is set on COLUMN.
                    {"add_default_value" },
                    // Tests scenario where DEFAULT value is modified.
                    {"modify_default_value" },
                    // Tests scenario where DEFAULT value is dropped from COLUMN.
                    {"drop_default_value" },
                    // Tests scenario where NOT NULL constraint is set
                // on COLUMN.
                    {"add_not_null" },
                    // Tests scenario where NOT NULL constraint is dropped
                // from COLUMN.
                    {"drop_not_null" },
                    // Tests scenario where COLUMN is added to TABLE definition.
                    {"add_column" },
                    // Tests scenario where COLUMN is dropped from TABLE.
                    {"drop_column" },
                    // Tests scenario where new TABLE is added.
                    {"add_table" },
                    // Tests scenario where TABLE is dropped.
                    {"drop_table" },
                    // Tests scenario where TABLE CONSTRAINT is added.
                    {"add_constraint" },
                    // Tests scenario where TABLE CONSTRAINT is modified.
                    {"modify_constraint" },
                    // Tests scenario where TABLE CONSTRAINT is dropped.
                    {"drop_constraint" },
                    // Tests reading of TABLE with INHERITS.
                    {"read_inherits" },
                    // Tests scenario where TABLE with INHERITS is added.
                    {"add_inherits" },
                    // Tests scenario where original and new TABLE contain
                //different INHERITS.
                    {"modify_inherits" }
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
        PgDiff.createDiff(
                writer,
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
