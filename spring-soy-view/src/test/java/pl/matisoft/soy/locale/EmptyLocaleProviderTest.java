package pl.matisoft.soy.locale;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 18.10.13
 * Time: 19:01
 */
public class EmptyLocaleProviderTest {

    private EmptyLocaleProvider emptyLocaleProvider = new EmptyLocaleProvider();

    @Test
    public void testShouldNotReturnNull() throws Exception {
        Assert.assertNotNull(emptyLocaleProvider.resolveLocale(null));
    }

    @Test
    public void testDefaultAbsent() throws Exception {
        Assert.assertFalse("locale should be absent", emptyLocaleProvider.resolveLocale(null).isPresent());
    }

}
