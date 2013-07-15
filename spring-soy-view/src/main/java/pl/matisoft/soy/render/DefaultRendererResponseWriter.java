package pl.matisoft.soy.render;

import com.google.template.soy.tofu.SoyTofu;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 12.07.13
 * Time: 17:12
 */
public class DefaultRendererResponseWriter implements RendererResponseWriter {

    @Override
    public void writeResponse(final SoyTofu.Renderer renderer, final RenderRequest renderRequest) throws IOException {
        final PrintWriter writer = renderRequest.getResponse().getWriter();
        writer.append(renderer.render());
        writer.flush();
    }

}
