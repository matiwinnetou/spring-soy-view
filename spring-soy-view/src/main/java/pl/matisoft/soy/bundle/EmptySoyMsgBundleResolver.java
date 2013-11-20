package pl.matisoft.soy.bundle;

import java.io.IOException;
import java.util.Locale;

import com.google.common.base.Optional;
import com.google.template.soy.msgs.SoyMsgBundle;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 22:37
 *
 * An empty implementation of SoyMsgBundleResolver that always returns empty SoyMsgBundle,
 * i.e. absent value
 */
public class EmptySoyMsgBundleResolver implements SoyMsgBundleResolver {

    @Override
    public Optional<SoyMsgBundle> resolve(final Optional<Locale> locale) throws IOException {
        return Optional.absent();
    }

}
