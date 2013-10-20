package pl.matisoft.soy.ajax.auth;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 18.10.13
 * Time: 19:14
 */
public class ConfigurableAuthManagerTest {

    private ConfigurableAuthManager configurableAuthManager = new ConfigurableAuthManager();

    @Test
    public void testAllowedTemplatesListNonNull() throws Exception {
        Assert.assertNotNull("should not allow by default", configurableAuthManager.allowedTemplates);
    }

    @Test
    public void testAllowedTemplatesEmpty() throws Exception {
        Assert.assertTrue("should be empty", configurableAuthManager.allowedTemplates.isEmpty());
    }

    @Test
    public void testDefault() throws Exception {
        Assert.assertFalse("should not allow by default", configurableAuthManager.isAllowed("template"));
    }

    @Test
    public void testAllow() throws Exception {
        configurableAuthManager.setAllowedTemplates(Lists.newArrayList("template"));
        Assert.assertTrue("should allow", configurableAuthManager.isAllowed("template"));
    }

}
