package pl.matisoft.soy.ajax.auth;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 12/10/2013
 * Time: 23:38
 *
 * AuthManager is an interface, which is responsible for controlling which templates are allowed to be compiled
 * to JavaScript
 */
public interface AuthManager {

    boolean isAllowed(String url);

}
