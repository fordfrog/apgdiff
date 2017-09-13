/**
 * Copyright 2006 StartNet s.r.o.
 *
 * Distributed under MIT license
 */
package cz.startnet.utils.pgdiff.loader;

import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests for PgDiffLoader class.
 *
 * @author fordfrog
 */
@RunWith(value = Parameterized.class)
public class PgDumpLoaderTest {

    /**
     * Provides parameters for running the tests.
     *
     * @return parameters for the tests
     */
    @Parameters
    public static Collection<?> parameters() {
        return Arrays.asList(
                new Object[][]{
                    {1},
                    {2},
                    {3},
                    {4},
                    {5},
                    {6},
                    {7},
                    {8},
                    {9},
                    {10},
                    {11},
                    {12},
                    {13},
                    {14},
                    {15}
                });
    }
    /**
     * Index of the file that should be tested.
     */
    private final int fileIndex;

    /**
     * Creates a new instance of PgDumpLoaderTest.
     *
     * @param fileIndex {@link #fileIndex}
     */
    public PgDumpLoaderTest(final int fileIndex) {
        this.fileIndex = fileIndex;
    }

    /**
     * Runs single test.
     */
    @Test(timeout = 1000)
    public void loadSchema() {
        PgDumpLoader.loadDatabaseSchema(
                getClass().getResourceAsStream("schema_" + fileIndex + ".sql"),
                "UTF-8", false, false);
    }
}
