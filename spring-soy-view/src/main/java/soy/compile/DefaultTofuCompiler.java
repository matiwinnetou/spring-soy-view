package soy.compile;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;
import com.google.template.soy.tofu.SoyTofuOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soy.config.AbstractSoyConfigEnabled;
import soy.SoyUtils;
import soy.config.SoyViewConfig;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 20.06.13
 * Time: 17:40
 */
public class DefaultTofuCompiler extends AbstractSoyConfigEnabled implements TofuCompiler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTofuCompiler.class);

    @Override
    public SoyTofu compile(final Collection<File> files) {
        SoyUtils.checkSoyViewConfig(config);
        logger.info("SoyTofu compilation of templates:" + files.size());
        final long time1 = System.currentTimeMillis();

        final SoyFileSet.Builder sfsBuilder = new SoyFileSet.Builder();

        for (final File file : files) {
            sfsBuilder.add(file);
        }

        final SoyFileSet soyFileSet = sfsBuilder.build();

        final SoyTofuOptions soyTofuOptions = createSoyTofuOptions(config);
        final SoyTofu soyTofu = soyFileSet.compileToTofu(soyTofuOptions);

        final long time2 = System.currentTimeMillis();

        logger.info("SoyTofu compilation complete." + (time2 - time1) + " ms");

        return soyTofu;
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
        return new SoyFileSet.Builder()
                .add(templateFile)
                .build();
    }

}
