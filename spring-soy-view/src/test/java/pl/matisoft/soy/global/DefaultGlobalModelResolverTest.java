package pl.matisoft.soy.global;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Vector;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 20.10.13
 * Time: 19:56
 */
public class DefaultGlobalModelResolverTest {

    @InjectMocks
    private DefaultGlobalModelResolver defaultGlobalModelResolver = new DefaultGlobalModelResolver();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void defaultInjectCookies() throws Exception {
        Assert.assertTrue(defaultGlobalModelResolver.isInjectCookies());
    }

    @Test
    public void defaultInjectRequestHeaders() throws Exception {
        Assert.assertTrue(defaultGlobalModelResolver.isInjectRequestHeaders());
    }

    @Test
    public void defaultInjectRequestParams() throws Exception {
        Assert.assertTrue(defaultGlobalModelResolver.isInjectRequestParams());
    }

    @Test
    public void resolveData() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final Enumeration e = mock(Enumeration.class);
        when(e.hasMoreElements()).thenReturn(false);
        when(request.getParameterNames()).thenReturn(e);
        when(request.getHeaderNames()).thenReturn(e);
        when(request.getCookies()).thenReturn(new Cookie[]{});
        final Optional<SoyMapData> soyMapDataOptional = defaultGlobalModelResolver.resolveData(request);
        Assert.assertTrue(soyMapDataOptional.isPresent());
        Assert.assertEquals("should be empty", 0, soyMapDataOptional.get().asMap().size());
    }

    @Test
    public void resolveDataRequestHeaders() throws Exception {
        final Vector<String> names = new Vector<String>();
        names.add("name1");
        names.add("name2");

        final Vector<String> values = new Vector<String>();
        values.add("value1");
        values.add("value2");

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final Enumeration e = mock(Enumeration.class);
        when(e.hasMoreElements()).thenReturn(false);
        when(request.getParameterNames()).thenReturn(e);
        when(request.getHeaderNames()).thenReturn(names.elements());
        when(request.getHeader("name1")).thenReturn(values.get(0));
        when(request.getHeader("name2")).thenReturn(values.get(1));

        when(request.getCookies()).thenReturn(new Cookie[]{});
        final Optional<SoyMapData> soyMapDataOptional = defaultGlobalModelResolver.resolveData(request);
        Assert.assertTrue(soyMapDataOptional.isPresent());

        Assert.assertEquals("should not be value1", "value1", soyMapDataOptional.get().get("request.header.name1").stringValue());
        Assert.assertEquals("should not be value2", "value2", soyMapDataOptional.get().get("request.header.name2").stringValue());
    }

    @Test
    public void resolveDataCookies() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final Enumeration e = mock(Enumeration.class);
        when(e.hasMoreElements()).thenReturn(false);
        when(request.getParameterNames()).thenReturn(e);
        when(request.getHeaderNames()).thenReturn(e);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("name1", "value1"), new Cookie("name2", "value2")});

        final Optional<SoyMapData> soyMapDataOptional = defaultGlobalModelResolver.resolveData(request);
        Assert.assertTrue(soyMapDataOptional.isPresent());

        Assert.assertEquals("should not be value1", "value1", soyMapDataOptional.get().get("request.cookie.name1.value").stringValue());
        Assert.assertEquals("should not be value2", "value2", soyMapDataOptional.get().get("request.cookie.name2.value").stringValue());

        Assert.assertEquals("should not be name1", "name1", soyMapDataOptional.get().get("request.cookie.name1.name").stringValue());
        Assert.assertEquals("should not be name2", "name2", soyMapDataOptional.get().get("request.cookie.name2.name").stringValue());
    }

    @Test
    public void resolveDataParameters() throws Exception {
        final Vector<String> names = new Vector<String>();
        names.add("name1");
        names.add("name2");

        final Vector<String> values = new Vector<String>();
        values.add("value1");
        values.add("value2");

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final Enumeration e = mock(Enumeration.class);
        when(e.hasMoreElements()).thenReturn(false);
        when(request.getParameterNames()).thenReturn(names.elements());
        when(request.getHeaderNames()).thenReturn(e);
        when(request.getParameter("name1")).thenReturn(values.get(0));
        when(request.getParameter("name2")).thenReturn(values.get(1));

        when(request.getCookies()).thenReturn(new Cookie[]{});
        final Optional<SoyMapData> soyMapDataOptional = defaultGlobalModelResolver.resolveData(request);
        Assert.assertTrue(soyMapDataOptional.isPresent());

        Assert.assertEquals("should not be value1", "value1", soyMapDataOptional.get().get("request.parameter.name1").stringValue());
        Assert.assertEquals("should not be value2", "value2", soyMapDataOptional.get().get("request.parameter.name2").stringValue());
    }

}
