package pl.matisoft.soy.compile;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import com.google.common.base.Optional;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;

/**
 * The SoyTofu binary compiler that based on
 * set of files creates a compiled object.
 *
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 20.06.13
 * Time: 17:39
 */
public interface TofuCompiler {

    /**
     * Obtains a binary compiled version based on a set of input urls
     *
     *
     * @param files
     * @return
     */
    SoyTofu compile(Collection<URL> files) throws IOException;

    /**
     * Obtains a compiled template to JavaScript as a String based on a template url
     * @param template
     * @param soyMsgBundle
     * @return - an optional with a compiled JavaScript or absent in case of an issue
     */
    Optional<String> compileToJsSrc(URL template, @Nullable SoyMsgBundle soyMsgBundle);

    /**
     * Obtains a compiled template to JavaScript as a String based on a collection of template urls

     * @param templates - a collection of template urls
     * @param soyMsgBundle
     * @return - collection containing string with compiled templates.
     */
    Collection<String> compileToJsSrc(Collection<URL> templates, @Nullable SoyMsgBundle soyMsgBundle);

}
