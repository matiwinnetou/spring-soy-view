package pl.matisoft.soy.global.runtime;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.template.soy.data.SoyMapData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.matisoft.soy.global.runtime.resolvers.RuntimeDataResolver;

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
public class DefaultGlobalRuntimeModelResolver implements GlobalRuntimeModelResolver {

    private static final Logger logger = LoggerFactory.getLogger(DefaultGlobalRuntimeModelResolver.class);

    private List<RuntimeDataResolver> resolvers = Lists.newArrayList();

    private List<RuntimeDataResolver> userResolvers = Lists.newArrayList();

    public DefaultGlobalRuntimeModelResolver(List<RuntimeDataResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public DefaultGlobalRuntimeModelResolver(List<RuntimeDataResolver> resolvers, List<RuntimeDataResolver> userResolvers) {
        this.resolvers = resolvers;
        this.userResolvers = userResolvers;
    }

    public DefaultGlobalRuntimeModelResolver() {
    }

    @Override
    public Optional<SoyMapData> resolveData(final HttpServletRequest request, final HttpServletResponse response, final Map<String, ? extends Object> model) {
        final SoyMapData root = new SoyMapData();

        for (final RuntimeDataResolver runtimeDataResolver : resolvers) {
            logger.debug("resolving:{}", runtimeDataResolver);
            runtimeDataResolver.resolveData(request, response, model, root);
        }

        for (final RuntimeDataResolver runtimeDataResolver : userResolvers) {
            logger.debug("user data resolving:{}", runtimeDataResolver);
            runtimeDataResolver.resolveData(request, response, model, root);
        }

        return Optional.of(root);
    }

    public void setResolvers(List<RuntimeDataResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public List<RuntimeDataResolver> getResolvers() {
        return resolvers;
    }

    public List<RuntimeDataResolver> getUserResolvers() {
        return userResolvers;
    }

    public void setUserResolvers(List<RuntimeDataResolver> userResolvers) {
        this.userResolvers = userResolvers;
    }

}
