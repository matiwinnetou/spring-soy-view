package pl.matisoft.soy.compile;

import com.google.common.base.Optional;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;

import javax.annotation.Nullable;
import java.io.File;
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
    public Optional<SoyTofu> compile(Collection<File> files) {
        return Optional.absent();
    }

    @Override
    public List<String> compileToJsSrc(File template, @Nullable SoyMsgBundle soyMsgBundle) {
        return Collections.emptyList();
    }

}
