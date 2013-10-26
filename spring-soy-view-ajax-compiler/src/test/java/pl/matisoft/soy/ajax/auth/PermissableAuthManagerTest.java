package pl.matisoft.soy.ajax.auth;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 18.10.13
 * Time: 19:13
 */
public class PermissableAuthManagerTest {

    private PermissableAuthManager permissableAuthManager = new PermissableAuthManager();

    @Test
    public void testDefault() throws Exception {
        Assert.assertTrue("should allow all", permissableAuthManager.isAllowed("template"));
    }

}
