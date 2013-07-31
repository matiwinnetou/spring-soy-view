package pl.matisoft.soy.template;

import com.google.common.base.Optional;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 23/06/2013
 * Time: 12:14
 */
public class EmptyTemplateFilesResolver implements TemplateFilesResolver {

    @Override
    public Collection<URL> resolve() throws IOException {
        return Collections.emptyList();
    }

    @Override
    public Optional<URL> resolve(final String templateName) throws IOException {
        return Optional.absent();
    }

}
