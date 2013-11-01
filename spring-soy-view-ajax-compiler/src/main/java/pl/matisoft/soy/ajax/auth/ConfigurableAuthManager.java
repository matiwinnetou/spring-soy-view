package pl.matisoft.soy.ajax.auth;

import com.google.common.collect.ImmutableList;
import pl.matisoft.soy.config.SoyViewConfig;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 12/10/2013
 * Time: 23:44
 *
 * A configurable implementation of AuthManager that simply takes a list of allowed
 * templates to be compiled from an internal unmodifiable list
 *
 * Spring XML example:<br>
 * <code>
 *   <property name="authManager">
 *     <bean class="pl.matisoft.soy.ajax.auth.ConfigurableAuthManager">
 *       <property name="allowedTemplates">
 *        <list><value>ajax_macros</value></list>
 *       </property>
 *     </bean>
 *   </property>
 * </code>
 * Spring JavaConfig example:<br>
 * <code>
 *  @Bean
 *  public AuthManager authManager() {
 *     final ConfigurableAuthManager configurableAuthManager = new ConfigurableAuthManager();
 *     configurableAuthManager.setAllowedTemplates(Lists.newArrayList("templates/client-words.soy", "templates/server-time.soy"));
 *
 *     return configurableAuthManager;
 *  }
 * </code>
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
