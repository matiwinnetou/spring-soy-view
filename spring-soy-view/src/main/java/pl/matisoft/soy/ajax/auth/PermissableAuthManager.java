package pl.matisoft.soy.ajax.auth;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 12/10/2013
 * Time: 23:39
 */
public class PermissableAuthManager implements AuthManager {

    @Override
    public boolean isAllowed(String url) {
        return true;
    }

}
