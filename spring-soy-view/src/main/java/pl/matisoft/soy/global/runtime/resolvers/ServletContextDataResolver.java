package pl.matisoft.soy.global.runtime.resolvers;

import com.google.template.soy.data.SoyMapData;
import org.springframework.web.context.ServletContextAware;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 01/11/2013
 * Time: 18:13
 */
@Deprecated
public class ServletContextDataResolver implements RuntimeDataResolver, ServletContextAware {

    @Inject
    private ServletContext servletContext;

    private String prefix = "_servlet.context.";

    @Override
    public void resolveData(final HttpServletRequest request, final HttpServletResponse response, final Map<String, ? extends Object> model, SoyMapData root) {
        if (servletContext.getContextPath() != null) {
            root.put(prefix + "contextPath", servletContext.getContextPath());
        }

        root.put(prefix + "minorVersion", servletContext.getMinorVersion());
        root.put(prefix + "majorVersion", servletContext.getMajorVersion());
        if (servletContext.getServletContextName() != null) {
            root.put(prefix + "servletContextName", servletContext.getServletContextName());
        }
        if (servletContext.getServerInfo() != null) {
            root.put(prefix + "serverInfo", servletContext.getServerInfo());
        }
        for (final Enumeration<String> e = servletContext.getInitParameterNames(); e.hasMoreElements();) {
            final String paramName = e.nextElement();
            final String paramValue = servletContext.getInitParameter(paramName);
            if (paramValue != null) {
                root.put(prefix + "parameter." + paramName, paramValue);
            }
        }
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
