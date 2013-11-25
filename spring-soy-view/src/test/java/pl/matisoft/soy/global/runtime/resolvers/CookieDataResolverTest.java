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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 01/11/2013
 * Time: 16:48
 */
@RunWith(MockitoJUnitRunner.class)
public class CookieDataResolverTest {

    private CookieDataResolver cookieResolver = new CookieDataResolver();

    @Test
    public void testBindCookies() throws Exception {
        final SoyMapData soyMapData = new SoyMapData();

        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);

        final Enumeration e = mock(Enumeration.class);
        when(e.hasMoreElements()).thenReturn(false);
        when(request.getParameterNames()).thenReturn(e);
        when(request.getHeaderNames()).thenReturn(e);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("name1", "value1"), new Cookie("name2", "value2")});

        cookieResolver.resolveData(request, response, null, soyMapData);

        Assert.assertEquals("should not be value1", "value1", soyMapData.get("_request.cookie.name1.value").stringValue());
        Assert.assertEquals("should not be value2", "value2", soyMapData.get("_request.cookie.name2.value").stringValue());

        Assert.assertEquals("should not be name1", "name1", soyMapData.get("_request.cookie.name1.name").stringValue());
        Assert.assertEquals("should not be name2", "name2", soyMapData.get("_request.cookie.name2.name").stringValue());
    }

}
