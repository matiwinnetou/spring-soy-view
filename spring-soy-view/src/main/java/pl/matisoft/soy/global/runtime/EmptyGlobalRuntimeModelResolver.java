package pl.matisoft.soy.global.runtime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 21/06/2013
 * Time: 22:59
 *
 * An implementation of Null Object Pattern
 */
public class EmptyGlobalRuntimeModelResolver implements GlobalRuntimeModelResolver {

    public Optional<SoyMapData> resolveData(final HttpServletRequest request, final HttpServletResponse response, final Map<String, ? extends Object> model) {
        return Optional.absent();
    }

}
