package soy;

import com.google.common.base.Optional;
import com.google.template.soy.tofu.SoyTofu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import soy.compile.TofuCompiler;
import soy.config.SoyViewConfig;
import soy.template.TemplateFilesResolver;

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

    private SoyTofu compiledTemplates;

    public SoyTemplateViewResolver() {
        super();
        setExposeSpringMacroHelpers(false);
    }

    @Override
    protected void initApplicationContext() {
        super.initApplicationContext();
        if (isCache()) {
            this.compiledTemplates = compileTemplates("<class_init>").orNull();
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

        if (isCache()) {
            view.setCompiledTemplates(compiledTemplates);
        } else {
            if (!viewName.endsWith(".html")) { //????
                view.setCompiledTemplates(compileTemplates(viewName).orNull());
            }
        }

        view.setConfig(config);

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

            return Optional.fromNullable(tofuCompiler.compile(templateFiles));
        }

        return Optional.absent();
    }

    public void setConfig(final SoyViewConfig config) {
        this.config = config;
    }

}
