package soy.template;

import java.io.File;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 20.06.13
 * Time: 17:43
 */
public interface TemplateFilesResolver {

    Collection<File> resolve();

}
