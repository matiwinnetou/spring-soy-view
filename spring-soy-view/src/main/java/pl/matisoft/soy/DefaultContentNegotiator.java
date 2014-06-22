package pl.matisoft.soy;

import static java.util.Arrays.asList;
import static java.util.Collections.list;
import static java.util.Collections.unmodifiableList;
import static java.util.regex.Pattern.quote;
import static org.springframework.util.Assert.isInstanceOf;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * This class is responsible for determining whether or not it can support the requested content types.
 * 
 * @author Abhijit Sarkar
 */
public class DefaultContentNegotiator implements ContentNegotiator {

	private static final Logger logger = LoggerFactory.getLogger(DefaultContentNegotiator.class);

	public static final String DEFAULT_FAVORED_PARAMETER_NAME = "format";
	public static final List<String> DEFAULT_SUPPORTED_CONTENT_TYPES = unmodifiableList(asList("text/html"));
	public static final String ACCEPT_HEADER = "Accept";

	private static final String CONTENT_TYPE_DELIMITER = ",";

	private List<String> supportedContentTypes;
	private boolean favorParameterOverAcceptHeader;
	private String favoredParameterName;
	private Comparator<List<String>> contentTypeComparator;

	public DefaultContentNegotiator() {
		supportedContentTypes = DEFAULT_SUPPORTED_CONTENT_TYPES;
		favorParameterOverAcceptHeader = false;
		favoredParameterName = DEFAULT_FAVORED_PARAMETER_NAME;
		contentTypeComparator = new DefaultContentTypeComparator();
	}

	public List<String> getSupportedContentTypes() {
		return supportedContentTypes;
	}

	public void setSupportedContentTypes(final List<String> supportedContentTypes) {
		this.supportedContentTypes = supportedContentTypes;
	}

	public boolean isFavorParameterOverAcceptHeader() {
		return favorParameterOverAcceptHeader;
	}

	public void setFavorParameterOverAcceptHeader(final boolean favorParameterOverAcceptHeader) {
		this.favorParameterOverAcceptHeader = favorParameterOverAcceptHeader;
	}

	public String getFavoredParameterName() {
		return favoredParameterName;
	}

	public void setFavoredParameterName(final String parameterName) {
		this.favoredParameterName = parameterName;
	}

	public Comparator<List<String>> getContentTypeComparator() {
		return contentTypeComparator;
	}

	public void setContentTypeComparator(final Comparator<List<String>> contentTypeComparator) {
		this.contentTypeComparator = contentTypeComparator;
	}

	/**
	 * Returns true if able to support the requested content types; false otherwise.
	 * 
	 * @return True if able to support the requested content types; false otherwise.
	 */
	@Override
	public boolean isSupportedContentTypes(final List<String> contentTypes) {
		return contentTypeComparator.compare(supportedContentTypes, contentTypes) == 0;
	}

	/**
	 * Returns requested content types or default content type if none found.
	 * 
	 * @return Requested content types or default content type if none found.
	 */
	@Override
	public List<String> contentTypes() {
		List<String> contentTypes = null;
		final HttpServletRequest request = getHttpRequest();

		if (favorParameterOverAcceptHeader) {
			contentTypes = getFavoredParameterValueAsList(request);
		} else {
			contentTypes = getAcceptHeaderValues(request);
		}

		if (isEmpty(contentTypes)) {
			logger.debug("Setting content types to default: {}.", DEFAULT_SUPPORTED_CONTENT_TYPES);

			contentTypes = DEFAULT_SUPPORTED_CONTENT_TYPES;
		}

		return unmodifiableList(contentTypes);
	}

	private HttpServletRequest getHttpRequest() {
		final RequestAttributes attrs = getRequestAttributes();
		isInstanceOf(ServletRequestAttributes.class, attrs);

		return ((ServletRequestAttributes) attrs).getRequest();
	}

	private List<String> getFavoredParameterValueAsList(HttpServletRequest request) {
		logger.debug("Retrieving content type from favored parameter: {}.", favoredParameterName);

		final String favoredParameterValue = request.getParameter(favoredParameterName);

		return favoredParameterValue != null ? splitContentTypes(asList(favoredParameterValue)) : null;
	}

	@SuppressWarnings("unchecked")
	private List<String> getAcceptHeaderValues(HttpServletRequest request) {
		logger.debug("Retrieving content type from header: {}.", ACCEPT_HEADER);

		return splitContentTypes(list(request.getHeaders(ACCEPT_HEADER)));
	}

	private List<String> splitContentTypes(final List<String> contentTypes) {
		final List<String> splitContentTypes = new ArrayList<String>();

		for (String aContentType : contentTypes) {
			splitContentTypes.addAll(asList(aContentType.split(CONTENT_TYPE_DELIMITER)));
		}

		logger.debug("Split content types {} into {}.", contentTypes, splitContentTypes);

		return splitContentTypes;
	}

	private static final class DefaultContentTypeComparator implements Comparator<List<String>> {
		private static final int NOT_EQUAL = 1;
		private static final int EQUAL = 0;
		private static final String ASTERISK = "*";
		private static final String PERIOD_PLUS_ASTERISK = ".*";

		@Override
		public int compare(List<String> supportedContentTypes, List<String> contentTypes) {
			logger.debug("Comparing supported content types with request content types: {}, {}.",
					supportedContentTypes, contentTypes);

			if (isEmpty(contentTypes)) {
				return NOT_EQUAL;
			}

			for (String aContentType : contentTypes) {
				for (String aSupportedContentType : supportedContentTypes) {
					if (aSupportedContentType.matches(convertToRegex(aContentType))) {
						return EQUAL;
					}
				}
			}

			return NOT_EQUAL;
		}

		private String convertToRegex(final String aContentType) {
			return aContentType.replaceAll(quote(ASTERISK), PERIOD_PLUS_ASTERISK);
		}
	}
}
