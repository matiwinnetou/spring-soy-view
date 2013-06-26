package pl.matisoft.soy.render;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;
import pl.matisoft.soy.bundle.EmptySoyMsgBundleResolver;
import pl.matisoft.soy.bundle.SoyMsgBundleResolver;
import pl.matisoft.soy.data.EmptyToSoyDataConverter;
import pl.matisoft.soy.data.ToSoyDataConverter;
import pl.matisoft.soy.data.adjust.EmptyModelAdjuster;
import pl.matisoft.soy.data.adjust.ModelAdjuster;
import pl.matisoft.soy.global.EmptyGlobalModelResolver;
import pl.matisoft.soy.global.GlobalModelResolver;
import pl.matisoft.soy.locale.EmptyLocaleProvider;
import pl.matisoft.soy.locale.LocaleProvider;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 22/06/2013
 * Time: 17:00
 */
public class DefaultTemplateRenderer implements TemplateRenderer {

    private ToSoyDataConverter toSoyDataConverter = new EmptyToSoyDataConverter();

    private GlobalModelResolver globalModelResolver = new EmptyGlobalModelResolver();

    private LocaleProvider localeProvider = new EmptyLocaleProvider();

    private ModelAdjuster modelAdjuster = new EmptyModelAdjuster();

    private SoyMsgBundleResolver soyMsgBundleResolver = new EmptySoyMsgBundleResolver();

    private boolean debugOn = false;

    @Override
    public Optional<String> render(final Optional<SoyTofu> compiledTemplates, final String templateName, final HttpServletRequest request, final Object model) throws Exception {
        Preconditions.checkNotNull(toSoyDataConverter, "toSoyDataConverter cannot be null!");
        Preconditions.checkNotNull(localeProvider, "localeProvider cannot be null!");
        Preconditions.checkNotNull(soyMsgBundleResolver, "soyMsgBundleResolver cannot be null!");
        Preconditions.checkNotNull(globalModelResolver, "globalModelResolver cannot be null!");

        if (!compiledTemplates.isPresent()) {
            return Optional.absent();
        }

        final SoyTofu.Renderer renderer = compiledTemplates.get().newRenderer(templateName);

        final Object adjustedModel = modelAdjuster.adjust(model);

        final Optional<SoyMapData> soyMapData = toSoyDataConverter.toSoyMap(adjustedModel);
        if (soyMapData.isPresent()) {
            renderer.setData(soyMapData.get());
        }
        final Optional<Locale> locale = localeProvider.resolveLocale(request);
        final Optional<SoyMsgBundle> soyMsgBundleOptional = soyMsgBundleResolver.resolve(locale);
        if (soyMsgBundleOptional.isPresent()) {
            renderer.setMsgBundle(soyMsgBundleOptional.get());
        }
        final Optional<SoyMapData> globalModel = globalModelResolver.resolveData();
        if (globalModel.isPresent()) {
            renderer.setIjData(globalModel.get());
        }
        if (debugOn) {
            renderer.setDontAddToCache(true);
        }

        return Optional.of(renderer.render());
    }

    public void setToSoyDataConverter(final ToSoyDataConverter toSoyDataConverter) {
        this.toSoyDataConverter = toSoyDataConverter;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
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

    public void setModelAdjuster(ModelAdjuster modelAdjuster) {
        this.modelAdjuster = modelAdjuster;
    }

}
