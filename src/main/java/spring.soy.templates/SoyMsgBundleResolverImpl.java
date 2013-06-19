package spring.soy.templates;

import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.SoyMsgBundleHandler;
import com.google.template.soy.xliffmsgplugin.XliffMsgPlugin;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 00:01
 */
public class SoyMsgBundleResolverImpl {

    private static final Map<Locale, SoyMsgBundle> msgBundles = new ConcurrentHashMap<Locale, SoyMsgBundle>();

    public SoyMsgBundle resolve(Locale locale) throws IOException {
        SoyMsgBundle soyMsgBundle = msgBundles.get(locale);
        if (soyMsgBundle == null) {
            soyMsgBundle = createSoyMsgBundle(locale);
            if (soyMsgBundle == null) {
                soyMsgBundle = createSoyMsgBundle(new Locale(locale.getLanguage()));
            }
            if (soyMsgBundle == null) {
                soyMsgBundle = createSoyMsgBundle(Locale.ENGLISH);
            }
            if (soyMsgBundle == null) {
                throw new IOException("No message bundle found.");
            }

            msgBundles.put(locale, soyMsgBundle);
        }

        return soyMsgBundle;
    }

    private static SoyMsgBundle createSoyMsgBundle(Locale locale) throws IOException {
        URL msgFile = Thread.currentThread().getContextClassLoader().getResource("xliffs/messages_"+locale.toString()+".xlf");
        if (msgFile == null){
            return null;
        }

        SoyMsgBundleHandler msgBundleHandler = new SoyMsgBundleHandler(new XliffMsgPlugin());
        SoyMsgBundle msgBundle = msgBundleHandler.createFromResource(msgFile);

        return msgBundle;
    }

}
