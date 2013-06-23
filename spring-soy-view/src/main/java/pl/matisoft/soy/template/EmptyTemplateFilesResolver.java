package pl.matisoft.soy.template;

import com.google.common.base.Optional;

import java.io.File;
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
    public Collection<File> resolve() {
        return Collections.emptyList();
    }

    @Override
    public Optional<File> resolve(String templateName) {
        return Optional.absent();
    }

}
