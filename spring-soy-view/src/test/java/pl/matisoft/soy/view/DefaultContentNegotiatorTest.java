package pl.matisoft.soy.view;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.enumeration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static pl.matisoft.soy.view.DefaultContentNegotiator.ACCEPT_HEADER;
import static pl.matisoft.soy.view.DefaultContentNegotiator.DEFAULT_FAVORED_PARAMETER_NAME;

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
		when(req.getHeaders(ACCEPT_HEADER)).thenReturn(enumeration(asList("text/html", "text/plain")));

		contentNegotiator.setSupportedContentTypes(asList("text/html", "application/xml"));

		assertTrue(contentNegotiator.isSupportedContentTypes());
	}

	@Test
	public void testIsSupportedContentTypesWhenMultipleMatch() {
		when(req.getHeaders(ACCEPT_HEADER)).thenReturn(enumeration(asList("text/html", "application/xml")));

		contentNegotiator.setSupportedContentTypes(asList("text/html", "application/xml"));

		assertTrue(contentNegotiator.isSupportedContentTypes());
	}

	@Test
	public void testIsSupportedContentTypesWhenNoMatch() {
		when(req.getHeaders(ACCEPT_HEADER)).thenReturn(enumeration(asList("text/html")));

		contentNegotiator.setSupportedContentTypes(asList("application/xml"));

		assertFalse(contentNegotiator.isSupportedContentTypes());
	}

	@Test
	public void testIsSupportedContentTypesWhenEmpty() {
		when(req.getHeaders(ACCEPT_HEADER)).thenReturn(enumeration(emptyList()));

		contentNegotiator.setSupportedContentTypes(asList("application/xml"));

		assertFalse(contentNegotiator.isSupportedContentTypes());
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
}
