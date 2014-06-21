package pl.matisoft.soy.global.runtime.resolvers;

import com.google.template.soy.data.SoyMapData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 01/11/2013
 * Time: 16:24
 */
@Deprecated
public class RequestParametersDataResolver implements RuntimeDataResolver {

    private String prefix = "_request.parameter.";

    @Override
    public void resolveData(final HttpServletRequest request, final HttpServletResponse response, final Map<String, ? extends Object> model, final SoyMapData root) {
        for (final Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            final String paramName = (String) e.nextElement();
            final String parameter = request.getParameter(paramName);
            if (parameter != null) {
                root.put("_request.parameter." + paramName, parameter);
            }
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
