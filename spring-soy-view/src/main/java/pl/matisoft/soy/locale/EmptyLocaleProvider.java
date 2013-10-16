package pl.matisoft.soy.locale;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import com.google.common.base.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 22:59
 *
 * An empty implementation, very useful for application localised only in English
 */
public class EmptyLocaleProvider implements LocaleProvider {

    @Override
    public Optional<Locale> resolveLocale(final HttpServletRequest request) {
        return Optional.absent();
    }

}
