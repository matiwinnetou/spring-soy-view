package pl.matisoft.soy.global.runtime.resolvers;

import com.google.template.soy.data.SoyMapData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 01/11/2013
 * Time: 18:21
 */
@Deprecated
public class HttpSessionDataResolver implements RuntimeDataResolver {

    private String prefix = "_http.session.";

    @Override
    public void resolveData(final HttpServletRequest request, final HttpServletResponse response, final Map<String, ? extends Object> model, final SoyMapData root) {
        final HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        appendId(root, session);
        appendCreationTime(root, session);
        appendLastAccessedTime(root, session);
        appendMaxInactiveInterval(root, session);
    }

    private void appendMaxInactiveInterval(final SoyMapData root, final HttpSession session) {
        root.put(prefix + "maxInactiveInterval", session.getMaxInactiveInterval());
    }

    private void appendLastAccessedTime(final SoyMapData root, final HttpSession session) {
        root.put(prefix + "lastAccessedTime", DateFormat.getDateTimeInstance().format(new Date(session.getLastAccessedTime())));
    }

    private void appendCreationTime(final SoyMapData root, final HttpSession session) {
        root.put(prefix + "creationTime", DateFormat.getDateTimeInstance().format(new Date(session.getCreationTime())));
    }

    private void appendId(final SoyMapData root, final HttpSession session) {
        if (session.getId() != null) {
            root.put(prefix + "id", session.getId());
        }
    }

}
