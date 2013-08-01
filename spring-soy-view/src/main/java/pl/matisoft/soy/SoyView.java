package pl.matisoft.soy;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.template.soy.tofu.SoyTofu;
import org.springframework.web.servlet.view.AbstractTemplateView;
import pl.matisoft.soy.bundle.EmptySoyMsgBundleResolver;
import pl.matisoft.soy.bundle.SoyMsgBundleResolver;
import pl.matisoft.soy.data.adjust.EmptyModelAdjuster;
import pl.matisoft.soy.data.adjust.ModelAdjuster;
import pl.matisoft.soy.global.EmptyGlobalModelResolver;
import pl.matisoft.soy.global.GlobalModelResolver;
import pl.matisoft.soy.locale.EmptyLocaleProvider;
import pl.matisoft.soy.locale.LocaleProvider;
import pl.matisoft.soy.render.DefaultTemplateRenderer;
import pl.matisoft.soy.render.RenderRequest;
import pl.matisoft.soy.render.TemplateRenderer;

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

    protected Optional<SoyTofu> compiledTemplates = Optional.absent();

    protected String templateName;

    protected TemplateRenderer templateRenderer = new DefaultTemplateRenderer();

    protected ModelAdjuster modelAdjuster = new EmptyModelAdjuster();

    protected GlobalModelResolver globalModelResolver = new EmptyGlobalModelResolver();

    protected LocaleProvider localeProvider = new EmptyLocaleProvider();

    protected SoyMsgBundleResolver soyMsgBundleResolver = new EmptySoyMsgBundleResolver();

    public SoyView() {
    }

    @Override
    protected void renderMergedTemplateModel(final Map<String, Object> model,
                                             final HttpServletRequest request,
                                             final HttpServletResponse response) throws Exception {
        Preconditions.checkNotNull(templateName, "templateName cannot be null");
        Preconditions.checkNotNull(templateRenderer, "templateRenderer cannot be null");
        Preconditions.checkNotNull(modelAdjuster, "modelAdjuster cannot be null");
        Preconditions.checkNotNull(globalModelResolver, "globalModelResolver cannot be null");
        Preconditions.checkNotNull(localeProvider, "localeProvider cannot be null");
        Preconditions.checkNotNull(soyMsgBundleResolver, "soyMsgBundleResolver cannot be null");

        if (!compiledTemplates.isPresent()) {
            throw new RuntimeException("Unable to render - compiled templates are empty!");
        }

        final Object adjustedModel = modelAdjuster.adjust(model);

        final RenderRequest renderRequest = new RenderRequest.Builder()
                .compiledTemplates(compiledTemplates)
                .templateName(templateName)
                .model(adjustedModel)
                .request(request)
                .response(response)
                .globalRuntimeModel(globalModelResolver.resolveData(request))
                .soyMsgBundle(soyMsgBundleResolver.resolve(localeProvider.resolveLocale(request)))
                .build();

        templateRenderer.render(renderRequest);
    }

    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    public void setCompiledTemplates(final Optional<SoyTofu> compiledTemplates) {
        this.compiledTemplates = compiledTemplates;
    }

    public void setTemplateRenderer(final TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    public void setModelAdjuster(final ModelAdjuster modelAdjuster) {
        this.modelAdjuster = modelAdjuster;
    }

    public void setGlobalModelResolver(GlobalModelResolver globalModelResolver) {
        this.globalModelResolver = globalModelResolver;
    }

    public void setLocaleProvider(LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
    }

    public void setSoyMsgBundleResolver(SoyMsgBundleResolver soyMsgBundleResolver) {
        this.soyMsgBundleResolver = soyMsgBundleResolver;
    }

}
