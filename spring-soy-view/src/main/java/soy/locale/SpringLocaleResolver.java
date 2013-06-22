package soy.locale;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 22:59
 */
public class SpringLocaleResolver implements LocaleResolver {

    private Locale locale;

    @Override
    public Locale resolveLocale(final HttpServletRequest request) {
        return locale;
    }

    public void setLocale(final Locale locale) {
        this.locale = locale;
    }

}
