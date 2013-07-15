package pl.matisoft.soy.render;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 12.07.13
 * Time: 17:08
 */
public interface RendererConfigurer {

    void setupRenderer(SoyTofu.Renderer renderer, RenderRequest renderRequest, Optional<SoyMapData> model) throws Exception;

}
