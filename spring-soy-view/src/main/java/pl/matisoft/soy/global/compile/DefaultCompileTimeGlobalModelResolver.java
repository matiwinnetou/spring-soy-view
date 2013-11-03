package pl.matisoft.soy.global.compile;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 21/06/2013
 * Time: 23:06
 *
 * A default implementation, which delegates to a map
 *
 * Example usage (Java Config):
 * <code>
 * @Bean
 * public CompileTimeGlobalModelResolver compileTimeGlobalModelResolver() {
 *    final Map<String,String> data = new HashMap<String,String>();
 *    data.put("site.url", "http://www.bbc.co.uk");
 *    final DefaultCompileTimeGlobalModelResolver resolver = new DefaultCompileTimeGlobalModelResolver();
 *    resolver.setData(data);
 *
 *    return resolver;
 * }
 * </code>
 */
public class DefaultCompileTimeGlobalModelResolver implements CompileTimeGlobalModelResolver {

    private Map data;

    @Override
    public Optional<SoyMapData> resolveData() {
        if (data == null || data.isEmpty()) {
            return Optional.absent();
        }

        return Optional.of(new SoyMapData(data));
    }

    public void setData(final Map data) {
        this.data = data;
    }

    public void setProperties(final Properties properties) {
        data = new HashMap();
        for (final String propertyName : properties.stringPropertyNames()) {
            data.put(propertyName, properties.getProperty(propertyName));
        }
    }

}
