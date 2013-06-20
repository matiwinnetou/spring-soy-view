package soy.compile;

import com.google.template.soy.tofu.SoyTofu;

import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 20.06.13
 * Time: 17:39
 */
public interface TofuCompiler {

    SoyTofu compile(URL[] urls);

}
