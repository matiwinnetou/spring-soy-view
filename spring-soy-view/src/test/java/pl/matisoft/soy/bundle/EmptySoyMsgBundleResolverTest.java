package pl.matisoft.soy.bundle;

import java.util.Locale;

import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 23.10.13
 * Time: 11:24
 */
public class EmptySoyMsgBundleResolverTest {

    private EmptySoyMsgBundleResolver emptySoyMsgBundleResolver = new EmptySoyMsgBundleResolver();

    @Test
    public void defaultNull() throws Exception {
        Assert.assertFalse(emptySoyMsgBundleResolver.resolve(null).isPresent());
    }

    @Test
    public void defaultAbsent() throws Exception {
        Assert.assertFalse(emptySoyMsgBundleResolver.resolve(Optional.<Locale>absent()).isPresent());
    }

    @Test
    public void localePassedIn() throws Exception {
        Assert.assertFalse(emptySoyMsgBundleResolver.resolve(Optional.of(Locale.GERMANY)).isPresent());
    }

}
