package pl.matisoft.soy.render;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import pl.matisoft.soy.SoyView;
import pl.matisoft.soy.data.ToSoyDataConverter;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 18.10.13
 * Time: 19:30
 */
public class DefaultTemplateRendererTest {

    @InjectMocks
    private DefaultTemplateRenderer defaultTemplateRenderer = new DefaultTemplateRenderer();

    @Mock
    private ToSoyDataConverter toSoyDataConverter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testDefaultDebug() throws Exception {
        Assert.assertFalse("debug is off by default", defaultTemplateRenderer.isHotReloadMode());
    }

    @Test
    public void testSetDebug() throws Exception {
        defaultTemplateRenderer.setHotReloadMode(true);
        Assert.assertTrue("setting debug flag works", defaultTemplateRenderer.isHotReloadMode());
    }

    @Test
    public void testThrowsNPE() throws Exception {
        try {
            defaultTemplateRenderer.render(null);
            fail("should throw NPE");
        } catch (NullPointerException ex) {
        }
    }

    @Test
    public void testDontRenderWithoutCompiledTemplates() throws Exception {
        final RenderRequest renderRequest = mock(RenderRequest.class);
        final HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
        when(renderRequest.getCompiledTemplates()).thenReturn(Optional.<SoyTofu>absent());
        when(renderRequest.getResponse()).thenReturn(httpServletResponse);
        defaultTemplateRenderer.render(renderRequest);
        Mockito.verifyZeroInteractions(httpServletResponse);
    }

    @Test
    public void testRenderWithoutAnyDataWithoutDebug() throws Exception {
        final Object domainMock = new Object();
        final RenderRequest renderRequest = mock(RenderRequest.class);
        final SoyTofu soyTofu = mock(SoyTofu.class);
        final PrintWriter printWriter = mock(PrintWriter.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final String templateName = "soy.example.clientWords";
        final SoyTofu.Renderer renderer = mock(SoyTofu.Renderer.class);
        final SoyMapData modelData = new SoyMapData();
        final SoyView soyView = mock(SoyView.class);

        when(renderRequest.getCompiledTemplates()).thenReturn(Optional.of(soyTofu));
        when(toSoyDataConverter.toSoyMap(domainMock)).thenReturn(Optional.<SoyMapData>absent());
        when(renderRequest.getModel()).thenReturn(domainMock);
        when(renderRequest.getRequest()).thenReturn(request);
        when(renderRequest.getResponse()).thenReturn(response);
        when(renderRequest.getTemplateName()).thenReturn(templateName);
        when(soyTofu.newRenderer(templateName)).thenReturn(renderer);
        when(renderRequest.getGlobalRuntimeModel()).thenReturn(Optional.<SoyMapData>absent());
        when(renderRequest.getSoyMsgBundle()).thenReturn(Optional.<SoyMsgBundle>absent());
        when(renderRequest.getSoyView()).thenReturn(soyView);
        when(response.getWriter()).thenReturn(printWriter);

        defaultTemplateRenderer.render(renderRequest);

        Mockito.verify(renderer, never()).setDontAddToCache(anyBoolean());
        Mockito.verify(renderer, never()).setData(modelData);
        Mockito.verify(renderer, never()).setIjData(any(SoyMapData.class));
        Mockito.verify(renderer, never()).setMsgBundle(any(SoyMsgBundle.class));
        Mockito.verify(renderer).render();
        Mockito.verify(printWriter).write(anyString());
        Mockito.verify(printWriter).flush();
        Mockito.verify(response).flushBuffer();
    }

    @Test
    public void testRenderWithAllDataWithDebugOn() throws Exception {
        defaultTemplateRenderer.setHotReloadMode(true);
        final Object domainMock = new Object();
        final RenderRequest renderRequest = mock(RenderRequest.class);
        final SoyTofu soyTofu = mock(SoyTofu.class);
        final PrintWriter printWriter = mock(PrintWriter.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final String templateName = "soy.example.clientWords";
        final SoyTofu.Renderer renderer = mock(SoyTofu.Renderer.class);
        final SoyMapData modelData = new SoyMapData();
        final SoyMsgBundle soyMsgBundle = mock(SoyMsgBundle.class);
        final SoyMapData runtimeModel = new SoyMapData();
        final SoyView soyView = mock(SoyView.class);

        when(renderRequest.getCompiledTemplates()).thenReturn(Optional.of(soyTofu));
        when(toSoyDataConverter.toSoyMap(domainMock)).thenReturn(Optional.of(modelData));
        when(renderRequest.getModel()).thenReturn(domainMock);
        when(renderRequest.getRequest()).thenReturn(request);
        when(renderRequest.getResponse()).thenReturn(response);
        when(renderRequest.getTemplateName()).thenReturn(templateName);
        when(soyTofu.newRenderer(templateName)).thenReturn(renderer);
        when(renderRequest.getGlobalRuntimeModel()).thenReturn(Optional.of(runtimeModel));
        when(renderRequest.getSoyMsgBundle()).thenReturn(Optional.of(soyMsgBundle));
        when(response.getWriter()).thenReturn(printWriter);
        when(renderRequest.getSoyView()).thenReturn(soyView);

        defaultTemplateRenderer.render(renderRequest);

        Mockito.verify(renderer).setDontAddToCache(anyBoolean());
        Mockito.verify(renderer).setData(modelData);
        Mockito.verify(renderer).setIjData(runtimeModel);
        Mockito.verify(renderer).setMsgBundle(soyMsgBundle);

        Mockito.verify(renderer).render();
        Mockito.verify(printWriter).write(anyString());
        Mockito.verify(printWriter).flush();
        Mockito.verify(response).flushBuffer();
    }

}
