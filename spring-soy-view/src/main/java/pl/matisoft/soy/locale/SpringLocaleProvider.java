package pl.matisoft.soy.locale;

import com.google.common.base.Optional;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 01/11/2013
 * Time: 21:47
 */
public class SpringLocaleProvider implements LocaleProvider {

    @Override
    public Optional<Locale> resolveLocale(final HttpServletRequest request) {
        final LocaleResolver localeResolver = (LocaleResolver) request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE);
        if (localeResolver != null) {
            final Locale locale = localeResolver.resolveLocale(request);
            if (locale != null) {
                return Optional.of(locale);
            }
        }

        return Optional.fromNullable(request.getLocale());
    }

}
