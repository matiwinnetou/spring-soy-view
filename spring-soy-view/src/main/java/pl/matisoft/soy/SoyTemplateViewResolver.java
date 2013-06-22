package pl.matisoft.soy;

import com.google.common.base.Optional;
import com.google.template.soy.tofu.SoyTofu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import pl.matisoft.soy.compile.TofuCompiler;
import pl.matisoft.soy.config.SoyViewConfig;
import pl.matisoft.soy.template.TemplateFilesResolver;

import java.io.File;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 19:51
 */
public class SoyTemplateViewResolver extends AbstractTemplateViewResolver {

    private static final Logger logger = LoggerFactory.getLogger(SoyTemplateViewResolver.class);

    private SoyViewConfig config;

    private Optional<SoyTofu> compiledTemplates;

    public SoyTemplateViewResolver() {
        super();
        setExposeSpringMacroHelpers(false);
    }

    @Override
    protected void initApplicationContext() {
        super.initApplicationContext();
        if (isCache()) {
            this.compiledTemplates = compileTemplates("<class_init>");
        }
    }

    @Override
    protected Class<?> getViewClass() {
        return SoyView.class;
    }

    @Override
    protected AbstractUrlBasedView buildView(final String viewName) throws Exception {
        SoyUtils.checkSoyViewConfig(config);
        final SoyView view = (SoyView) super.buildView(viewName);
        view.setTemplateName(viewName);
        view.setContentType(contentType());
        view.setConfig(config);

        if (!compiledTemplates.isPresent()) {
            //if (!viewName.endsWith(".html")) { //strange we get extra call for a page itself, investigate more
                view.setCompiledTemplates(compileTemplates(viewName));
            //}
            return view;
        }

        if (isCache() && compiledTemplates.isPresent()) { //extra sanity check if compiled templates are available
            view.setCompiledTemplates(compiledTemplates);
        }

        return view;
    }

    private String contentType() {
        String encoding = config.getEncoding();
        if (encoding == null) {
            encoding = "utf-8";
        }

        return "text/html; charset=" + encoding;
    }

    private Optional<SoyTofu> compileTemplates(final String viewName) {
        logger.debug("Compile all templates, initBy: " + viewName);
        final TemplateFilesResolver templateFilesResolver = config.getTemplateFilesResolver();
        final Collection<File> templateFiles = templateFilesResolver.resolve();
        if (templateFiles != null && templateFiles.size() > 0) {
            final TofuCompiler tofuCompiler = config.getTofuCompiler();

            return tofuCompiler.compile(templateFiles);
        }

        return Optional.absent();
    }

    public void setConfig(final SoyViewConfig config) {
        this.config = config;
    }

}
