package pl.matisoft.soy;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;
import org.springframework.web.servlet.view.AbstractView;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;
import pl.matisoft.soy.bundle.EmptySoyMsgBundleResolver;
import pl.matisoft.soy.bundle.SoyMsgBundleResolver;
import pl.matisoft.soy.holder.CompiledTemplatesHolder;
import pl.matisoft.soy.holder.EmptyCompiledTemplatesHolder;
import pl.matisoft.soy.config.SoyViewConfig;
import pl.matisoft.soy.data.adjust.EmptyModelAdjuster;
import pl.matisoft.soy.data.adjust.ModelAdjuster;
import pl.matisoft.soy.global.runtime.EmptyGlobalModelResolver;
import pl.matisoft.soy.global.runtime.GlobalModelResolver;
import pl.matisoft.soy.locale.EmptyLocaleProvider;
import pl.matisoft.soy.locale.LocaleProvider;
import pl.matisoft.soy.render.EmptyTemplateRenderer;
import pl.matisoft.soy.render.TemplateRenderer;

import java.util.Locale;

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
public class SoyTemplateViewResolver extends AbstractCachingViewResolver implements Ordered {

    /**
     * Prefix for special view names that specify a redirect URL (usually
     * to a controller after a form has been submitted and processed).
     * Such view names will not be resolved in the configured default
     * way but rather be treated as special shortcut.
     */
    public static final String REDIRECT_URL_PREFIX = "redirect:";

    /**
     * Prefix for special view names that specify a forward URL (usually
     * to a controller after a form has been submitted and processed).
     * Such view names will not be resolved in the configured default
     * way but rather be treated as special shortcut.
     */
    public static final String FORWARD_URL_PREFIX = "forward:";

    /**
     * Special soy prefix to indicate that this template if meant to be rendered by soy template renderer
     */
    public static final String SOY_PREFIX = "soy:";

    private static final Logger logger = LoggerFactory.getLogger(SoyTemplateViewResolver.class);

    protected TemplateRenderer templateRenderer = new EmptyTemplateRenderer();

    protected ModelAdjuster modelAdjuster = new EmptyModelAdjuster();

    protected GlobalModelResolver globalModelResolver = new EmptyGlobalModelResolver();

    protected LocaleProvider localeProvider = new EmptyLocaleProvider();

    protected SoyMsgBundleResolver soyMsgBundleResolver = new EmptySoyMsgBundleResolver();

    /** an encoding to use */
    private String encoding = SoyViewConfig.DEFAULT_ENCODING;

    /** a default view */
    private String indexView = "index";

    private boolean redirectContextRelative = true;

    private boolean redirectHttp10Compatible = true;

    private int order = Integer.MAX_VALUE;

    private CompiledTemplatesHolder compiledTemplatesHolder = new EmptyCompiledTemplatesHolder();

    protected View loadView(final String viewName, final Locale locale) throws Exception {
        Preconditions.checkNotNull(viewName, "viewName cannot be null!");
        Preconditions.checkNotNull(templateRenderer, "templateRenderer cannot be null!");
        logger.debug("loadView:{}, locale:{}", viewName, locale);

        if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
            final String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length());
            final RedirectView view = new RedirectView(redirectUrl, redirectContextRelative, redirectHttp10Compatible);

            return applyLifecycleMethods(viewName, view);
        }

        if (viewName.startsWith(FORWARD_URL_PREFIX)) {
            final String forwardUrl = viewName.substring(FORWARD_URL_PREFIX.length());

            return new InternalResourceView(forwardUrl);
        }

        final String newViewName = orIndexView(normalize(viewName));

        if (!canHandle(newViewName)) {
            logger.debug("Unable to handle, this is not a soy template, needs 'soy:' prefix.");
            return null;
        }

        final SoyView view = new SoyView();
        view.setTemplateName(stripPrefix(newViewName));
        view.setContentType(contentType());
        view.setTemplateRenderer(templateRenderer);
        view.setModelAdjuster(modelAdjuster);
        view.setGlobalModelResolver(globalModelResolver);
        view.setLocaleProvider(localeProvider);
        view.setSoyMsgBundleResolver(soyMsgBundleResolver);
        view.setCompiledTemplates(compiledTemplatesHolder.compiledTemplates());

        return view;
    }

    private boolean canHandle(final String templateName) {
        return templateName.startsWith(SOY_PREFIX);
    }

    private String stripPrefix(final String templateName) {
        if (canHandle(templateName)) {
            return templateName.replace(SOY_PREFIX, "");
        }

        return templateName;
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

    private View applyLifecycleMethods(String viewName, AbstractView view) {
        return (View) getApplicationContext().getAutowireCapableBeanFactory().initializeBean(view, viewName);
    }

    private String contentType() {
        return "text/html; charset=" + encoding;
    }

    public void setTemplateRenderer(final TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
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

    public void setRedirectContextRelative(boolean redirectContextRelative) {
        this.redirectContextRelative = redirectContextRelative;
    }

    public void setRedirectHttp10Compatible(boolean redirectHttp10Compatible) {
        this.redirectHttp10Compatible = redirectHttp10Compatible;
    }

    public boolean isRedirectContextRelative() {
        return redirectContextRelative;
    }

    public boolean isRedirectHttp10Compatible() {
        return redirectHttp10Compatible;
    }

    public void setCompiledTemplatesHolder(CompiledTemplatesHolder compiledTemplatesHolder) {
        this.compiledTemplatesHolder = compiledTemplatesHolder;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
