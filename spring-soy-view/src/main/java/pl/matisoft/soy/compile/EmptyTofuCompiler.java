package pl.matisoft.soy.compile;

import com.google.common.base.Optional;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 23/06/2013
 * Time: 12:15
 */
public class EmptyTofuCompiler implements TofuCompiler {

    @Override
    public Optional<SoyTofu> compile(Collection<URL> urls) {
        return Optional.absent();
    }

    @Override
    public List<String> compileToJsSrc(URL template, @Nullable SoyMsgBundle soyMsgBundle) {
        return Collections.emptyList();
    }

}
