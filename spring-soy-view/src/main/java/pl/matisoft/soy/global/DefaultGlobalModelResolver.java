package pl.matisoft.soy.global;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 14/07/2013
 * Time: 16:46
 */
public class DefaultGlobalModelResolver implements GlobalModelResolver {

    private static final Logger logger = LoggerFactory.getLogger(DefaultGlobalModelResolver.class);

    private boolean injectRequest = true;
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

        for (final Cookie cookie : request.getCookies()) {
            injectSimpleParams(cookie, soyMapData, "request.cookie." + cookie.getName());
        }
    }

    protected void addServletContext(final HttpServletRequest request, final SoyMapData soyMapData) {

    }

    protected static void injectSimpleParams(final Object obj, final SoyMapData soyMapData, final String keyPrefix) {
        final BeanMap beanMap = new BeanMap(obj);
        for (final Iterator<String> it = beanMap.keyIterator(); it.hasNext();) {
            final String key = it.next();
            final Object value = beanMap.get(key);
            if (beanMap.getType(key).equals(Boolean.class)
                    || beanMap.getType(key).equals(String.class)
                    || beanMap.getType(key).equals(Integer.class)
                    || beanMap.getType(key).equals(Double.class)) {
                soyMapData.put(keyPrefix + "." + key, value);
            }
        }
    }

    public void setInjectRequestParams(boolean injectRequestParams) {
        this.injectRequestParams = injectRequestParams;
    }

    public void setInjectCookies(boolean injectCookies) {
        this.injectCookies = injectCookies;
    }

    public void setInjectRequestHeaders(boolean injectRequestHeaders) {
        this.injectRequestHeaders = injectRequestHeaders;
    }

    public void setInjectRequest(boolean injectRequest) {
        this.injectRequest = injectRequest;
    }

}
