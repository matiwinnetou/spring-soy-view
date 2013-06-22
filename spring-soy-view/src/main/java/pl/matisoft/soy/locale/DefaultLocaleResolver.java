package pl.matisoft.soy.locale;

import com.google.common.base.Optional;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 22:59
 */
public class DefaultLocaleResolver implements LocaleResolver {

    private Locale locale;

    @Override
    public Optional<Locale> resolveLocale(final HttpServletRequest request) {
        return Optional.fromNullable(locale);
    }

    public void setLocale(final Locale locale) {
        this.locale = locale;
    }

}
