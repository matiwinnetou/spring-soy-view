package pl.matisoft.soy;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.template.soy.tofu.SoyTofu;
import org.springframework.web.servlet.view.AbstractTemplateView;
import pl.matisoft.soy.render.DefaultTemplateRenderer;
import pl.matisoft.soy.render.TemplateRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 19/06/2013
 * Time: 23:32
 */
public class SoyView extends AbstractTemplateView {

    private Optional<SoyTofu> compiledTemplates = Optional.absent();

    private String templateName;

    private TemplateRenderer templateRenderer = new DefaultTemplateRenderer();

    public SoyView() {
    }

    @Override
    protected void renderMergedTemplateModel(final Map<String, Object> model,
                                             final HttpServletRequest request,
                                             final HttpServletResponse response) throws Exception {
        Preconditions.checkNotNull(templateName, "templateName cannot be null");
        Preconditions.checkNotNull(templateRenderer, "templateRenderer cannot be null");
        final Writer writer = response.getWriter();

        if (!compiledTemplates.isPresent()) {
            throw new RuntimeException("Unable to render - compiled templates are empty!");
        }

        final Optional<String> renderedTemplate = templateRenderer.render(compiledTemplates, templateName, request, model);
        if (renderedTemplate.isPresent()) {
            writer.write(renderedTemplate.get());
            writer.flush();
        }
    }

    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    public void setCompiledTemplates(final Optional<SoyTofu> compiledTemplates) {
        this.compiledTemplates = compiledTemplates;
    }

    public void setTemplateRenderer(final TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

}
