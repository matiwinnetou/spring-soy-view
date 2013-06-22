package soy.compile;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 20.06.13
 * Time: 17:40
 */
public class DefaultTofuCompiler implements TofuCompiler {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTofuCompiler.class);

    @Override
    public SoyTofu compile(final Collection<File> files) {
        logger.info("SoyTofu compilation of all templates:" + files.size());
        final long time1 = System.currentTimeMillis();

        final SoyFileSet.Builder sfsBuilder = new SoyFileSet.Builder();

        for (final File file : files) {
            sfsBuilder.add(file);
        }

        final SoyFileSet soyFileSet = sfsBuilder.build();
        final SoyTofu soyTofu = soyFileSet.compileToTofu();

        final long time2 = System.currentTimeMillis();

        logger.info("SoyTofu compilation complete." + (time2 - time1) + " ms");

        return soyTofu;
    }

    @Override
    public final List<String> compileToJsSrc(final File file, final SoyJsSrcOptions soyJsSrcOptions, final SoyMsgBundle soyMsgBundle) {
        final SoyFileSet soyFileSet = buildSoyFileSetFrom(file);

        return soyFileSet.compileToJsSrc(soyJsSrcOptions, soyMsgBundle);
    }

    private SoyFileSet buildSoyFileSetFrom(final File templateFile) {
        return new SoyFileSet.Builder()
                .add(templateFile)
                .build();
    }

}
