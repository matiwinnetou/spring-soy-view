package pl.matisoft.soy;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.template.soy.tofu.SoyTofu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import pl.matisoft.soy.bundle.EmptySoyMsgBundleResolver;
import pl.matisoft.soy.bundle.SoyMsgBundleResolver;
import pl.matisoft.soy.compile.DefaultTofuCompiler;
import pl.matisoft.soy.compile.TofuCompiler;
import pl.matisoft.soy.data.adjust.EmptyModelAdjuster;
import pl.matisoft.soy.data.adjust.ModelAdjuster;
import pl.matisoft.soy.global.EmptyGlobalModelResolver;
import pl.matisoft.soy.global.GlobalModelResolver;
import pl.matisoft.soy.locale.EmptyLocaleProvider;
import pl.matisoft.soy.locale.LocaleProvider;
import pl.matisoft.soy.render.EmptyTemplateRenderer;
import pl.matisoft.soy.render.TemplateRenderer;
import pl.matisoft.soy.template.EmptyTemplateFilesResolver;
import pl.matisoft.soy.template.TemplateFilesResolver;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 19:51
 */
public class SoyTemplateViewResolver extends AbstractTemplateViewResolver {

    private static final Logger logger = LoggerFactory.getLogger(SoyTemplateViewResolver.class);

    protected Optional<SoyTofu> compiledTemplates = Optional.absent();

    protected TemplateRenderer templateRenderer = new EmptyTemplateRenderer();

    protected TemplateFilesResolver templateFilesResolver = new EmptyTemplateFilesResolver();

    protected TofuCompiler tofuCompiler = new DefaultTofuCompiler();

    protected ModelAdjuster modelAdjuster = new EmptyModelAdjuster();

    protected GlobalModelResolver globalModelResolver = new EmptyGlobalModelResolver();

    protected LocaleProvider localeProvider = new EmptyLocaleProvider();

    protected SoyMsgBundleResolver soyMsgBundleResolver = new EmptySoyMsgBundleResolver();

    private String encoding = "utf-8";

    private boolean ignoreHtmlView = false;

    public SoyTemplateViewResolver() {
        super();
        setExposeSpringMacroHelpers(false);
    }

    @Override
    protected void initApplicationContext() {
        super.initApplicationContext();
        if (isCache()) {
            try {
                this.compiledTemplates = compileTemplates("<class_init>");
            } catch (IOException ex) {
                logger.error("Unable to compile template files", ex);
            }
        }
    }

    @Override
    protected Class<?> getViewClass() {
        return SoyView.class;
    }

    @Override
    protected AbstractUrlBasedView buildView(final String viewName) throws Exception {
        Preconditions.checkNotNull(viewName, "viewName cannot be null!");
        Preconditions.checkNotNull(templateRenderer, "templateRenderer cannot be null!");

        final SoyView view = (SoyView) super.buildView(viewName);
        view.setTemplateName(viewName);
        view.setContentType(contentType());
        view.setTemplateRenderer(templateRenderer);
        view.setModelAdjuster(modelAdjuster);
        view.setGlobalModelResolver(globalModelResolver);
        view.setLocaleProvider(localeProvider);
        view.setSoyMsgBundleResolver(soyMsgBundleResolver);

        if (!compiledTemplates.isPresent()) {
            if (!isHtmlView(viewName)) {
                view.setCompiledTemplates(compileTemplates(viewName));
            }

            return view;
        }

        if (isCache() && compiledTemplates.isPresent()) { //extra sanity check if compiled templates are available
            view.setCompiledTemplates(compiledTemplates);
        }

        return view;
    }

    private String contentType() {
        return "text/html; charset=" + encoding;
    }

    private boolean isHtmlView(final String viewName) {
        return ignoreHtmlView && viewName.endsWith(".html");
    }

    private Optional<SoyTofu> compileTemplates(final String viewName) throws IOException {
        Preconditions.checkNotNull(templateFilesResolver, "templatesRenderer cannot be null!");
        Preconditions.checkNotNull(tofuCompiler, "tofuCompiler cannot be null!");

        logger.debug("Compile all templates, initBy: " + viewName);
        final Collection<URL> templateFiles = templateFilesResolver.resolve();
        if (templateFiles != null && templateFiles.size() > 0) {
            return tofuCompiler.compile(templateFiles);
        }

        return Optional.absent();
    }

    public void setTemplateRenderer(final TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public void setTemplateFilesResolver(final TemplateFilesResolver templateFilesResolver) {
        this.templateFilesResolver = templateFilesResolver;
    }

    public void setTofuCompiler(final TofuCompiler tofuCompiler) {
        this.tofuCompiler = tofuCompiler;
    }

    public void setDebugOn(final boolean debugOn) {
        setCache(!debugOn);
    }

    public void setIgnoreHtmlView(final boolean ignoreHtmlView) {
        this.ignoreHtmlView = ignoreHtmlView;
    }

    public void setModelAdjuster(final ModelAdjuster modelAdjuster) {
        this.modelAdjuster = modelAdjuster;
    }

    public void setGlobalModelResolver(final GlobalModelResolver globalModelResolver) {
        this.globalModelResolver = globalModelResolver;
    }

    public void setLocaleProvider(final LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
    }

    public void setSoyMsgBundleResolver(final SoyMsgBundleResolver soyMsgBundleResolver) {
        this.soyMsgBundleResolver = soyMsgBundleResolver;
    }

}
