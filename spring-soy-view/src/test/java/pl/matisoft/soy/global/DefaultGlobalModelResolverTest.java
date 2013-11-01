package pl.matisoft.soy.global;

import com.google.common.collect.Lists;
import com.google.template.soy.data.SoyMapData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import pl.matisoft.soy.global.runtime.DefaultGlobalModelResolver;
import pl.matisoft.soy.global.runtime.resolvers.RuntimeResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
    public void defaultResolverListNotNull() {
        Assert.assertNotNull("should not be null", defaultGlobalModelResolver.getResolvers());
    }

    @Test
    public void defaultResolverListEmpty() {
        Assert.assertTrue("list should be empty", defaultGlobalModelResolver.getResolvers().isEmpty());
    }

    @Test
    public void shouldResolve() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        Assert.assertNotNull("should not be null", defaultGlobalModelResolver.resolveData(request, null, null));
    }

    @Test
    public void shouldResolveWithResolvers() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);

        final RuntimeResolver mockResolver = mock(RuntimeResolver.class);

        defaultGlobalModelResolver.setResolvers(Lists.<RuntimeResolver>newArrayList(mockResolver));
        defaultGlobalModelResolver.resolveData(request, response, null);
        verify(mockResolver).resolveData(eq(request), eq(response), anyMap(), any(SoyMapData.class));

        Assert.assertNotNull("should not be null", defaultGlobalModelResolver.resolveData(request, response, null));
    }

}
