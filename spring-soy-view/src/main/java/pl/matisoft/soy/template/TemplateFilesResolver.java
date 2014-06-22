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
 *
 * An interface which provides a collection of urls that are soy files.
 */
public interface TemplateFilesResolver {

    /**
     * Iterate over all files and provide urls of files pointing to soy template files
     * @return urls
     * @throws java.io.IOException - i/o error
     */
    Collection<URL> resolve() throws IOException;

    /**
     * Iterate over all files and provide a matching url for a template passed in as a parameter
     * @param templateName - name of template
     * @return urls
     * @throws java.io.IOException - i/o error
     */
    Optional<URL> resolve(String templateName) throws IOException;

}
