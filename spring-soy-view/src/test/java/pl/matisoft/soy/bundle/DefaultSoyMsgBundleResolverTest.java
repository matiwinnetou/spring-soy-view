package pl.matisoft.soy.bundle;

import com.google.common.base.Optional;
import com.google.template.soy.msgs.SoyMsgBundle;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.Locale;

import static junit.framework.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 22/10/2013
 * Time: 21:25
 */
public class DefaultSoyMsgBundleResolverTest {

    @InjectMocks
    private DefaultSoyMsgBundleResolver defaultSoyMsgBundleResolver = new DefaultSoyMsgBundleResolver();

    @Test
    public void defaultDebug() throws Exception {
        Assert.assertFalse("debug should be off", defaultSoyMsgBundleResolver.isHotReloadMode());
    }

    @Test
    public void defaultMessagesPath() throws Exception {
        Assert.assertEquals("default messages path", "messages", defaultSoyMsgBundleResolver.getMessagesPath());
    }

    @Test
    public void settingDebug() throws Exception {
        defaultSoyMsgBundleResolver.setHotReloadMode(true);
        Assert.assertTrue("debug should be on", defaultSoyMsgBundleResolver.isHotReloadMode());
    }

    @Test
    public void defaultFallbackToEnglish() throws Exception {
        Assert.assertTrue("fallBack to English should be on", defaultSoyMsgBundleResolver.isFallbackToEnglish());
    }

    @Test
    public void resolveWithNoLocale() throws Exception {
        Assert.assertFalse("value should be absent", defaultSoyMsgBundleResolver.resolve(Optional.<Locale>absent()).isPresent());
    }

