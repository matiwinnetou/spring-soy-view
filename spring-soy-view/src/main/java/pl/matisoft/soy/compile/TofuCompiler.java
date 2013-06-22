package pl.matisoft.soy.compile;

import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 20.06.13
 * Time: 17:39
 */
public interface TofuCompiler {

    SoyTofu compile(Collection<File> files);

    List<String> compileToJsSrc(File template, SoyMsgBundle soyMsgBundle);

}
