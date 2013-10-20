package pl.matisoft.soy.global.compile;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 20.10.13
 * Time: 20:45
 */
public class EmptyCompileTimeGlobalModelResolverTest {

    private EmptyCompileTimeGlobalModelResolver defaultCompileTimeGlobalModelResolver = new EmptyCompileTimeGlobalModelResolver();

    @Test
    public void notNull() throws Exception {
        Assert.assertNotNull("not null", defaultCompileTimeGlobalModelResolver.resolveData());
    }

    @Test
    public void absent() throws Exception {
        Assert.assertFalse("should be absent", defaultCompileTimeGlobalModelResolver.resolveData().isPresent());
    }

}
