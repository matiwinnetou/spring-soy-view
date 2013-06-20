package soy.compile;

import com.google.template.soy.tofu.SoyTofu;

import java.io.File;
import java.net.URL;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 20.06.13
 * Time: 17:39
 */
public interface TofuCompiler {

    SoyTofu compile(Collection<File> files);

}
