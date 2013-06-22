package pl.matisoft.soy;

import com.google.common.base.Optional;
import com.google.template.soy.msgs.SoyMsgBundle;
import pl.matisoft.soy.bundle.SoyMsgBundleResolver;
import pl.matisoft.soy.config.SoyViewConfig;
import pl.matisoft.soy.locale.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 21/06/2013
 * Time: 00:36
 */
public class SoyUtils {

    public static Optional<SoyMsgBundle> soyMsgBundle(final SoyViewConfig config, HttpServletRequest request) throws IOException {
        final LocaleResolver localeResolver = config.getLocaleResolver();
        final Optional<Locale> locale = localeResolver.resolveLocale(request);

        if (locale.isPresent()) {
            final SoyMsgBundleResolver bundleResolver = config.getSoyMsgBundleResolver();

            return bundleResolver.resolve(locale.get());
        }

        return Optional.absent();
    }

    public static void checkSoyViewConfig(final SoyViewConfig config) {
        if (config == null) {
            throw new RuntimeException("SoyViewConfig needs to injected!");
        }
    }

}
