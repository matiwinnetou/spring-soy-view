package pl.matisoft.soy.locale;

import javax.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 18.10.13
 * Time: 19:03
 */
public class DefaultLocaleProviderTest {

    private DefaultLocaleProvider defaultLocaleProvider = new DefaultLocaleProvider();

    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testShouldNotReturnNull() throws Exception {
        Assert.assertTrue("should return value", defaultLocaleProvider.resolveLocale(null).isPresent());
    }

    @Test
    public void shouldReturnUsDefault() throws Exception {
        Assert.assertEquals("should return absent", Locale.US, defaultLocaleProvider.resolveLocale(request).get());
    }

}
