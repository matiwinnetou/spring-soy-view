package pl.matisoft.soy.global.runtime.resolvers;

import com.google.template.soy.data.SoyMapData;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.support.RequestContext;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 01/11/2013
 * Time: 17:08
 */
@Deprecated
public class RequestContextDataResolver implements RuntimeDataResolver, ServletContextAware {

    private String prefix = "_request.context.";

    @Inject
    private ServletContext servletContext;

    @Override
    public void resolveData(final HttpServletRequest request, final HttpServletResponse response, final Map<String, ? extends Object> model, final SoyMapData root) {
        final RequestContext requestContext = new RequestContext(request, response, servletContext, (Map<String, Object>) model);

        if (requestContext.getContextPath() != null) {
            root.put(prefix + "contextPath", requestContext.getContextPath());
        }
        if (requestContext.getPathToServlet() != null) {
            root.put(prefix + "pathToServlet", requestContext.getPathToServlet());
        }
        if (requestContext.getQueryString() != null) {
            root.put(prefix + "queryString", requestContext.getQueryString());
        }
        if (requestContext.getRequestUri() != null) {
            root.put(prefix + "requestUri", requestContext.getRequestUri());
        }
        if (requestContext.getLocale() != null) {
            root.put(prefix + "locale", requestContext.getLocale().toString());
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public void setServletContext(final ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
