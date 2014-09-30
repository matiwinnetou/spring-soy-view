package pl.matisoft.soy;

import static java.util.Arrays.asList;
import static java.util.Collections.enumeration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static pl.matisoft.soy.DefaultContentNegotiator.ACCEPT_HEADER;
import static pl.matisoft.soy.DefaultContentNegotiator.DEFAULT_FAVORED_PARAMETER_NAME;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ServletRequestAttributes.class, HttpServletRequest.class, RequestContextHolder.class })
public class DefaultContentNegotiatorTest {
	private DefaultContentNegotiator contentNegotiator = new DefaultContentNegotiator();
	private HttpServletRequest req;

	@Before
	public void setUp() {
		ServletRequestAttributes attrs = mock(ServletRequestAttributes.class);
		req = mock(HttpServletRequest.class);
		when(attrs.getRequest()).thenReturn(req);

		mockStatic(RequestContextHolder.class);
		when(RequestContextHolder.getRequestAttributes()).thenReturn(attrs);
	}

	@Test
	public void testIsSupportedContentTypesWhenOneMatch() {
		contentNegotiator.setSupportedContentTypes(asList("text/html", "application/xml"));

		assertTrue(contentNegotiator.isSupportedContentTypes(asList("text/html", "text/plain")));
	}

	@Test
	public void testIsSupportedContentTypesWhenMultipleMatch() {
		contentNegotiator.setSupportedContentTypes(asList("text/html", "application/xml"));

		assertTrue(contentNegotiator.isSupportedContentTypes(asList("text/html", "application/xml")));
	}

	@Test
	public void testIsSupportedContentTypesWhenNoMatch() {
		contentNegotiator.setSupportedContentTypes(asList("application/xml"));

		assertFalse(contentNegotiator.isSupportedContentTypes(asList("text/html")));
	}

	@Test
	public void testIsSupportedContentTypesWhenEmpty() {
		contentNegotiator.setSupportedContentTypes(asList("application/xml"));

		assertFalse(contentNegotiator.isSupportedContentTypes(null));
	}

	@Test
	public void testIsSupportedContentTypesWhenWildcard() {
		contentNegotiator.setSupportedContentTypes(asList("text/html"));

		assertTrue(contentNegotiator.isSupportedContentTypes(asList("*/*")));
	}

	@Test
	public void testContentTypesWhenFavoredParameter() {
		when(req.getParameter(DEFAULT_FAVORED_PARAMETER_NAME)).thenReturn("text/html");
		when(req.getHeaders(ACCEPT_HEADER)).thenReturn(enumeration(asList("text/html", "text/plain")));

		contentNegotiator.setFavorParameterOverAcceptHeader(true);

		assertEquals(asList("text/html"), contentNegotiator.contentTypes());
	}

	@Test
	public void testContentTypesWhenAcceptHeader() {
		when(req.getParameter(DEFAULT_FAVORED_PARAMETER_NAME)).thenReturn("text/html");
		when(req.getHeaders(ACCEPT_HEADER)).thenReturn(enumeration(asList("text/html", "text/plain")));

		assertEquals(asList("text/html", "text/plain"), contentNegotiator.contentTypes());
	}

	@Test
	public void testContentTypesWhenFavoredSetButNull() {
		when(req.getParameter(DEFAULT_FAVORED_PARAMETER_NAME)).thenReturn(null);

		contentNegotiator.setFavorParameterOverAcceptHeader(true);

		assertEquals(asList("text/html"), contentNegotiator.contentTypes());
	}

	@Test
	public void testContentTypesWhenNeedsSplit() {
		when(req.getHeaders(ACCEPT_HEADER)).thenReturn(
				enumeration(asList("text/html,application/xhtml+xml,application/xml;q=0.9")));

		assertEquals(asList("text/html", "application/xhtml+xml", "application/xml;q=0.9"),
				contentNegotiator.contentTypes());
	}

    @Test
    public void testContentTypesWhenNeedsSplitWithWhitespace() {
        when(req.getHeaders(ACCEPT_HEADER)).thenReturn(
                enumeration(asList("text/html, application/xhtml+xml, application/xml;q=0.9")));

        assertEquals(asList("text/html", "application/xhtml+xml", "application/xml;q=0.9"),
                contentNegotiator.contentTypes());
    }
}
