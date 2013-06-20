package soy.locale;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 00:08
 */
public class AcceptHeaderLocaleResolver implements LocaleResolver {

    public Locale resolveLocale(final HttpServletRequest request) {
        return request.getLocale();
    }

}
