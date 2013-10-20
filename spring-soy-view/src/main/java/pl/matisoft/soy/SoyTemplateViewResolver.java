package pl.matisoft.soy;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.template.soy.tofu.SoyTofu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import pl.matisoft.soy.bundle.EmptySoyMsgBundleResolver;
import pl.matisoft.soy.bundle.SoyMsgBundleResolver;
import pl.matisoft.soy.compile.DefaultTofuCompiler;
import pl.matisoft.soy.compile.TofuCompiler;
import pl.matisoft.soy.config.SoyViewConfig;
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

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 19:51
 *
 * An Soy implementation of Spring's TemplateViewResolver.
 *
 * Warning: please read carefully JavaDocs from AbstractTemplateViewResolver
 * and it the classes from which it inherits as it may be necessary to
 * set some configuration options for this resolver to work properly for your use case.
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

    /** an encoding to use */
    private String encoding = SoyViewConfig.DEFAULT_ENCODING;

    /** a default view */
    private String indexView = "index";

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
            } catch (final IOException ex) {
                throw new IllegalStateException("Unable to compile Soy templates.", ex);
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

        final SoyView view = (SoyView) super.buildView(orIndexView(normalize(viewName)));
        view.setTemplateName(view.getUrl());
        view.setContentType(contentType());
        view.setTemplateRenderer(templateRenderer);
        view.setModelAdjuster(modelAdjuster);
        view.setGlobalModelResolver(globalModelResolver);
        view.setLocaleProvider(localeProvider);
        view.setSoyMsgBundleResolver(soyMsgBundleResolver);

        if (!compiledTemplates.isPresent()) {
            view.setCompiledTemplates(compileTemplates(viewName));

            return view;
        }

        if (isCache() && compiledTemplates.isPresent()) { //extra sanity check if compiled templates are available
            view.setCompiledTemplates(compiledTemplates);
        }

        return view;
    }

    /**
     * Map / to a default view name.
     * @param viewName The view name.
     * @return The defaulted view name.
     */
    private String orIndexView(final String viewName) {
        return StringUtils.isEmpty(viewName) ? indexView : viewName;
    }

    /**
     * Remove beginning and ending slashes, then replace all occurrences of / with .
     *
     * @param viewName The Spring viewName
     * @return The name of the view, dot separated.
     */
    private String normalize(final String viewName) {
        String normalized = viewName;

        if (normalized.startsWith("/")) {
            normalized = normalized.substring(0);
        }

        if (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized.replaceAll("/", ".");
    }

    private String contentType() {
        return "text/html; charset=" + encoding;
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

    public void setIndexView(String indexView) {
        this.indexView = indexView;
    }

}
