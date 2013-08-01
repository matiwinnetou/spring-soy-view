package pl.matisoft.soy.render;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;
import pl.matisoft.soy.data.DefaultToSoyDataConverter;
import pl.matisoft.soy.data.ToSoyDataConverter;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 22/06/2013
 * Time: 17:00
 */
public class DefaultTemplateRenderer implements TemplateRenderer {

    protected ToSoyDataConverter toSoyDataConverter = new DefaultToSoyDataConverter();

    private boolean debugOn = false;

    @Override
    public void render(final RenderRequest renderRequest) throws Exception {
        if (!renderRequest.getCompiledTemplates().isPresent()) {
            return;
        }

        final SoyTofu compiledTemplates = renderRequest.getCompiledTemplates().get();

        final String templateName = renderRequest.getTemplateName();
        final SoyTofu.Renderer renderer = compiledTemplates.newRenderer(templateName);

        final Optional<SoyMapData> soyModel = toSoyDataConverter.toSoyMap(renderRequest.getModel());
        setupRenderer(renderer, renderRequest, soyModel);
        writeResponse(renderer, renderRequest);
    }

    protected void setupRenderer(final SoyTofu.Renderer renderer, final RenderRequest renderRequest, final Optional<SoyMapData> model) throws Exception {
        if (model.isPresent()) {
            renderer.setData(model.get());
        }
        final Optional<SoyMapData> globalModel = renderRequest.getGlobalRuntimeModel();
        if (globalModel.isPresent()) {
            renderer.setIjData(globalModel.get());
        }
        final Optional<SoyMsgBundle> soyMsgBundleOptional = renderRequest.getSoyMsgBundle();
        if (soyMsgBundleOptional.isPresent()) {
            renderer.setMsgBundle(soyMsgBundleOptional.get());
        }
        if (debugOn) {
            renderer.setDontAddToCache(true);
        }
    }

    protected void writeResponse(final SoyTofu.Renderer renderer, final RenderRequest renderRequest) throws IOException {
        final PrintWriter writer = renderRequest.getResponse().getWriter();
        writer.append(renderer.render());
        writer.flush();
        renderRequest.getResponse().flushBuffer();
    }

    public void setToSoyDataConverter(final ToSoyDataConverter toSoyDataConverter) {
        this.toSoyDataConverter = toSoyDataConverter;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

}
