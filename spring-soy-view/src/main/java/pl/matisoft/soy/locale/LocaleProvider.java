package pl.matisoft.soy.locale;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import com.google.common.base.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 00:07
 *
 * An interface that resolves locale needed to obtain SoyMsgBundle object.
 */
public interface LocaleProvider {

    Optional<Locale> resolveLocale(HttpServletRequest request);

}
