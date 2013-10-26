package pl.matisoft.soy.ajax.url;

import com.google.common.base.Optional;

import java.io.IOException;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 03.07.13
 * Time: 20:16
 */
public interface TemplateUrlComposer {

    Optional<String> compose(final String soyTemplateFileName) throws IOException;

    Optional<String> compose(final Collection<String> soyTemplateFileNames) throws IOException;

}
