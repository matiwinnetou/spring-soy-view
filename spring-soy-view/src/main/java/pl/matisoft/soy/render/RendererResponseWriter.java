package pl.matisoft.soy.render;

import com.google.template.soy.tofu.SoyTofu;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 12.07.13
 * Time: 17:11
 */
public interface RendererResponseWriter {

    void writeResponse(final SoyTofu.Renderer renderer, final RenderRequest renderRequest) throws IOException;

}
