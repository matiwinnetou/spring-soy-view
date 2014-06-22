package pl.matisoft.soy.locale;

import com.google.common.base.Optional;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 00:08
 *
 * An implementation of LocaleProvider that uses user's browser Accept-Header
 * setting resolving a preferred locale based on browser configuration.
 */
public class AcceptHeaderLocaleProvider implements LocaleProvider {

    public Optional<Locale> resolveLocale(final HttpServletRequest request) {
        return Optional.fromNullable(request.getLocale());
    }

}
