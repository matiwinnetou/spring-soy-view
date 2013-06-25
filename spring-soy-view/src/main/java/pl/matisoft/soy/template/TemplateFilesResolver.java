package pl.matisoft.soy.template;

import com.google.common.base.Optional;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 20.06.13
 * Time: 17:43
 */
public interface TemplateFilesResolver {

    Collection<URL> resolve() throws IOException;

    Optional<URL> resolve(String templateName) throws IOException;

}
