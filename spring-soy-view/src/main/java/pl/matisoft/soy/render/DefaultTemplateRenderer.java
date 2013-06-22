package pl.matisoft.soy.render;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;
import pl.matisoft.soy.SoyUtils;
import pl.matisoft.soy.config.AbstractSoyConfigEnabled;
import pl.matisoft.soy.data.ToSoyDataConverter;

import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 22/06/2013
 * Time: 17:00
 */
public class DefaultTemplateRenderer extends AbstractSoyConfigEnabled implements TemplateRenderer {

    @Override
    public Optional<String> render(final Optional<SoyTofu> compiledTemplates, final String templateName, final HttpServletRequest request, final Object model) throws Exception {
        SoyUtils.checkSoyViewConfig(config);

        if (!compiledTemplates.isPresent()) {
            return Optional.absent();
        }

        final SoyTofu.Renderer renderer = compiledTemplates.get().newRenderer(templateName);
        final ToSoyDataConverter soyDataConverter = config.getToSoyDataConverter();
        final Optional<SoyMapData> soyMapData = soyDataConverter.toSoyMap(model);
        if (soyMapData.isPresent()) {
            renderer.setData(soyMapData.get());
        }
        final Optional<SoyMsgBundle> soyMsgBundleOptional = SoyUtils.soyMsgBundle(config, request);
        if (soyMsgBundleOptional.isPresent()) {
            renderer.setMsgBundle(soyMsgBundleOptional.get());
        }
        final Optional<SoyMapData> globalModel = config.getGlobalModelResolver().resolveData();
        if (globalModel.isPresent()) {
            renderer.setIjData(globalModel.get());
        }
        if (config.isDebugOn()) {
            renderer.setDontAddToCache(true);
        }

        return Optional.of(renderer.render());
    }

}
