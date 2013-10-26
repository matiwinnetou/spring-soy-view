package pl.matisoft.soy.ajax;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.matisoft.soy.template.TemplateFilesResolver;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;

import static org.mockito.Mockito.mock;

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

    @Before
    public void setUp() throws Exception {
        soyAjaxController.setTemplateFilesResolver(templateFilesResolver);
    }

    @Test
    @Ignore
    public void testCompileJs() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final URL url1 = getClass().getClassLoader().getResource("template1.soy");
        //when(templateFilesResolver.resolve(anyString())).thenReturn(Optional.of(url1));
        //soyAjaxController.getJsForTemplateFilesHash(new String[]{"template1.soy"}, "true", request);
    }

}
