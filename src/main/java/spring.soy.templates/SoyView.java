package spring.soy.templates;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractTemplateView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 19/06/2013
 * Time: 23:32
 */
public class SoyView extends AbstractTemplateView {

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public void render(final Map<String, ?> stringMap, final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws Exception {

    }

    private SoyTofu compiledTemplates;
    private String templateName;

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model,
                                             HttpServletRequest request, HttpServletResponse response) throws Exception {

        String rendition = compiledTemplates.render(templateName, model, null);
        response.getWriter().write(rendition);
    }

    public void setCompiledTemplates(SoyTofu compiledTemplates) {
        this.compiledTemplates = compiledTemplates;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

}
