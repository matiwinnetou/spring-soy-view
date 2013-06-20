package soy;

import com.google.template.soy.tofu.SoyTofu;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SoyViewConfig config;

    private SoyTofu compiledTemplates;

    public SoyTemplateViewResolver() {
        super();
        setExposeSpringMacroHelpers(false);
    }

    @Override
    protected void initApplicationContext() {
        super.initApplicationContext();
        compiledTemplates = compileTemplates();
    }

    @Override
    protected Class<?> getViewClass() {
        return SoyView.class;
    }

    private boolean shouldCompileAgain() {
        if (isCache()) return false;
        if (!config.isDebugOn()) return false;

        return true;
    }

    @Override
    protected AbstractUrlBasedView buildView(final String viewName) throws Exception {
        SoyUtils.checkSoyViewConfig(config);
        final SoyView view = (SoyView) super.buildView(viewName);
        view.setTemplateName(viewName);

        if (shouldCompileAgain()) {
            view.setCompiledTemplates(compileTemplates());
        } else {
            if (compiledTemplates == null) {
                this.compiledTemplates = compileTemplates();
            }

            view.setCompiledTemplates(compiledTemplates);
        }

        view.setSoyViewConfig(config);

        return view;
    }

    private SoyTofu compileTemplates() {
        System.out.println("SoyTofu compilation of all templates...");
        final long time1 = System.currentTimeMillis();
        final TemplateFilesResolver templateFilesResolver = config.getTemplateFilesResolver();
        final Collection<File> templateFiles = templateFilesResolver.resolve();
        final TofuCompiler tofuCompiler = config.getTofuCompiler();

        final SoyTofu soyTofu = tofuCompiler.compile(templateFiles);

        final long time2 = System.currentTimeMillis();

        System.out.println("SoyTofu compilation complete." + (time2 - time1) + " ms");

        return soyTofu;
    }

    public void setConfig(final SoyViewConfig config) {
        this.config = config;
    }

}
