package pl.matisoft.soy.render;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 18.10.13
 * Time: 19:24
 */
public class EmptyTemplateRendererTest {

    @InjectMocks
    private EmptyTemplateRenderer emptyTemplateRenderer = new EmptyTemplateRenderer();

    @Mock
    private RenderRequest renderRequest;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testName() throws Exception {
        emptyTemplateRenderer.render(renderRequest);
        Mockito.verifyZeroInteractions(renderRequest);
    }

}
