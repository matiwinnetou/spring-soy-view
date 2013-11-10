package pl.matisoft.soy.global.runtime.resolvers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import com.google.template.soy.data.SoyMapData;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 01/11/2013
 * Time: 16:29
 */
public interface RuntimeDataResolver {

    void resolveData(HttpServletRequest request, HttpServletResponse response, Map<String, ? extends Object> model, SoyMapData root);

}
