package pl.matisoft.soy.ajax.auth;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 12/10/2013
 * Time: 23:39
 *
 * An implementation that allows all urls to be compiled from soy source to JavaScript
 */
public class PermissableAuthManager implements AuthManager {

    @Override
    public boolean isAllowed(String url) {
        return true;
    }

}
