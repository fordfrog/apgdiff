/*
 * $Id$
 */
package cz.startnet.utils.pgdiff.parsers;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link ParserUtils}.
 *
 * @author fordfrog
 * @version $Id$
 */
public class ParserUtilsTest {

    /**
     * Tests getting of command length if more single quotes and commas are
     * present.
     */
    @Test(timeout = 1000)
    public void getCommandEndSingleQuotes() {
        final int result =
                ParserUtils.getCommandEnd(
                "CREATE TABLE user_preferences (exboxes text " +
                "DEFAULT '''test1'',''test2'',''test3'',''test4'''::text )",
                31);
        Assert.assertThat(result, IsEqual.equalTo(100));
    }
}
