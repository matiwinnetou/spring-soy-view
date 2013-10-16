package pl.matisoft.soy.compile;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.tofu.SoyTofuOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.matisoft.soy.global.compile.CompileTimeGlobalModelResolver;
import pl.matisoft.soy.global.compile.EmptyCompileTimeGlobalModelResolver;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 20.06.13
 * Time: 17:40
 */
public class DefaultTofuCompiler implements TofuCompiler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTofuCompiler.class);

    private boolean debugOn = false;

    private CompileTimeGlobalModelResolver compileTimeGlobalModelResolver = new EmptyCompileTimeGlobalModelResolver();

    private SoyJsSrcOptions soyJsSrcOptions = new SoyJsSrcOptions();

    @Override
    public Optional<SoyTofu> compile(@Nullable final Collection<URL> urls) {
        Preconditions.checkNotNull("compileTimeGlobalModelResolver", compileTimeGlobalModelResolver);

        if (urls == null || urls.isEmpty()) {
            return Optional.absent();
        }
        logger.debug("SoyTofu compilation of templates:" + urls.size());
        final long time1 = System.currentTimeMillis();

        final SoyFileSet.Builder sfsBuilder = new SoyFileSet.Builder();

        for (final URL url : urls) {
            sfsBuilder.add(url);
        }

        addRuntimeGlobals(sfsBuilder);

        final SoyFileSet soyFileSet = sfsBuilder.build();

        final SoyTofuOptions soyTofuOptions = createSoyTofuOptions();
        final SoyTofu soyTofu = soyFileSet.compileToTofu(soyTofuOptions);

        final long time2 = System.currentTimeMillis();

        logger.debug("SoyTofu compilation complete." + (time2 - time1) + " ms");

        return Optional.fromNullable(soyTofu);
    }

    private void addRuntimeGlobals(final SoyFileSet.Builder sfsBuilder) {
        final Optional<SoyMapData> soyMapData = compileTimeGlobalModelResolver.resolveData();
        if (soyMapData.isPresent()) {
            final Map<String, ?> mapData = soyMapData.get().asMap();
            if (mapData.size() > 0) {
                logger.debug("Setting compile time globals, entries number:" + mapData.size());
                sfsBuilder.setCompileTimeGlobals(mapData);
            }
        }
    }

    private SoyTofuOptions createSoyTofuOptions() {
        final SoyTofuOptions soyTofuOptions = new SoyTofuOptions();
        soyTofuOptions.setUseCaching(debugOn);

        return soyTofuOptions;
    }

    @Override
    public final Optional<String> compileToJsSrc(@Nullable final URL url, @Nullable final SoyMsgBundle soyMsgBundle) {
        if (url == null) {
            return Optional.absent();
        }

        final Collection<String> compiledTemplates = compileToJsSrc(Lists.newArrayList(url), soyMsgBundle);
        if (compiledTemplates.isEmpty()) {
            return Optional.absent();
        }

        return Optional.fromNullable(compiledTemplates.iterator().next());
    }

    @Override
    public Collection<String> compileToJsSrc(Collection<URL> templates, @Nullable SoyMsgBundle soyMsgBundle) {
        Preconditions.checkNotNull("soyJsSrcOptions", soyJsSrcOptions);
        logger.debug("SoyJavaScript compilation of template:" + templates);
        final long time1 = System.currentTimeMillis();

        final SoyFileSet soyFileSet = buildSoyFileSetFrom(templates);

        final List<String> ret = soyFileSet.compileToJsSrc(soyJsSrcOptions, soyMsgBundle);

        final long time2 = System.currentTimeMillis();

        logger.debug("SoyJavaScript compilation complete." + (time2 - time1) + " ms");

        return ret;
    }

    private SoyFileSet buildSoyFileSetFrom(final Collection<URL> urls) {
        final SoyFileSet.Builder builder = new SoyFileSet.Builder();

        for (final URL url : urls) {
            builder.setAllowExternalCalls(true);
            builder.add(url);
        }

        addRuntimeGlobals(builder);

        return builder.build();
    }

    public void setDebugOn(final boolean debugOn) {
        this.debugOn = debugOn;
    }

    public void setCompileTimeGlobalModelResolver(final CompileTimeGlobalModelResolver compileTimeGlobalModelResolver) {
        this.compileTimeGlobalModelResolver = compileTimeGlobalModelResolver;
    }

    public void setSoyJsSrcOptions(final SoyJsSrcOptions soyJsSrcOptions) {
        this.soyJsSrcOptions = soyJsSrcOptions;
    }

}
