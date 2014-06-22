package pl.matisoft.soy.ajax.auth;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 12/10/2013
 * Time: 23:44
 *
 * A configurable implementation of AuthManager that simply takes a list of allowed
 * templates to be compiled from an internal unmodifiable list
 */
public class ConfigurableAuthManager implements AuthManager {

    /**friendly*/ ImmutableList<String> allowedTemplates = new ImmutableList.Builder<String>().build();

    public ConfigurableAuthManager(List<String> allowedTemplates) {
        this.allowedTemplates = ImmutableList.copyOf(allowedTemplates);
    }

    public ConfigurableAuthManager() {
    }

    public void setAllowedTemplates(final List<String> allowedTemplates) {
        this.allowedTemplates = ImmutableList.copyOf(allowedTemplates);
    }

    @Override
    public boolean isAllowed(final String url) {
        return allowedTemplates.contains(url);
    }

}
