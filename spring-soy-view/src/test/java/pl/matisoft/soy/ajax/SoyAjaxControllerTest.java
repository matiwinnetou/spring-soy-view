package pl.matisoft.soy.ajax;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;

import com.google.common.base.Optional;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpClientErrorException;
import pl.matisoft.soy.template.TemplateFilesResolver;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 07/10/2013
 * Time: 21:09
 */
public class SoyAjaxControllerTest {

    @InjectMocks
    private SoyAjaxController soyAjaxController = new SoyAjaxController();

    @Mock
    private TemplateFilesResolver templateFilesResolver;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        soyAjaxController.setTemplateFilesResolver(templateFilesResolver);
    }

    @Test
    @Ignore
    public void testCompileJsFailWithNotFound() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(templateFilesResolver.resolve("template1")).thenReturn(Optional.<URL>absent());
        try {
            soyAjaxController.getJsForTemplateFiles(new String[]{"templates/template1.soy"}, "false", request);
            fail("should throw exception");
        } catch(HttpClientErrorException ex) {
        }
    }

//    @Test
//    public void testCompileJs() throws Exception {
//        final HttpServletRequest request = mock(HttpServletRequest.class);
//        final URL url1 = getClass().getClassLoader().getResource("template1.soy");
//        when(templateFilesResolver.resolve(anyString())).thenReturn(Optional.of(url1));
//        soyAjaxController.getJsForTemplateFiles(new String[]{"template1.soy"}, "true", request);
//    }

    @Test
    public void testStripExtensions() throws Exception {
        final String[] arr = new String[] {"abc.soy", "abc"};
        final String[] newArr = soyAjaxController.stripExtensions(arr);

        assertEquals(2, newArr.length);
        assertEquals("abc", newArr[0]);
        assertEquals("abc", newArr[1]);
    }

    @Test
    public void testStripExtensionsNull() throws Exception {
        final String[] newArr = soyAjaxController.stripExtensions(null);
        assertEquals(0, newArr.length);
    }

    @Test
    public void testArrayToPathNullCheck() throws Exception {
        final String out = soyAjaxController.arrayToPath(null);
        assertEquals("", out);
    }

    @Test
    public void testArrayToPath() throws Exception {
        final String[] array = new String[]{"abc.soy", "abc"};
        final String out = soyAjaxController.arrayToPath(array);
        assertEquals("abc.soy,abc", out);
    }

}
