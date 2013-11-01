package pl.matisoft.soy.global.runtime;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.template.soy.data.SoyMapData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.matisoft.soy.global.runtime.resolvers.RuntimeResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 14/07/2013
 * Time: 16:46
 *
 * A default implementation of GlobalModelResolver contains a list of resolvers,
 * each resolver can decide what data should be injected.
 */
public class DefaultGlobalModelResolver implements GlobalModelResolver {

    private static final Logger logger = LoggerFactory.getLogger(DefaultGlobalModelResolver.class);

    private List<RuntimeResolver> resolvers = Lists.newArrayList();

    @Override
    public Optional<SoyMapData> resolveData(final HttpServletRequest request, final HttpServletResponse response, final Map<String, ? extends Object> model) {
        final SoyMapData root = new SoyMapData();

        for (final RuntimeResolver runtimeResolver : resolvers) {
            logger.debug("resolving:{}", runtimeResolver);
            runtimeResolver.resolveData(request, response, model, root);
        }

        return Optional.of(root);
    }

    public void setResolvers(List<RuntimeResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public List<RuntimeResolver> getResolvers() {
        return resolvers;
    }

}
