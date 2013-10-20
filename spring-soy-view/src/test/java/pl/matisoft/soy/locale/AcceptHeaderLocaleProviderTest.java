package pl.matisoft.soy.locale;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import com.google.common.base.Optional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 18.10.13
 * Time: 18:54
 */
public class AcceptHeaderLocaleProviderTest {

    private AcceptHeaderLocaleProvider acceptHeaderLocaleProvider = new AcceptHeaderLocaleProvider();

    @Mock
    private HttpServletRequest request;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldThrowNPE() throws Exception {
        try {
            acceptHeaderLocaleProvider.resolveLocale(null);
            fail("npe should be thrown");
        } catch (Exception ex) {

        }
    }

    @Test
    public void shouldReturnLocale() throws Exception {
        when(request.getLocale()).thenReturn(Locale.GERMAN);
        final Optional<Locale> localeOptional = acceptHeaderLocaleProvider.resolveLocale(request);
        Assert.assertTrue("locale should be present", localeOptional.isPresent());
        Assert.assertEquals("locale should be German", Locale.GERMAN, localeOptional.get());
    }

}
