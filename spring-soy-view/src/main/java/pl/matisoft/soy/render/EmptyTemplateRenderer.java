package pl.matisoft.soy.render;

import com.google.common.base.Optional;
import com.google.template.soy.tofu.SoyTofu;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 23/06/2013
 * Time: 12:20
 */
public class EmptyTemplateRenderer implements TemplateRenderer {

    @Override
    public Optional<String> render(Optional<SoyTofu> compiledTemplates, String templateName, HttpServletRequest request, Object model) throws Exception {
        return Optional.absent();
    }

}
