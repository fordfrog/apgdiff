/**
 * Copyright 2010 StartNet s.r.o.
 */
package cz.startnet.utils.pgdiff.parsers;

import cz.startnet.utils.pgdiff.schema.PgDatabase;
import cz.startnet.utils.pgdiff.schema.PgSchema;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link #ParserUtils}.
 *
 * @author fordfrog
 */
public class ParserUtilsTest {

    @Test(timeout = 1000)
    public void testParseSchemaBothQuoted() {
        final PgDatabase database = new PgDatabase();
        final PgSchema schema = new PgSchema("juzz_system");
        database.addSchema(schema);

        Assert.assertThat(ParserUtils.getSchemaName(
                "\"juzz_system\".\"f_obj_execute_node_select\"", database),
                IsEqual.equalTo("juzz_system"));
    }

    @Test(timeout = 1000)
    public void testParseSchemaFirstQuoted() {
        final PgDatabase database = new PgDatabase();
        final PgSchema schema = new PgSchema("juzz_system");
        database.addSchema(schema);

        Assert.assertThat(ParserUtils.getSchemaName(
                "\"juzz_system\".f_obj_execute_node_select", database),
                IsEqual.equalTo("juzz_system"));
    }

    @Test(timeout = 1000)
    public void testParseSchemaSecondQuoted() {
        final PgDatabase database = new PgDatabase();
        final PgSchema schema = new PgSchema("juzz_system");
        database.addSchema(schema);

        Assert.assertThat(ParserUtils.getSchemaName(
                "juzz_system.\"f_obj_execute_node_select\"", database),
                IsEqual.equalTo("juzz_system"));
    }

    @Test(timeout = 1000)
    public void testParseSchemaNoneQuoted() {
        final PgDatabase database = new PgDatabase();
        final PgSchema schema = new PgSchema("juzz_system");
        database.addSchema(schema);

        Assert.assertThat(ParserUtils.getSchemaName(
                "juzz_system.f_obj_execute_node_select", database),
                IsEqual.equalTo("juzz_system"));
    }

    @Test(timeout = 1000)
    public void testParseSchemaThreeQuoted() {
        final PgDatabase database = new PgDatabase();
        final PgSchema schema = new PgSchema("juzz_system");
        database.addSchema(schema);

        Assert.assertThat(ParserUtils.getSchemaName(
                "\"juzz_system\".\"f_obj_execute_node_select\".\"test\"",
                database), IsEqual.equalTo("juzz_system"));
    }

    @Test(timeout = 1000)
    public void testParseObjectBothQuoted() {
        Assert.assertThat(ParserUtils.getObjectName(
                "\"juzz_system\".\"f_obj_execute_node_select\""),
                IsEqual.equalTo("f_obj_execute_node_select"));
    }

    @Test(timeout = 1000)
    public void testParseObjectFirstQuoted() {
        Assert.assertThat(ParserUtils.getObjectName(
                "\"juzz_system\".f_obj_execute_node_select"),
                IsEqual.equalTo("f_obj_execute_node_select"));
    }

    @Test(timeout = 1000)
    public void testParseObjectSecondQuoted() {
        Assert.assertThat(ParserUtils.getObjectName(
                "juzz_system.\"f_obj_execute_node_select\""),
                IsEqual.equalTo("f_obj_execute_node_select"));
    }

    @Test(timeout = 1000)
    public void testParseObjectNoneQuoted() {
        Assert.assertThat(ParserUtils.getObjectName(
                "juzz_system.f_obj_execute_node_select"),
                IsEqual.equalTo("f_obj_execute_node_select"));
    }

    @Test(timeout = 1000)
    public void testParseObjectThreeQuoted() {
        Assert.assertThat(ParserUtils.getObjectName(
                "\"juzz_system\".\"f_obj_execute_node_select\".\"test\""),
                IsEqual.equalTo("test"));
    }
}
