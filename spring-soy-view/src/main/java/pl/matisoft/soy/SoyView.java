package pl.matisoft.soy;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.template.soy.tofu.SoyTofu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.web.servlet.View;
import pl.matisoft.soy.bundle.EmptySoyMsgBundleResolver;
import pl.matisoft.soy.bundle.SoyMsgBundleResolver;
import pl.matisoft.soy.config.SoyViewConfigDefaults;
import pl.matisoft.soy.data.adjust.EmptyModelAdjuster;
import pl.matisoft.soy.data.adjust.ModelAdjuster;
import pl.matisoft.soy.global.runtime.EmptyGlobalRuntimeModelResolver;
import pl.matisoft.soy.global.runtime.GlobalRuntimeModelResolver;
import pl.matisoft.soy.locale.EmptyLocaleProvider;
import pl.matisoft.soy.locale.LocaleProvider;
import pl.matisoft.soy.render.DefaultTemplateRenderer;
import pl.matisoft.soy.render.RenderRequest;
import pl.matisoft.soy.render.TemplateRenderer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 19/06/2013
 * Time: 23:32
 */
public class SoyView implements View, BeanNameAware {

    private static final Logger logger = LoggerFactory.getLogger(SoyView.class);

    /** Compiled soy binary objects */
    protected Optional<SoyTofu> compiledTemplates = Optional.absent();

    /** name of the template with namespace, .e.g. ajax_macros.show_customer_data */
    protected String templateName;

    protected TemplateRenderer templateRenderer = new DefaultTemplateRenderer();

    protected ModelAdjuster modelAdjuster = new EmptyModelAdjuster();

    protected GlobalRuntimeModelResolver globalRuntimeModelResolver = new EmptyGlobalRuntimeModelResolver();

    protected LocaleProvider localeProvider = new EmptyLocaleProvider();

    protected SoyMsgBundleResolver soyMsgBundleResolver = new EmptySoyMsgBundleResolver();

    private String contentType = "text/html; charset=" + SoyViewConfigDefaults.DEFAULT_ENCODING;

    private String beanName = "";

    public SoyView() {
    }

    @Override
    public void render(Map<String, ? extends Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Preconditions.checkNotNull(templateName, "templateName cannot be null");
        Preconditions.checkNotNull(templateRenderer, "templateRenderer cannot be null");
        Preconditions.checkNotNull(modelAdjuster, "modelAdjuster cannot be null");
        Preconditions.checkNotNull(globalRuntimeModelResolver, "globalModelResolver cannot be null");
        Preconditions.checkNotNull(localeProvider, "localeProvider cannot be null");
        Preconditions.checkNotNull(soyMsgBundleResolver, "soyMsgBundleResolver cannot be null");

        logger.debug("SoyView rendering with beanName:{} and model:{}", beanName, model);

        if (!compiledTemplates.isPresent()) {
            throw new IOException("Unable to render - compiled templates are empty!");
        }

        final Object adjustedModel = modelAdjuster.adjust(model);

        final RenderRequest renderRequest = new RenderRequest.Builder()
                .compiledTemplates(compiledTemplates)
                .templateName(templateName)
                .model(adjustedModel)
                .request(request)
                .response(response)
                .globalRuntimeModel(globalRuntimeModelResolver.resolveData(request, response, model))
                .soyMsgBundle(soyMsgBundleResolver.resolve(localeProvider.resolveLocale(request)))
                .soyView(this)
                .build();

        templateRenderer.render(renderRequest);
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setCompiledTemplates(Optional<SoyTofu> compiledTemplates) {
        this.compiledTemplates = compiledTemplates;
    }

    public void setTemplateRenderer(TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    public void setModelAdjuster(ModelAdjuster modelAdjuster) {
        this.modelAdjuster = modelAdjuster;
    }

    public void setGlobalRuntimeModelResolver(GlobalRuntimeModelResolver globalRuntimeModelResolver) {
        this.globalRuntimeModelResolver = globalRuntimeModelResolver;
    }

    public void setLocaleProvider(LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
    }

    public void setSoyMsgBundleResolver(SoyMsgBundleResolver soyMsgBundleResolver) {
        this.soyMsgBundleResolver = soyMsgBundleResolver;
    }

}
