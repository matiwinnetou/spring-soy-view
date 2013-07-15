package pl.matisoft.soy.render;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 22/06/2013
 * Time: 17:00
 */
public class DefaultTemplateRenderer implements TemplateRenderer {

    protected RenderersProvider renderersProvider = new DefaultRenderersProvider();

    protected RendererConfigurer rendererConfigurer = new DefaultRendererConfigurer();

    protected RendererResponseWriter rendererResponseWriter = new DefaultRendererResponseWriter();

    @Override
    public void render(final RenderRequest renderRequest) throws Exception {
        if (!renderRequest.getCompiledTemplates().isPresent()) {
            return;
        }

        final List<RendererModelTuple> rendererModelTuples = renderersProvider.renderers(renderRequest);

        for (final RendererModelTuple tuple : rendererModelTuples) {
            rendererConfigurer.setupRenderer(tuple.getRenderer(), renderRequest, tuple.getModel());
            rendererResponseWriter.writeResponse(tuple.getRenderer(), renderRequest);
        }
    }

    public void setRenderersProvider(final RenderersProvider renderersProvider) {
        this.renderersProvider = renderersProvider;
    }

    public void setRendererConfigurer(final RendererConfigurer rendererConfigurer) {
        this.rendererConfigurer = rendererConfigurer;
    }

    public void setRendererResponseWriter(final RendererResponseWriter rendererResponseWriter) {
        this.rendererResponseWriter = rendererResponseWriter;
    }

}
