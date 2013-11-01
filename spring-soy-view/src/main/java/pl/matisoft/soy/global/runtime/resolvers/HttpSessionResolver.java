package pl.matisoft.soy.global.runtime.resolvers;

import com.google.template.soy.data.SoyMapData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 01/11/2013
 * Time: 18:21
 */
public class HttpSessionResolver implements RuntimeResolver {

    @Override
    public void resolveData(HttpServletRequest request, HttpServletResponse response, Map<String, ? extends Object> model, SoyMapData root) {
//        final HttpSession session = request.getSession(false);
//        if (session != null) {
//            session.get
//        }
    }

}

