package pl.matisoft.soy.bundle;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
import pl.matisoft.soy.config.SoyViewConfig;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 00:01
 *
 * An implementation of SoyMsgBundleResolver that returns SoyMsgBundle based on a configurable url,
 * which can be retrieved from classpath using Thread.getContextClassLoader().
 *
 * The implementation will fallback to English translation if a language specific translation cannot be found
 * only if fallbackToEnglish configuration option is set.
 *
 * Assuming defaults and locale set to pl_PL, an implementation will look for a following file in a classpath:
 * messages_pl_PL.xlf
 */
public class DefaultSoyMsgBundleResolver implements SoyMsgBundleResolver {

    private static final Logger logger = LoggerFactory.getLogger(DefaultSoyMsgBundleResolver.class);

    /** default filename, which should be visible in classpath which defines compiled soy message bundle file */
    public final static String DEF_MESSAGES_PATH = "messages";

    /** a cache of msgBundles */
    private static Map<Locale, SoyMsgBundle> msgBundles = new ConcurrentHashMap<Locale, SoyMsgBundle>();

    /** a path to a bundle */
    private String messagesPath = DEF_MESSAGES_PATH;

    /** will cache msgBundles if a debugOn is off, if debug is on,
     *  will compile msg bundles each time it is invoked */
    private boolean debugOn = SoyViewConfig.DEFAULT_DEBUG_ON;

    /** in case translation is missing for a passed in locale,
     *  whether the implementation should fallback to English returning
     *  an english translation if available */
    private boolean fallbackToEnglish = true;

    /**
     * Based on a provided locale return a SoyMsgBundle file.
     *
     * If a passed in locale object is "Optional.absent()",
     * the implementation will return Optional.absent() as well
     *
     * @param locale
     * @return
     * @throws IOException in case there is an i/o error reading msg bundle
     */
    public Optional<SoyMsgBundle> resolve(final Optional<Locale> locale) throws IOException {
        if (!locale.isPresent()) {
            return Optional.absent();
        }
        if (debugOn) {
            logger.debug("Debug is on, clearing all cached msg bundles.");
            msgBundles = new ConcurrentHashMap<Locale, SoyMsgBundle>();
        }
        synchronized (msgBundles) {
            SoyMsgBundle soyMsgBundle = msgBundles.get(locale.get());
            if (soyMsgBundle == null) {
                soyMsgBundle = createSoyMsgBundle(locale.get());
                if (soyMsgBundle == null) {
                    soyMsgBundle = createSoyMsgBundle(new Locale(locale.get().getLanguage()));
                }

                if (soyMsgBundle == null && fallbackToEnglish) {
                    soyMsgBundle = createSoyMsgBundle(Locale.ENGLISH);
                }

                if (soyMsgBundle == null) {
                    return Optional.absent();
                }

                msgBundles.put(locale.get(), soyMsgBundle);
            }

            return Optional.fromNullable(soyMsgBundle);
        }
    }

    /**
     * An implementation that using a ContextClassLoader iterates over all urls it finds
     * based on a messagePath and locale, e.g. messages_de_DE.xlf and returns a merged
     * SoyMsgBundle of SoyMsgBundle matching a a pattern it finds in a class path.
     *
     * @param locale
     * @return
     * @throws IOException
     */
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

    /**
     * Merge msg bundles together, creating new MsgBundle with merges msg bundles passed in as a method argument
     *
     * @param locale
     * @param soyMsgBundles
     * @return
     */
    private Optional<? extends SoyMsgBundle> mergeMsgBundles(final Locale locale, final List<SoyMsgBundle> soyMsgBundles) {
        if (soyMsgBundles.isEmpty()) {
            return Optional.absent();
        }

        final List<SoyMsg> msgs = Lists.newArrayList();
        for (final SoyMsgBundle smb : soyMsgBundles) {
            for (final Iterator<SoyMsg> it = smb.iterator(); it.hasNext();) {
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

    public void setFallbackToEnglish(boolean fallbackToEnglish) {
        this.fallbackToEnglish = fallbackToEnglish;
    }

}
