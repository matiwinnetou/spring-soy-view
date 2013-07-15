package pl.matisoft.soy.render;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 12.07.13
 * Time: 10:55
 */
public interface RenderersProvider {

    List<RendererModelTuple> renderers(RenderRequest renderRequest) throws Exception;

}
