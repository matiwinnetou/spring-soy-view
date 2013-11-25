package pl.matisoft.soy.global.runtime.resolvers;

import com.google.template.soy.data.SoyMapData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Vector;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 01/11/2013
 * Time: 16:53
 */
@RunWith(MockitoJUnitRunner.class)
public class RequestParametersDataResolverTest {

    private RequestParametersDataResolver requestParametersResolver = new RequestParametersDataResolver();

    @Test
    public void resolveRequestParameters() throws Exception {
        final SoyMapData soyMapData = new SoyMapData();

        final Vector<String> names = new Vector<String>();
        names.add("name1");
        names.add("name2");

        final Vector<String> values = new Vector<String>();
        values.add("value1");
        values.add("value2");

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);

        final Enumeration e = mock(Enumeration.class);
        when(e.hasMoreElements()).thenReturn(false);
        when(request.getParameterNames()).thenReturn(names.elements());
        when(request.getHeaderNames()).thenReturn(e);
        when(request.getParameter("name1")).thenReturn(values.get(0));
        when(request.getParameter("name2")).thenReturn(values.get(1));

        when(request.getCookies()).thenReturn(new Cookie[]{});
        requestParametersResolver.resolveData(request, response, null, soyMapData);

        Assert.assertEquals("should not be value1", "value1", soyMapData.get("_request.parameter.name1").stringValue());
        Assert.assertEquals("should not be value2", "value2", soyMapData.get("_request.parameter.name2").stringValue());
    }

}
