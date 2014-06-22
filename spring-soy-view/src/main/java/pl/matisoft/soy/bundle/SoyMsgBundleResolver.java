package pl.matisoft.soy.bundle;

import com.google.common.base.Optional;
import com.google.template.soy.msgs.SoyMsgBundle;

import java.io.IOException;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 00:01
 *
 * This interface defines a possibility to obtain SoyMsgBundle, containing localisation
 * data based on user's locale.
 */
public interface SoyMsgBundleResolver {

    Optional<SoyMsgBundle> resolve(Optional<Locale> locale) throws IOException;

}
