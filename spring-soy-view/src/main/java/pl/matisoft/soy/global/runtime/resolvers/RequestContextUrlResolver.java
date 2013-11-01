package pl.matisoft.soy.global.runtime.resolvers;

import com.google.template.soy.data.SoyMapData;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 01/11/2013
 * Time: 17:08
 */
public class RequestContextUrlResolver implements RuntimeResolver {

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    private String prefix = "_request.context.url.";

    @Override
    public void resolveData(final HttpServletRequest request, final HttpServletResponse response, final Map<String, ? extends Object> model, final SoyMapData root) {
        if (urlPathHelper.getContextPath(request) != null) {
            root.put(prefix + "contextPath", urlPathHelper.getContextPath(request));
        }
        if (urlPathHelper.getLookupPathForRequest(request) != null) {
            root.put(prefix + "lookupPathForRequest", urlPathHelper.getLookupPathForRequest(request));
        }
        if (urlPathHelper.getOriginatingContextPath(request) != null) {
            root.put(prefix + "originatingContextPath", urlPathHelper.getOriginatingContextPath(request));
        }
        if (urlPathHelper.getOriginatingQueryString(request) != null) {
            root.put(prefix + "originatingQueryString", urlPathHelper.getOriginatingQueryString(request));
        }
        if (urlPathHelper.getOriginatingRequestUri(request) != null) {
            root.put(prefix + "originatingRequestUri", urlPathHelper.getOriginatingRequestUri(request));
        }
        if (urlPathHelper.getOriginatingServletPath(request) != null) {
            root.put(prefix + "originatingServletPath", urlPathHelper.getOriginatingServletPath(request));
        }
        if (urlPathHelper.getServletPath(request) != null) {
            root.put(prefix + "servletPath", urlPathHelper.getServletPath(request));
        }
        if (urlPathHelper.getPathWithinApplication(request) != null) {
            root.put(prefix + "pathWithinApplication", urlPathHelper.getPathWithinApplication(request));
        }
        if (urlPathHelper.getPathWithinServletMapping(request) != null) {
            root.put(prefix + "pathWithinServletMapping", urlPathHelper.getPathWithinServletMapping(request));
        }
        if (urlPathHelper.getRequestUri(request) != null) {
            root.put(prefix + "requestUri", urlPathHelper.getRequestUri(request));
        }

//        if (requestContext.getDefaultHtmlEscape() != null) {
//            root.put(prefix + "defaultHtmlEscape", urlPathHelper.getrequestContext.getDefaultHtmlEscape().booleanValue());
//        }
//        if (requestContext.getLocale() != null) {
//            root.put(prefix + "locale", requestContext.getLocale().toString());
//        }
//        if (requestContext.getPathToServlet() != null) {
//            root.put(prefix + "pathToServlet", requestContext.getPathToServlet());
//        }
//        if (requestContext.getRequestUri() != null) {
//            root.put(prefix + "requestUri", requestContext.getRequestUri());
//        }
//        if (requestContext.getQueryString() != null) {
//            root.put(prefix + "queryString", requestContext.getQueryString());
//        }
//        if (requestContext.getTheme() != null) {
//            root.put(prefix + "theme.name", requestContext.getTheme().getName());
//        }
//        if (requestContext.getWebApplicationContext() != null) {
//            root.put(prefix + "web.context.startup.date", requestContext.getWebApplicationContext().getStartupDate());
//            if (requestContext.getWebApplicationContext().getServletContext() != null) {
//                root.put(prefix + "servlet.context.server.info", requestContext.getWebApplicationContext().getServletContext().getServerInfo());
//                root.put(prefix + "servlet.context.name", requestContext.getWebApplicationContext().getServletContext().getServletContextName());
//            }
//        }
    }

    public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    public UrlPathHelper getUrlPathHelper() {
        return urlPathHelper;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

}
