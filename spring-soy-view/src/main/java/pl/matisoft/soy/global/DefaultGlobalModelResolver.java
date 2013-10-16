package pl.matisoft.soy.global;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 14/07/2013
 * Time: 16:46
 *
 * A default implementation of GlobalModelResolver that contains a support for
 * automatically injecting as injected data "request parameters", "request headers" and
 * "cookies".
 *
 * Those injected values will automatically map by convention to:
 * 1. request parameters will map to: $ij.request.parameter.variableName
 * 2. request headers will map to: $ij.request.header.variableName
 * 3. cookies will map to: $ij.request.cookie.variableName
 *
 * All parameter values will be mapped to strings
 */
public class DefaultGlobalModelResolver implements GlobalModelResolver {

    private static final Logger logger = LoggerFactory.getLogger(DefaultGlobalModelResolver.class);

    private boolean injectRequestParams = true;
    private boolean injectRequestHeaders = true;
    private boolean injectCookies = true;

    @Override
    public Optional<SoyMapData> resolveData(final HttpServletRequest request) {
        final SoyMapData soyMapData = new SoyMapData();

        addRequestParameters(request, soyMapData);
        addRequestHeaders(request, soyMapData);
        addCookies(request, soyMapData);

        return Optional.of(soyMapData);
    }

    protected void addRequestHeaders(final HttpServletRequest request, final SoyMapData soyMapData) {
        if (!injectRequestHeaders) {
            return;
        }

        for (final Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
            final String headerName = (String) e.nextElement();
            soyMapData.put("request.header." + headerName, request.getHeader(headerName));
        }
    }

    protected void addRequestParameters(final HttpServletRequest request, final SoyMapData soyMapData) {
        if (!injectRequestParams) {
            return;
        }

        for (final Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            final String paramName = (String) e.nextElement();
            soyMapData.put("request.parameter." + paramName, request.getParameter(paramName));
        }
    }

    protected void addCookies(final HttpServletRequest request, final SoyMapData soyMapData) {
        if (!injectCookies) {
            return;
        }

        if (request.getCookies() == null) {
            logger.debug("no cookies!");
            return;
        }
        for (final Cookie cookie : request.getCookies()) {
            if (StringUtils.hasLength(cookie.getName())) {
                final String keyPrefix = "request.cookie." + cookie.getName();
                soyMapData.put(keyPrefix + ".name", cookie.getName());
                if (StringUtils.hasLength(cookie.getValue())) {
                    soyMapData.put(keyPrefix + ".value", cookie.getValue());
                }
                if (StringUtils.hasLength(cookie.getComment())) {
                    soyMapData.put(keyPrefix + ".comment", cookie.getComment());
                }
                if (StringUtils.hasLength(cookie.getDomain())) {
                    soyMapData.put(keyPrefix + ".domain", cookie.getDomain());
                }
                soyMapData.put(keyPrefix + ".maxAge", cookie.getMaxAge());

                if (StringUtils.hasLength(cookie.getPath())) {
                    soyMapData.put(keyPrefix + ".path", cookie.getPath());
                }
                soyMapData.put(keyPrefix + ".version", cookie.getVersion());
                soyMapData.put(keyPrefix + ".secure", cookie.getSecure());
            }
        }
    }

    public void setInjectRequestParams(final boolean injectRequestParams) {
        this.injectRequestParams = injectRequestParams;
    }

    public void setInjectCookies(final boolean injectCookies) {
        this.injectCookies = injectCookies;
    }

    public void setInjectRequestHeaders(final boolean injectRequestHeaders) {
        this.injectRequestHeaders = injectRequestHeaders;
    }

}
