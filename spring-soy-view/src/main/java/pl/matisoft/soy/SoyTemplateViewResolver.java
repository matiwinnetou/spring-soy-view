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
import pl.matisoft.soy.config.SoyViewConfigDefaults;
import pl.matisoft.soy.data.adjust.EmptyModelAdjuster;
import pl.matisoft.soy.data.adjust.ModelAdjuster;
import pl.matisoft.soy.global.runtime.EmptyGlobalRuntimeModelResolver;
import pl.matisoft.soy.global.runtime.GlobalRuntimeModelResolver;
import pl.matisoft.soy.holder.CompiledTemplatesHolder;
import pl.matisoft.soy.holder.EmptyCompiledTemplatesHolder;
import pl.matisoft.soy.locale.EmptyLocaleProvider;
import pl.matisoft.soy.locale.LocaleProvider;
import pl.matisoft.soy.render.EmptyTemplateRenderer;
import pl.matisoft.soy.render.TemplateRenderer;

import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA. User: mati Date: 20/06/2013 Time: 19:51
 *
 * An Soy implementation of Spring's TemplateViewResolver.
 *
 * Warning: please read carefully JavaDocs from AbstractTemplateViewResolver and it the classes from which it inherits
 * as it may be necessary to set some configuration options for this resolver to work properly for your use case.
 */
public class SoyTemplateViewResolver extends AbstractCachingViewResolver implements Ordered {

	/**
	 * Prefix for special view names that specify a redirect URL (usually to a controller after a form has been
	 * submitted and processed). Such view names will not be resolved in the configured default way but rather be
	 * treated as special shortcut.
	 */
	public static final String REDIRECT_URL_PREFIX = "redirect:";

	/**
	 * Prefix for special view names that specify a forward URL (usually to a controller after a form has been submitted
	 * and processed). Such view names will not be resolved in the configured default way but rather be treated as
	 * special shortcut.
	 */
	public static final String FORWARD_URL_PREFIX = "forward:";

	private static final Logger logger = LoggerFactory.getLogger(SoyTemplateViewResolver.class);

	protected TemplateRenderer templateRenderer = new EmptyTemplateRenderer();

	protected ModelAdjuster modelAdjuster = new EmptyModelAdjuster();

	protected GlobalRuntimeModelResolver globalRuntimeModelResolver = new EmptyGlobalRuntimeModelResolver();

	protected LocaleProvider localeProvider = new EmptyLocaleProvider();

	protected SoyMsgBundleResolver soyMsgBundleResolver = new EmptySoyMsgBundleResolver();

	private CompiledTemplatesHolder compiledTemplatesHolder = new EmptyCompiledTemplatesHolder();

	private ContentNegotiator contentNegotiator = new DefaultContentNegotiator();

	/** an encoding to use */
	private String encoding = SoyViewConfigDefaults.DEFAULT_ENCODING;

	/** a default view */
	private String indexView = "index";

	private boolean redirectContextRelative = true;

	private boolean redirectHttp10Compatible = true;

	private int order = Integer.MAX_VALUE;

	private String prefix = SoyViewConfigDefaults.DEFAULT_SOY_PREFIX;

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
		
		final List<String> contentTypes = contentNegotiator.contentTypes();

		if (!canHandle(newViewName) || !contentNegotiator.isSupportedContentTypes(contentTypes)) {
			logger.debug("Unable to handle view: {}.", newViewName);

			return null;
		}

		final SoyView view = new SoyView();
		view.setTemplateName(stripPrefix(newViewName));
		view.setContentType(resolveContentType(contentTypes));
		view.setTemplateRenderer(templateRenderer);
		view.setModelAdjuster(modelAdjuster);
		view.setGlobalRuntimeModelResolver(globalRuntimeModelResolver);
		view.setLocaleProvider(localeProvider);
		view.setSoyMsgBundleResolver(soyMsgBundleResolver);
		view.setCompiledTemplates(compiledTemplatesHolder.compiledTemplates());

		return view;
	}

	private boolean canHandle(final String templateName) {
		return templateName.startsWith(prefix);
	}

	private String stripPrefix(final String templateName) {
		if (canHandle(templateName)) {
			return templateName.replace(prefix, "");
		}

		return templateName;
	}

	/**
	 * Map / to a default view name.
	 * 
	 * @param viewName
	 *            The view name.
	 * @return The defaulted view name.
	 */
	private String orIndexView(final String viewName) {
		return StringUtils.isEmpty(viewName) ? indexView : viewName;
	}

	/**
	 * Remove beginning and ending slashes, then replace all occurrences of / with .
	 *
	 * @param viewName
	 *            The Spring viewName
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
	
	protected String resolveContentType(final List<String> contentTypes) {
        // TODO: This should be better handled.		
		if (contentTypes.size() > 1) {
			logger.warn("More than one content type is not currently supported; "
					+ "only first one ({}) will be used.", contentTypes.get(0));
		}
		
		return contentTypes.get(0) + "; charset=" + encoding;
    }

	public void setTemplateRenderer(final TemplateRenderer templateRenderer) {
		this.templateRenderer = templateRenderer;
	}

	public void setModelAdjuster(final ModelAdjuster modelAdjuster) {
		this.modelAdjuster = modelAdjuster;
	}

	public void setGlobalRuntimeModelResolver(final GlobalRuntimeModelResolver globalRuntimeModelResolver) {
		this.globalRuntimeModelResolver = globalRuntimeModelResolver;
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

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getIndexView() {
		return indexView;
	}

	public void setHotReloadMode(final boolean hotReloadMode) {
		setCache(!hotReloadMode);
	}

    public void setContentNegotiator(ContentNegotiator contentNegotiator) {
        this.contentNegotiator = contentNegotiator;
    }

}
