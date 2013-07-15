package pl.matisoft.soy.render;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;

/**
* Created with IntelliJ IDEA.
* User: mszczap
* Date: 12.07.13
* Time: 17:14
*/
public class RendererModelTuple {

    private SoyTofu.Renderer renderer;
    private Optional<SoyMapData> model;

    public RendererModelTuple(SoyTofu.Renderer renderer, Optional<SoyMapData> model) {
        this.renderer = renderer;
        this.model = model;
    }

    public SoyTofu.Renderer getRenderer() {
        return renderer;
    }

    public Optional<SoyMapData> getModel() {
        return model;
    }

    @Override
    public String toString() {
        return "RendererModelTuple{" +
                "renderer=" + renderer +
                ", model=" + model +
                '}';
    }

}
