package pl.matisoft.soy.global;

import javax.servlet.http.HttpServletRequest;

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
public class EmptyGlobalModelResolver implements GlobalModelResolver {

    public Optional<SoyMapData> resolveData(HttpServletRequest request) {
        return Optional.absent();
    }

}
