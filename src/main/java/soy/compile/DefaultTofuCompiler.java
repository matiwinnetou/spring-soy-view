package soy.compile;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;

import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 20.06.13
 * Time: 17:40
 */
public class DefaultTofuCompiler implements TofuCompiler {

    @Override
    public SoyTofu compile(final URL[] urls) {
        final SoyFileSet.Builder sfsBuilder = new SoyFileSet.Builder();

        for (final URL url : urls) {
            sfsBuilder.add(url);
        }

        final SoyFileSet sfs = sfsBuilder.build();

        return sfs.compileToTofu();
    }

}
