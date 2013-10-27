package pl.matisoft.soy.ajax.url;

import java.io.IOException;
import java.util.Collection;

import com.google.common.base.Optional;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 03.07.13
 * Time: 20:16
 */
public interface TemplateUrlComposer {

    Optional<String> compose(String soyTemplateFileName) throws IOException;

    Optional<String> compose(Collection<String> soyTemplateFileNames) throws IOException;

}
