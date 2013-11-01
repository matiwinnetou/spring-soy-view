//package pl.matisoft.soy.ajax.url;
//
//import com.google.common.base.Optional;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.InjectMocks;
//import org.mockito.MockitoAnnotations;
//import pl.matisoft.soy.ajax.hash.HashFileGenerator;
//import pl.matisoft.soy.template.TemplateFilesResolver;
//
//import java.net.URL;
//
//import static org.mockito.Matchers.anyCollection;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
///**
// * Created with IntelliJ IDEA.
// * User: mati
// * Date: 21/10/2013
// * Time: 20:12
// */
//public class DefaultTemplateUrlComposerTest {
//
//    @InjectMocks
//    private DefaultTemplateUrlComposer defaultTemplateUrlComposer = new DefaultTemplateUrlComposer();
//
//    @Before
//    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    public void defaultSiteUrl() throws Exception {
//        Assert.assertNotNull("should not be null", defaultTemplateUrlComposer.getSiteUrl());
//    }
//
//    @Test
//    public void defaultNotNull() throws Exception {
//        Assert.assertNotNull("should not be null", defaultTemplateUrlComposer.getSiteUrl());
//    }
//
//    @Test
//    public void composeReturnsNotNull() throws Exception {
//        Assert.assertNotNull("should not return null", defaultTemplateUrlComposer.compose("templates/template1.soy"));
//    }
//
//    @Test
//    public void composeReturnsAbsentByDefault() throws Exception {
//        Assert.assertFalse("should return absent to", defaultTemplateUrlComposer.compose("templates/template1.soy").isPresent());
//    }
//
//    @Test
//    public void composeShouldCompose() throws Exception {
//        final String url = "templates/template1.soy";
//        final HashFileGenerator hashFileGenerator = mock(HashFileGenerator.class);
//        final TemplateFilesResolver templateFilesResolver = mock(TemplateFilesResolver.class);
//        final URL urlURL = new URL("file://");
//        defaultTemplateUrlComposer.setHashFileGenerator(hashFileGenerator);
//        defaultTemplateUrlComposer.setTemplateFilesResolver(templateFilesResolver);
//
//        when(templateFilesResolver.resolve(url)).thenReturn(Optional.of(urlURL));
//        when(hashFileGenerator.hash(Optional.<URL>of(urlURL))).thenReturn(Optional.of("md5"));
//        when(hashFileGenerator.hashMulti(anyCollection())).thenReturn(Optional.of("md5"));
//
//        Assert.assertTrue("should return value", defaultTemplateUrlComposer.compose(url).isPresent());
//        Assert.assertEquals("should equal to value", "/soy/compileJs?hash=md5&file=templates/template1.soy", defaultTemplateUrlComposer.compose(url).get());
//    }
//
//    @Test
//    public void composeShouldComposeWithSiteUrl() throws Exception {
//        final String url = "templates/template1.soy";
//        final HashFileGenerator hashFileGenerator = mock(HashFileGenerator.class);
//        final TemplateFilesResolver templateFilesResolver = mock(TemplateFilesResolver.class);
//        final URL urlURL = new URL("file://");
//        defaultTemplateUrlComposer.setHashFileGenerator(hashFileGenerator);
//        defaultTemplateUrlComposer.setTemplateFilesResolver(templateFilesResolver);
//        defaultTemplateUrlComposer.setSiteUrl("http://www.somesiteurl.com");
//
//        when(templateFilesResolver.resolve(url)).thenReturn(Optional.of(urlURL));
//        when(hashFileGenerator.hash(Optional.<URL>of(urlURL))).thenReturn(Optional.of("md5"));
//        when(hashFileGenerator.hashMulti(anyCollection())).thenReturn(Optional.of("md5"));
//
//        Assert.assertTrue("should return value", defaultTemplateUrlComposer.compose(url).isPresent());
//        Assert.assertEquals("should equal to value", "http://www.somesiteurl.com/soy/compileJs?hash=md5&file=templates/template1.soy", defaultTemplateUrlComposer.compose(url).get());
//    }
//
//}
