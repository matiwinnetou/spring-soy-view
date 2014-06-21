package pl.matisoft.soy.global.runtime.resolvers;

import com.google.template.soy.data.SoyMapData;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationObjectSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 01/11/2013
 * Time: 18:36
 */
@Deprecated
public class WebApplicationContextDataResolver extends WebApplicationObjectSupport implements RuntimeDataResolver {

    private String prefix = "_web.app.context.";

    @Override
    public void resolveData(final HttpServletRequest request, final HttpServletResponse response, final Map<String, ? extends Object> model, final SoyMapData root) {
        final WebApplicationContext context = getWebApplicationContext();
        if (context.getApplicationName() != null) {
            root.put(prefix + "applicationName", context.getApplicationName());
        }
        if (context.getDisplayName() != null) {
            root.put(prefix + "displayName", context.getDisplayName());
        }
        root.put(prefix + "startUp", DateFormat.getDateTimeInstance().format(new Date(context.getStartupDate())));
        if (context.getId() != null) {
            root.put(prefix + "id", context.getId());
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

}
