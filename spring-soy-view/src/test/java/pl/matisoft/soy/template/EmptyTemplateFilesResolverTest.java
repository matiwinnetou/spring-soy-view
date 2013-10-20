package pl.matisoft.soy.template;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 18.10.13
 * Time: 19:09
 */
public class EmptyTemplateFilesResolverTest {

    private EmptyTemplateFilesResolver  emptyTemplateFilesResolver = new EmptyTemplateFilesResolver();

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void nonNull() throws Exception {
        Assert.assertNotNull("should not be null", emptyTemplateFilesResolver.resolve());
    }

    @Test
    public void testResolveMany() throws Exception {
        Assert.assertTrue("should be empty", emptyTemplateFilesResolver.resolve().isEmpty());
    }

    @Test
    public void testResolveOne() throws Exception {
        Assert.assertFalse("should be absent", emptyTemplateFilesResolver.resolve("template").isPresent());
    }

}
