package pl.matisoft.soy.template;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 17.10.13
 * Time: 23:35
 */
public class DefaultTemplateFilesResolverTest {

    private DefaultTemplateFilesResolver defaultTemplateFilesResolver = new DefaultTemplateFilesResolver();

    @Test
    public void defaultDebugFlag() throws Exception {
        Assert.assertFalse("debug flag should be off", defaultTemplateFilesResolver.isDebugOn());
    }

    @Test
    public void defaultRecursive() throws Exception {
        Assert.assertTrue("recursive template file resolution should be on", defaultTemplateFilesResolver.isRecursive());
    }

    @Test
    public void defaultTemplateLocation() throws Exception {
        Assert.assertNotNull("template file location should have a default", defaultTemplateFilesResolver.getTemplatesLocation());
        Assert.assertEquals("template file location should be 'templates'", "templates", defaultTemplateFilesResolver.getTemplatesLocation().getFilename());
        Assert.assertTrue("template file location should be 'ClassPathResource'", defaultTemplateFilesResolver.getTemplatesLocation() instanceof ClassPathResource);
    }

    @Test
    public void resolveDebugOff() throws Exception {
        final Collection<URL> urls = defaultTemplateFilesResolver.resolve();
        Assert.assertEquals("should resolve two urls", 2, urls.size());
        final Iterator<URL> it = urls.iterator();
        final URL template1Url = it.next();
        final URL template2Url = it.next();
        Assert.assertTrue("template1Url file should end with template1.soy", template1Url.getFile().endsWith("template1.soy"));
        Assert.assertTrue("template2Url file should end with template2.soy", template2Url.getFile().endsWith("template2.soy"));
    }

    @Test
    public void resolveWithTemplateNameDebugOff() throws Exception {
        final Optional<URL> url = defaultTemplateFilesResolver.resolve("template1");
        Assert.assertTrue("should not be absent", url.isPresent());
        Assert.assertTrue("template1Url file should end with template1.soy", url.get().getFile().endsWith("template1.soy"));

    }

}
