package pl.matisoft.soy.render;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;
import pl.matisoft.soy.SoyView;
import pl.matisoft.soy.config.SoyViewConfig;
import pl.matisoft.soy.data.DefaultToSoyDataConverter;
import pl.matisoft.soy.data.ToSoyDataConverter;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 22/06/2013
 * Time: 17:00
 *
 * A default implementation of TemplateRenderer, which using a print writer writes to
 * a servlet output.
 *
 * The implementation will set all required objects if available on renderer, e.g. SoyMsgBundle, IjData (Injected Data)
 * before rendering to servlet output stream.
 *
 * Note: in order to support "an early head flush" performance recommendation and
 * "progressive rendering" a subclass of this class needs to be developed and an implementation
 * needs to inspect a passed in model object, cast it accordingly and flush to system out, after rendering it.
 *
 * A default implementation will flush to a servlet output stream once all model objects are resolved and converted to
 * SoyMapData, an important thing to notice here is that converting from model object to SoyMapData may be expensive
 * depending on an implementation, i.e. if model objects have been resolved in a controller
 * or if for instance they were wrapped in a Callable object.
 */
public class DefaultTemplateRenderer implements TemplateRenderer {

    protected ToSoyDataConverter toSoyDataConverter = new DefaultToSoyDataConverter();

    /**
     * whether debug is on, in case it is on - Soy's Renderer Don't Add To Cache will be turned on, which means
     * renderer caching will be disabled */
    private boolean debugOn = SoyViewConfig.DEFAULT_DEBUG_ON;

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
        final HttpServletResponse response = renderRequest.getResponse();
        final SoyView soyView = renderRequest.getSoyView();

        response.setContentType(soyView.getContentType());

        final PrintWriter writer = response.getWriter();
        writer.write(renderer.render());
        writer.flush();
        response.flushBuffer();
    }

    public void setToSoyDataConverter(final ToSoyDataConverter toSoyDataConverter) {
        this.toSoyDataConverter = toSoyDataConverter;
    }

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

    public boolean isDebugOn() {
        return debugOn;
    }

}
