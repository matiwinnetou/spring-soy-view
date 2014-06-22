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
import pl.matisoft.soy.config.SoyViewConfigDefaults;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    /** a cache of soy msg bundles */
    /** friendly */ Map<Locale, SoyMsgBundle> msgBundles = new ConcurrentHashMap<Locale, SoyMsgBundle>();

    /** a path to a bundle */
    private String messagesPath = SoyViewConfigDefaults.DEF_MESSAGES_PATH;

    /** will cache msgBundles if a hotReloadMode is off, if debug is on,
     *  will compile msg bundles each time it is invoked */
    private boolean hotReloadMode = SoyViewConfigDefaults.DEFAULT_HOT_RELOAD_MODE;

    /** in case translation is missing for a passed in locale,
     *  whether the implementation should fallback to English returning
     *  an english translation if available */
    private boolean fallbackToEnglish = true;

    /**
     * Based on a provided locale return a SoyMsgBundle file.
     *
     * If a passed in locale object is "Optional.absent()",
     * the implementation will return Optional.absent() as well
     * @param locale - maybe locale
     * @return maybe soy msg bundle
     */
    public Optional<SoyMsgBundle> resolve(final Optional<Locale> locale) throws IOException {
        if (!locale.isPresent()) {
            return Optional.absent();
        }

        synchronized (msgBundles) {
            SoyMsgBundle soyMsgBundle = null;
            if (isHotReloadModeOff()) {
                soyMsgBundle = msgBundles.get(locale.get());
            }
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

                if (isHotReloadModeOff()) {
                    msgBundles.put(locale.get(), soyMsgBundle);
                }
            }

            return Optional.fromNullable(soyMsgBundle);
        }
    }

    /**
     * An implementation that using a ContextClassLoader iterates over all urls it finds
     * based on a messagePath and locale, e.g. messages_de_DE.xlf and returns a merged
     * SoyMsgBundle of SoyMsgBundle matching a a pattern it finds in a class path.
     *
     * @param locale - locale
     * @return SoyMsgBundle - bundle
     * @throws java.io.IOException - io error
     */
    protected SoyMsgBundle createSoyMsgBundle(final Locale locale) throws IOException {
        Preconditions.checkNotNull(messagesPath, "messagesPath cannot be null!");
        final String path = messagesPath  + "_" + locale.toString() + ".xlf";

        final Enumeration<URL> e = Thread.currentThread().getContextClassLoader().getResources(path);

        final List<SoyMsgBundle> msgBundles = Lists.newArrayList();

        final SoyMsgBundleHandler msgBundleHandler = new SoyMsgBundleHandler(new XliffMsgPlugin());

        while (e.hasMoreElements()) {
            final URL msgFile = e.nextElement();
            final SoyMsgBundle soyMsgBundle = msgBundleHandler.createFromResource(msgFile);
            msgBundles.add(soyMsgBundle);
        }

        return mergeMsgBundles(locale, msgBundles).orNull();
    }

    /**
     * Merge msg bundles together, creating new MsgBundle with merges msg bundles passed in as a method argument
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

    public void setHotReloadMode(final boolean hotReloadMode) {
        this.hotReloadMode = hotReloadMode;
    }

    public void setFallbackToEnglish(boolean fallbackToEnglish) {
        this.fallbackToEnglish = fallbackToEnglish;
    }

    public String getMessagesPath() {
        return messagesPath;
    }

    public boolean isHotReloadMode() {
        return hotReloadMode;
    }

    private boolean isHotReloadModeOff() {
        return !hotReloadMode;
    }

    public boolean isFallbackToEnglish() {
        return fallbackToEnglish;
    }

}
