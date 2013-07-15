package pl.matisoft.soy.render;

import com.google.common.collect.Lists;
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

        return Lists.newArrayList(new RendererModelTuple(compiledTemplates.newRenderer(templateName), renderRequest.getSoyMapData()));
    }

}
