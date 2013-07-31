package pl.matisoft.soy.global;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 21/06/2013
 * Time: 22:59
 */
public class EmptyGlobalModelResolver implements GlobalModelResolver {

    public Optional<SoyMapData> resolveData(HttpServletRequest request) {
        return Optional.absent();
    }

}
