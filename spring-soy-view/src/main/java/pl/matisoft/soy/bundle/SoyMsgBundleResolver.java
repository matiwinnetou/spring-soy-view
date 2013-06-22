package pl.matisoft.soy.bundle;

import com.google.template.soy.msgs.SoyMsgBundle;

import java.io.IOException;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 00:01
 */
public interface SoyMsgBundleResolver {

    SoyMsgBundle resolve(Locale locale) throws IOException;

}
