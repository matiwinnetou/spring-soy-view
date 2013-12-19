package pl.matisoft.soy.ajax;

import com.google.common.base.Optional;
import com.google.template.soy.msgs.SoyMsgBundle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pl.matisoft.soy.bundle.SoyMsgBundleResolver;
import pl.matisoft.soy.compile.TofuCompiler;
import pl.matisoft.soy.locale.LocaleProvider;
import pl.matisoft.soy.template.TemplateFilesResolver;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 07/10/2013
 * Time: 21:09
 */
@RunWith(MockitoJUnitRunner.class)
public class SoyAjaxControllerTest {

    @InjectMocks
    private SoyAjaxController soyAjaxController = new SoyAjaxController();
    @Mock
    private TemplateFilesResolver templateFilesResolver;
    @Mock
    private SoyMsgBundleResolver soyMsgBundleResolver;
    @Mock
    private LocaleProvider localeProvider;
    @Mock
    private TofuCompiler tofuCompiler;

    @Before
    public void setUp() throws Exception {
        soyAjaxController.setTemplateFilesResolver(templateFilesResolver);
    }

    @Test
    public void testCompileJs() throws Exception {
        final String templateName = "templates/template1.soy";
        final String jsData = "jsData";

        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(localeProvider.resolveLocale(request)).thenReturn(Optional.<Locale>absent());
        when(soyMsgBundleResolver.resolve(any(Optional.class))).thenReturn(Optional.<SoyMsgBundle>absent());
        final URL url1 = getClass().getClassLoader().getResource(templateName);
        when(templateFilesResolver.resolve(templateName)).thenReturn(Optional.of(url1));
        when(tofuCompiler.compileToJsSrc(url1, null)).thenReturn(Optional.of(jsData));

        final ResponseEntity<String> responseEntity = soyAjaxController.compile("", new String[]{templateName}, null, "true", request);
        Assert.assertEquals("data should be equal", jsData, responseEntity.getBody());
        Assert.assertTrue("http status should be equal", (responseEntity.getStatusCode() == HttpStatus.OK));
    }

    @Test
    @Ignore
    public void testCompileJs2() throws Exception {
        final String templateName1 = "templates/template1.soy";
        final String templateName2 = "templates/template2.soy";
        final String jsData1 = "jsData1";
        final String jsData2 = "jsData2";

        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(localeProvider.resolveLocale(request)).thenReturn(Optional.<Locale>absent());
        when(soyMsgBundleResolver.resolve(any(Optional.class))).thenReturn(Optional.<SoyMsgBundle>absent());
        final URL url1 = getClass().getClassLoader().getResource(templateName1);
        final URL url2 = getClass().getClassLoader().getResource(templateName2);
        when(templateFilesResolver.resolve(templateName1)).thenReturn(Optional.of(url1));
        when(templateFilesResolver.resolve(templateName2)).thenReturn(Optional.of(url2));
        when(tofuCompiler.compileToJsSrc(url1, null)).thenReturn(Optional.of(jsData1));
        when(tofuCompiler.compileToJsSrc(url2, null)).thenReturn(Optional.of(jsData2));

        final ResponseEntity<String> responseEntity = soyAjaxController.compile("", new String[]{templateName1, templateName2}, null, "true", request);
        Assert.assertEquals("data should be equal", jsData1 + jsData2, responseEntity.getBody());
        Assert.assertTrue("http status should be equal", (responseEntity.getStatusCode() == HttpStatus.OK));
    }

}
