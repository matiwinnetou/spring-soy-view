package pl.matisoft.soy.compile;

import com.google.common.base.Optional;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;

import javax.annotation.Nullable;
import java.net.URL;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 20.06.13
 * Time: 17:39
 */
public interface TofuCompiler {

    Optional<SoyTofu> compile(Collection<URL> files);

    List<String> compileToJsSrc(URL template, @Nullable SoyMsgBundle soyMsgBundle);

}
