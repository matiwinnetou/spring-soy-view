package pl.matisoft.soy.compile;

import com.google.common.base.Optional;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.tofu.SoyTofuOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.matisoft.soy.SoyUtils;
import pl.matisoft.soy.config.AbstractSoyConfigEnabled;
import pl.matisoft.soy.config.SoyViewConfig;
import pl.matisoft.soy.global.compile.CompileTimeGlobalModelResolver;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 20.06.13
 * Time: 17:40
 */
public class DefaultTofuCompiler extends AbstractSoyConfigEnabled implements TofuCompiler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTofuCompiler.class);

    @Override
    public Optional<SoyTofu> compile(@Nullable final Collection<File> files) {
        if (files == null || files.isEmpty()) {
            return Optional.absent();
        }
        SoyUtils.checkSoyViewConfig(config);
        logger.debug("SoyTofu compilation of templates:" + files.size());
        final long time1 = System.currentTimeMillis();

        final SoyFileSet.Builder sfsBuilder = new SoyFileSet.Builder();

        for (final File file : files) {
            sfsBuilder.add(file);
        }

        final SoyFileSet soyFileSet = sfsBuilder.build();

        final SoyTofuOptions soyTofuOptions = createSoyTofuOptions(config);
        final SoyTofu soyTofu = soyFileSet.compileToTofu(soyTofuOptions);

        final long time2 = System.currentTimeMillis();

        logger.debug("SoyTofu compilation complete." + (time2 - time1) + " ms");

        return Optional.fromNullable(soyTofu);
    }

    private SoyTofuOptions createSoyTofuOptions(final SoyViewConfig config) {
        final SoyTofuOptions soyTofuOptions = new SoyTofuOptions();
        soyTofuOptions.setUseCaching(!config.isDebugOn());

        return soyTofuOptions;
    }

    @Override
    public final List<String> compileToJsSrc(final File file, final SoyMsgBundle soyMsgBundle) {
        SoyUtils.checkSoyViewConfig(config);
        logger.debug("SoyJavaScript compilation of template:" + file);
        final long time1 = System.currentTimeMillis();

        final SoyFileSet soyFileSet = buildSoyFileSetFrom(file);

        final List<String> ret = soyFileSet.compileToJsSrc(config.getJsSrcOptions(), soyMsgBundle);

        final long time2 = System.currentTimeMillis();

        logger.debug("SoyJavaScript compilation complete." + (time2 - time1) + " ms");

        return ret;
    }

    private SoyFileSet buildSoyFileSetFrom(final File templateFile) {
        final SoyFileSet.Builder builder = new SoyFileSet.Builder();
        builder.setAllowExternalCalls(true);
        builder.add(templateFile);

        final CompileTimeGlobalModelResolver compileTimeGlobalModelResolver = config.getCompileTimeGlobalModelResolver();
        final Optional<SoyMapData> soyMapData = compileTimeGlobalModelResolver.resolveData();

        if (soyMapData.isPresent()) {
            final Map<String, ?> mapData = soyMapData.get().asMap();
            if (mapData.size() > 0) {
                logger.debug("Setting compile time globals, entries number:" + mapData.size());
                builder.setCompileTimeGlobals(mapData);
            }
        }

        return builder.build();
    }

}
