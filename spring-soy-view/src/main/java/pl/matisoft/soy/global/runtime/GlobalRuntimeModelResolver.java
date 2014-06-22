package pl.matisoft.soy.global.runtime;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 21/06/2013
 * Time: 22:59
 *
 * An implementation that returns a runtime global data, in Soy, it is referred to as
 * "IjData", and this is basically runtime data that can change in the course of application lifetime
 * A good example of IjData is: a logged in user name.
 *
 * Injected data can be referenced using a predefined prefix $ij, {$ij.variable.name}, e.g. {$ij.user.name}
 */
public interface GlobalRuntimeModelResolver {

    Optional<SoyMapData> resolveData(HttpServletRequest request, HttpServletResponse response, Map<String, ? extends Object> model);

}
