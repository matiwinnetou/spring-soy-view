package pl.matisoft.soy.render;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 22/06/2013
 * Time: 17:00
 *
 * An interface which based on an object containing parameters passed to renderer will render
 * a Soy Template to an output, i.e. servlet output stream.
 */
public interface TemplateRenderer {

    void render(RenderRequest renderRequest) throws Exception;

}
