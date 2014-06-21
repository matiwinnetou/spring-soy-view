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
 * Time: 16:36
 */
@Deprecated
public class RequestHeadersDataResolver implements RuntimeDataResolver {

    private String prefix = "_request.header.";

    @Override
    public void resolveData(final HttpServletRequest request, final HttpServletResponse response, Map<String, ? extends Object> model, final SoyMapData root) {
        for (final Enumeration e = request.getHeaderNames(); e.hasMoreElements();) {
            final String headerName = (String) e.nextElement();
            final String requestHeader = request.getHeader(headerName);
            if (requestHeader != null) {
                root.put(prefix + headerName, requestHeader);
            }
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

}
