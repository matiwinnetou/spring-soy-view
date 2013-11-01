package pl.matisoft.soy.compile;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.Collection;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 23/06/2013
 * Time: 12:15
 *
 * An empty implementation of TofuCompiler, which normally should never be used
 * but it is an implementation of Null Object Pattern, which may prevent an NPE
 */
public class EmptyTofuCompiler implements TofuCompiler {

    @Override
    public SoyTofu compile(final Collection<URL> urls) {
        return null;
    }

    @Override
    public Optional<String> compileToJsSrc(final URL template, @Nullable SoyMsgBundle soyMsgBundle) {
        return Optional.absent();
    }

    @Override
    public Collection<String> compileToJsSrc(Collection<URL> templates, @Nullable SoyMsgBundle soyMsgBundle) {
        return Lists.newArrayList();
    }

}
