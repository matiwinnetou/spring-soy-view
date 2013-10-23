package pl.matisoft.soy.data.adjust;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 23.10.13
 * Time: 10:09
 */
public class EmptyModelAdjusterTest {

    private EmptyModelAdjuster emptyModelAdjuster = new EmptyModelAdjuster();

    @Test
    public void nullReturnsNull() throws Exception {
        Assert.assertNull("null returns null", emptyModelAdjuster.adjust(null));
    }

    @Test
    public void sameObject() throws Exception {
        final Object obj = new Object();
        Assert.assertEquals("return the same object", obj, emptyModelAdjuster.adjust(obj));
    }

}