    @Test
    public void resolveDefaultMessagePathValuePresent() throws Exception {
        final Optional<SoyMsgBundle> soyMsgBundleOptional = defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.ENGLISH));
        Assert.assertTrue("value should be present", soyMsgBundleOptional.isPresent());
    }

    @Test
    public void resolveDefaultMessagePathNotNull() throws Exception {
        final Optional<SoyMsgBundle> soyMsgBundleOptional = defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.ENGLISH));
        Assert.assertNotNull("value should be present", soyMsgBundleOptional.get());
    }

    @Test
    public void resolveDefaultMessagePathOneMsg() throws Exception {
        defaultSoyMsgBundleResolver.setMessagesPath("messages");
        final Optional<SoyMsgBundle> soyMsgBundleOptional = defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.ENGLISH));
        Assert.assertEquals("1 msg should be there", 1, soyMsgBundleOptional.get().getNumMsgs());
    }

    @Test
    public void resolveDefaultMessagePathOneMsgLocaleMatches() throws Exception {
        defaultSoyMsgBundleResolver.setMessagesPath("messages");
        final Optional<SoyMsgBundle> soyMsgBundleOptional = defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.ENGLISH));
        Assert.assertEquals("locale string should equal", "en", soyMsgBundleOptional.get().getLocaleString());
    }

    @Test
    public void resolveCustomMessagePathTwoMsgs() throws Exception {
        defaultSoyMsgBundleResolver.setMessagesPath("msg/messages");
        final Optional<SoyMsgBundle> soyMsgBundleOptional = defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.ENGLISH));
        Assert.assertEquals("2 msgs should be there", 2, soyMsgBundleOptional.get().getNumMsgs());
    }

    @Test
    public void resolveCustomMessagePathTwoMsgsLocaleMatches() throws Exception {
        defaultSoyMsgBundleResolver.setMessagesPath("msg/messages");
        final Optional<SoyMsgBundle> soyMsgBundleOptional = defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.ENGLISH));
        Assert.assertEquals("locale string should equal", "en", soyMsgBundleOptional.get().getLocaleString());
    }

    @Test
    public void resolveCustomMessagePathGermany() throws Exception {
        defaultSoyMsgBundleResolver.setMessagesPath("msg/messages");
        final Optional<SoyMsgBundle> soyMsgBundleOptional = defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.GERMANY));
        Assert.assertEquals("3 msgs should be there", 3, soyMsgBundleOptional.get().getNumMsgs());
        Assert.assertEquals("locale string should equal", "de_DE", soyMsgBundleOptional.get().getLocaleString());
    }

    @Test
    public void resolveCustomMessagePathGermanyLocaleMatches() throws Exception {
        defaultSoyMsgBundleResolver.setMessagesPath("msg/messages");
        final Optional<SoyMsgBundle> soyMsgBundleOptional = defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.GERMANY));
        Assert.assertEquals("locale string should equal", "de_DE", soyMsgBundleOptional.get().getLocaleString());
    }

    @Test
    public void resolveCustomMessagePathGerman() throws Exception {
        defaultSoyMsgBundleResolver.setMessagesPath("msg/messages");
        final Optional<SoyMsgBundle> soyMsgBundleOptional = defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.GERMAN));
        Assert.assertEquals("4 msgs should be there", 4, soyMsgBundleOptional.get().getNumMsgs());
        Assert.assertEquals("locale string should equal", "de", soyMsgBundleOptional.get().getLocaleString());
    }

    @Test
    public void resolveCustomMessagePathGermanLocaleMatches() throws Exception {
        defaultSoyMsgBundleResolver.setMessagesPath("msg/messages");
        final Optional<SoyMsgBundle> soyMsgBundleOptional = defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.GERMAN));
        Assert.assertEquals("locale string should equal", "de", soyMsgBundleOptional.get().getLocaleString());
    }

    @Test
    public void resolveCustomMessagePathGermanCacheTest() throws Exception {
        defaultSoyMsgBundleResolver.setMessagesPath("msg/messages");
        final Optional<SoyMsgBundle> soyMsgBundleOptional = defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.GERMAN));
        Assert.assertEquals("locale string should equal", "de", soyMsgBundleOptional.get().getLocaleString());
    }

    @Test
    public void defaultFallBackToEnglish() throws Exception {
        defaultSoyMsgBundleResolver.setMessagesPath("msg/messages");
        final Optional<SoyMsgBundle> soyMsgBundleOptional = defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.FRANCE));
        Assert.assertEquals("locale string should equal", "en", soyMsgBundleOptional.get().getLocaleString());
    }

    @Test
    public void fallBackToEnglish() throws Exception {
        defaultSoyMsgBundleResolver.setFallbackToEnglish(false);
        defaultSoyMsgBundleResolver.setMessagesPath("msg/messages");
        final Optional<SoyMsgBundle> soyMsgBundleOptional = defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.FRANCE));
        Assert.assertFalse("value should not be present", soyMsgBundleOptional.isPresent());
    }

    @Test
    public void defaultCacheSize() throws Exception {
        Assert.assertTrue("should be empty", defaultSoyMsgBundleResolver.msgBundles.isEmpty());
    }

    @Test
    public void cacheWorks() throws Exception {
        defaultSoyMsgBundleResolver.setMessagesPath("msg/messages");
        defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.GERMAN));
        Assert.assertEquals("cache should have one entry", 1, defaultSoyMsgBundleResolver.msgBundles.size());
    }

    @Test
    public void cacheDoesntWorkInDebugMode() throws Exception {
        defaultSoyMsgBundleResolver.setHotReloadMode(true);
        defaultSoyMsgBundleResolver.setMessagesPath("msg/messages");
        defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.GERMAN));
        Assert.assertTrue("cache should be empty", defaultSoyMsgBundleResolver.msgBundles.isEmpty());
    }

    @Test
    public void cacheDoesntWorkInDebugModeThowsNPEOnCacheAccess() throws Exception {
        try {
            defaultSoyMsgBundleResolver.setHotReloadMode(true);
            defaultSoyMsgBundleResolver.setMessagesPath("msg/messages");
            defaultSoyMsgBundleResolver.resolve(Optional.of(Locale.GERMAN));
            defaultSoyMsgBundleResolver.msgBundles = null;
        } catch (NullPointerException ex) {
            fail("should not throw NPE");
        }
    }

}
