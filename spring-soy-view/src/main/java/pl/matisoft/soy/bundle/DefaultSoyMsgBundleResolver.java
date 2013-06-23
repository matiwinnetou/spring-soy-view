package pl.matisoft.soy.bundle;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.SoyMsgBundleHandler;
import com.google.template.soy.msgs.restricted.SoyMsg;
import com.google.template.soy.msgs.restricted.SoyMsgBundleImpl;
import com.google.template.soy.xliffmsgplugin.XliffMsgPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 00:01
 */
public class DefaultSoyMsgBundleResolver implements SoyMsgBundleResolver {

    public final static String DEF_MESSAGES_PATH = "messages";

    private static final Logger logger = LoggerFactory.getLogger(DefaultSoyMsgBundleResolver.class);

    private static Map<Locale, SoyMsgBundle> msgBundles = new ConcurrentHashMap<Locale, SoyMsgBundle>();

    private String messagesPath = DEF_MESSAGES_PATH;

    private boolean debugOn = false;

    public Optional<SoyMsgBundle> resolve(final Optional<Locale> locale) throws IOException {
        if (!locale.isPresent()) {
            return Optional.absent();
        }
        if (debugOn) {
            logger.debug("Debug is on, clearing all cached msg bundles.");
            msgBundles = new ConcurrentHashMap<Locale, SoyMsgBundle>();
        }
        SoyMsgBundle soyMsgBundle = msgBundles.get(locale.get());
        if (soyMsgBundle == null) {
            soyMsgBundle = createSoyMsgBundle(locale.get());
            if (soyMsgBundle == null) {
                soyMsgBundle = createSoyMsgBundle(new Locale(locale.get().getLanguage()));
            }

            if (soyMsgBundle == null) {
                soyMsgBundle = createSoyMsgBundle(Locale.ENGLISH);
            }

            if (soyMsgBundle == null) {
                throw new IOException("No message bundle found.");
            }

            msgBundles.put(locale.get(), soyMsgBundle);
        }

        return Optional.fromNullable(soyMsgBundle);
    }

    protected SoyMsgBundle createSoyMsgBundle(final Locale locale) throws IOException {
        Preconditions.checkNotNull(messagesPath, "messagesPath cannot be null!");
        final String path = messagesPath  + "_" + locale.toString() + ".xlf";

        final Enumeration<URL> e = Thread.currentThread().getContextClassLoader().getResources(path);

        final List<SoyMsgBundle> msgBundles = Lists.newArrayList();

        final SoyMsgBundleHandler msgBundleHandler = new SoyMsgBundleHandler(new XliffMsgPlugin());

        while (e.hasMoreElements()) {
            final URL msgFile = e.nextElement();
            msgBundles.add(msgBundleHandler.createFromResource(msgFile));
        }

        return mergeMsgBundles(locale, msgBundles).orNull();
    }

    private Optional<? extends SoyMsgBundle> mergeMsgBundles(final Locale locale, final List<SoyMsgBundle> soyMsgBundles) {
        if (soyMsgBundles.isEmpty()) {
            return Optional.absent();
        }

        final List<SoyMsg> msgs = Lists.newArrayList();

        for (final SoyMsgBundle smb : soyMsgBundles) {
            for (Iterator<SoyMsg> it = smb.iterator(); it.hasNext();) {
                msgs.add(it.next());
            }
        }

        return Optional.of(new SoyMsgBundleImpl(locale.toString(), msgs));
    }

    public void setMessagesPath(final String messagesPath) {
        this.messagesPath = messagesPath;
    }

    public void setDebugOn(final boolean debugOn) {
        this.debugOn = debugOn;
    }

}
