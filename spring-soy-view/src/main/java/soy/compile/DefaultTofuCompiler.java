package soy.compile;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;

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

    @Override
    public SoyTofu compile(final Collection<File> files) {
        final SoyFileSet.Builder sfsBuilder = new SoyFileSet.Builder();

        for (final File file : files) {
            sfsBuilder.add(file);
        }

        final SoyFileSet sfs = sfsBuilder.build();

        return sfs.compileToTofu();
    }

    @Override
    public final List<String> compileToJsSrc(final File file, SoyJsSrcOptions soyJsSrcOptions, SoyMsgBundle soyMsgBundle) {
        final SoyFileSet soyFileSet = buildSoyFileSetFrom(file);

        return soyFileSet.compileToJsSrc(soyJsSrcOptions, soyMsgBundle);
    }

    private SoyFileSet buildSoyFileSetFrom(final File templateFile) {
        SoyFileSet soyFileSet = (new SoyFileSet.Builder()).add(templateFile).build();

        return soyFileSet;
    }

}
