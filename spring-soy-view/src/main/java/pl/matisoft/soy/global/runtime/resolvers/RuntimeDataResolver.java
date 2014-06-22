package pl.matisoft.soy.global.runtime.resolvers;

import com.google.template.soy.data.SoyMapData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 01/11/2013
 * Time: 16:29
 */
public interface RuntimeDataResolver {

    void resolveData(HttpServletRequest request, HttpServletResponse response, Map<String, ? extends Object> model, SoyMapData root);

}
