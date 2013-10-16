package pl.matisoft.soy.ajax.auth;

import java.util.ArrayList;
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
 *     configurableAuthManager.setAllowedTemplates(Lists.newArrayList("client-words", "server-time"));
 *
 *     return configurableAuthManager;
 *  }
 * </code>
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
