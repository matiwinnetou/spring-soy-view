package pl.matisoft.soy.render;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 12.07.13
 * Time: 10:56
 */
public class DefaultRenderersProvider implements RenderersProvider {

    @Override
    public List<RendererModelTuple> renderers(final RenderRequest renderRequest) throws Exception {
        if (!renderRequest.getCompiledTemplates().isPresent()) {
            return Collections.emptyList();
        }

        final String templateName = renderRequest.getTemplateName();
        if (StringUtils.isEmpty(templateName)) {
            return Collections.emptyList();
        }

        final SoyTofu compiledTemplates = renderRequest.getCompiledTemplates().get();

        final SoyTofu.Renderer renderer = compiledTemplates.newRenderer(templateName);
        final Optional<SoyMapData> soyData = renderRequest.getSoyModel();
        final RendererModelTuple rendererModelTuple = new RendererModelTuple(renderer, soyData);

        return Lists.newArrayList(rendererModelTuple);
    }

}
