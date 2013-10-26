package pl.matisoft.soy.ajax;

import org.junit.Test;
import pl.matisoft.soy.ajax.utils.PathUtils;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 26/10/2013
 * Time: 11:48
 */
public class PathUtilsTest {

    @Test
    public void testArrayToPathNullCheck() throws Exception {
        final String out = PathUtils.arrayToPath(null);
        assertEquals("", out);
    }

    @Test
    public void testArrayToPath() throws Exception {
        final String[] array = new String[]{"abc.soy", "abc"};
        final String out = PathUtils.arrayToPath(array);
        assertEquals("abc.soy,abc", out);
    }

    @Test
    public void testArrayToPath2() throws Exception {
        final String[] array = new String[]{"templates/abc1.soy", "templates/abc2.soy"};
        final String out = PathUtils.arrayToPath(array);
        assertEquals("templates/abc1.soy,templates/abc2.soy", out);
    }

}
