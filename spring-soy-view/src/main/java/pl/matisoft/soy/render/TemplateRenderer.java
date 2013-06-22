package pl.matisoft.soy.render;

import com.google.template.soy.tofu.SoyTofu;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 22/06/2013
 * Time: 17:00
 */
public interface TemplateRenderer {

    String render(final SoyTofu compiledTemplates, final String templateName, final HttpServletRequest request, final Object model) throws Exception;

}
