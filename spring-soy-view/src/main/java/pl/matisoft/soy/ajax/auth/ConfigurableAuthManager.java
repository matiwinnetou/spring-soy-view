package pl.matisoft.soy.ajax.auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 12/10/2013
 * Time: 23:44
 */
public class ConfigurableAuthManager implements AuthManager {

    private List<String> allowedTemplates = new ArrayList<String>();

    public void setAllowedTemplates(final List<String> allowedTemplates) {
        this.allowedTemplates = allowedTemplates;
    }

    @Override
    public boolean isAllowed(final String url) {
        return allowedTemplates.contains(url);
    }

}
