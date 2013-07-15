package pl.matisoft.soy.render;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 12.07.13
 * Time: 17:09
 */
public class DefaultRendererConfigurer implements RendererConfigurer {

    private boolean debugOn = false;

    public void setupRenderer(final SoyTofu.Renderer renderer, final RenderRequest renderRequest, final Optional<SoyMapData> model) throws Exception {
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

    public void setDebugOn(boolean debugOn) {
        this.debugOn = debugOn;
    }

}