package pl.matisoft.soy.bundle;

import java.io.IOException;
import java.util.Locale;

import com.google.common.base.Optional;
import com.google.template.soy.msgs.SoyMsgBundle;

/**
 * This interface defines an interface to retrieve SoyMsgBundle, containing localisation
 * data based on user's locale.
 *
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 00:01
 */
public interface SoyMsgBundleResolver {

    Optional<SoyMsgBundle> resolve(Optional<Locale> locale) throws IOException;

}
