package pl.matisoft.soy.global;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 21/06/2013
 * Time: 22:59
 */
public interface GlobalModelResolver {

    Optional<SoyMapData> resolveData();

}
