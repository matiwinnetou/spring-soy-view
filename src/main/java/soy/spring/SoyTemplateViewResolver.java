package soy.spring;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import soy.config.SoyViewConfig;

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
    private SoyViewConfig soyViewConfig;

    private SoyTofu compiledTemplates;

    public SoyTemplateViewResolver() {
        super();
        setExposeSpringMacroHelpers(false);
    }

    @Override
    protected void initApplicationContext() {
        super.initApplicationContext();
        if (isCache()) {
            compiledTemplates = compileTemplates();
        }
    }

    @Override
    protected Class<?> getViewClass() {
        return SoyView.class;
    }

    public void setSoyViewConfig(final SoyViewConfig soyViewConfig) {
        this.soyViewConfig = soyViewConfig;
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        checkSoyViewConfig();
        final SoyView view = (SoyView) super.buildView(viewName);
        view.setTemplateName(viewName);

        if (isCache()) {
            view.setCompiledTemplates(compiledTemplates);
        } else {
            view.setCompiledTemplates(compileTemplates());
        }

        view.setSoyViewConfig(soyViewConfig);

        return view;
    }

    private SoyTofu compileTemplates() {
        final Collection<File> templateFiles = soyViewConfig.getTemplateFilesResolver().resolve();

        final SoyFileSet.Builder fileSetBuilder = new SoyFileSet.Builder();
        for (final File templateFile : templateFiles) {
            fileSetBuilder.add(templateFile);
        }

        final SoyFileSet soyFileSet = fileSetBuilder.build();

        return soyFileSet.compileToTofu();
    }

    private void checkSoyViewConfig() {
        if (soyViewConfig == null) {
            throw new RuntimeException("SoyViewConfig needs to be specified for SoyTemplateViewResolver");
        }
    }

}
