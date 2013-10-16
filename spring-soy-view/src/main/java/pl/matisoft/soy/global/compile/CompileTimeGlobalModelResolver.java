package pl.matisoft.soy.global.compile;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 21/06/2013
 * Time: 22:59
 *
 * An interface, which provides a compile time data, typically the data one would put inside
 * web application's properties files, e.g. site.url
 */
public interface CompileTimeGlobalModelResolver {

    Optional<SoyMapData> resolveData();

}
