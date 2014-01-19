package pl.matisoft.soy.template;

import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: Brandon Zeeb
 */
public class ClasspathTemplateFilesResolverTest {

    private ClasspathTemplateFilesResolver templateFilesResolver = new ClasspathTemplateFilesResolver();

    @Before
    public void setUp() {
        templateFilesResolver.setTemplatesLocation("classpath:templates");
        templateFilesResolver.afterPropertiesSet();
    }

    @Test
    public void defaultDebugFlag() throws Exception {
        Assert.assertFalse("debug flag should be off", templateFilesResolver.isHotReloadMode());
    }

    @Test
    public void setDebugFlag() throws Exception {
        templateFilesResolver.setHotReloadMode(true);
        Assert.assertTrue("debug flag should be on", templateFilesResolver.isHotReloadMode());
    }

    @Test
    public void defaultRecursive() throws Exception {
        Assert.assertTrue("recursive template file resolution should be on", templateFilesResolver.isRecursive());
    }

    @Test
    public void setRecursive() throws Exception {
        templateFilesResolver.setRecursive(false);
        Assert.assertFalse("recursive template file resolution should be off", templateFilesResolver.isRecursive());
    }

    @Test
    public void cacheShouldBeEmpty() throws Exception {
        Assert.assertNull("cache should be null from beginning", templateFilesResolver.cachedFiles);
    }

    @Test
    public void defaultTemplateLocation() throws Exception {
        Assert.assertNull("template file location should be null",
                new DefaultTemplateFilesResolver().getTemplatesLocation());
    }

    @Test
    public void resolveDebugOffRecursiveOn() throws Exception {
        final Collection<URL> urls = templateFilesResolver.resolve();
        Assert.assertEquals("should resolve urls", 5, urls.size());

        Set<String> names = nab_template_names(urls);

        for (final String s : new String[] {"template1", "template2", "template3", "sub/template4", "sub/template5"}) {
            Assert.assertTrue(s + " must be in the set of resolved templates.", names.contains("templates/" + s + ".soy"));
        }
    }

    @Test
    public void resolveDebugOffRecursiveOff() throws Exception {
        templateFilesResolver.setRecursive(false);
        final Collection<URL> urls = templateFilesResolver.resolve();
        Assert.assertEquals("should resolve urls", 3, urls.size());

        Set<String> names = nab_template_names(urls);

        for (final String s : new String[] {"template1", "template2", "template3"}) {
            Assert.assertTrue(s + " must be in the set of resolved templates.", names.contains("templates/" + s + ".soy"));
        }
    }

    @Test
    public void resolveWithFullTemplateNameDebugOff() throws Exception {
        final Optional<URL> url = templateFilesResolver.resolve("templates/template1");
        Assert.assertTrue("should be present", url.isPresent());
        Assert.assertTrue("template1Url file should end with template1.soy", url.get().getFile().endsWith("template1.soy"));
    }

    @Test
    public void resolveWithTemplateNameDebugOff() throws Exception {
        final Optional<URL> url = templateFilesResolver.resolve("template1");
        Assert.assertTrue("should be present", url.isPresent());
        Assert.assertTrue("template1Url file should end with template1.soy", url.get().getFile().endsWith("template1.soy"));
    }

    @Test
    public void resolveWithFullTemplateNameExtDebugOff() throws Exception {
        final Optional<URL> url = templateFilesResolver.resolve("templates/template1.soy");
        Assert.assertTrue("should be present", url.isPresent());
        Assert.assertTrue("template1Url file should end with template1.soy", url.get().getFile().endsWith("template1.soy"));
    }

    @Test
    public void resolveWithTemplateNameExtDebugOff() throws Exception {
        final Optional<URL> url = templateFilesResolver.resolve("template1.soy");
        Assert.assertTrue("should be present", url.isPresent());
        Assert.assertTrue("template1Url file should end with template1.soy", url.get().getFile().endsWith("template1.soy"));
    }

    @Test
    public void resolveWithFullTemplateNameExtDebugOffShouldNotWork() throws Exception {
        final Optional<URL> url = templateFilesResolver.resolve("tmpl/template1.soy");
        Assert.assertFalse("should be absent", url.isPresent());
    }

    @Test
    public void cacheDisabledWithDebugOn() throws Exception {
        templateFilesResolver.setHotReloadMode(true);
        templateFilesResolver.resolve("template1");

        Assert.assertNull("cache should be null", templateFilesResolver.cachedFiles);
    }

    @Test
    public void cacheDisabledByDefault() throws Exception {
        templateFilesResolver.resolve("template1");
        Assert.assertFalse("cache should not be empty", templateFilesResolver.cachedFiles.isEmpty());
    }

    @Test
    public void cacheEnabledWithDebugOff() throws Exception {
        templateFilesResolver.resolve("template1");
        Assert.assertFalse("cache should not be empty", templateFilesResolver.cachedFiles.isEmpty());
        Assert.assertEquals("number of items in cache should be equal", 5, templateFilesResolver.cachedFiles.size());
    }

    private Set<String> nab_template_names(Collection<URL> templates) {
        Set<String> names = new HashSet<String>();

        for (final URL template : templates) {
            final String file_path = template.getFile();
            final String file = file_path.substring(file_path.indexOf("templates"));

            names.add(file);
        }

        return names;
    }

}
