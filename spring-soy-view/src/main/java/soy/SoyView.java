package soy;

import com.google.common.base.Optional;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.tofu.SoyTofu;
import org.springframework.web.servlet.view.AbstractTemplateView;
import soy.config.SoyViewConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 19/06/2013
 * Time: 23:32
 */
public class SoyView extends AbstractTemplateView {

    private SoyTofu compiledTemplates;

    private String templateName;

    private SoyViewConfig config;

    public SoyView() {
    }

    @Override
    protected void renderMergedTemplateModel(final Map<String, Object> model,
                                             final HttpServletRequest request,
                                             final HttpServletResponse response) throws Exception {
        SoyUtils.checkSoyViewConfig(config);
        final Writer writer = response.getWriter();

//        if (config.isDebugOn()) {
//            final Collection<File> files = config.getTemplateFilesResolver().resolve();
//            compiledTemplates = config.getTofuCompiler().compile(files);
//        }

        final SoyTofu.Renderer renderer = compiledTemplates.newRenderer(templateName);
        final SoyMapData soyMapData = config.getToSoyDataConverter().toSoyMap(model);
        if (soyMapData != null) {
            renderer.setData(soyMapData);
        }
        final Optional<SoyMsgBundle> soyMsgBundleOptional = SoyUtils.soyMsgBundle(config, request);
        if (soyMsgBundleOptional.isPresent()) {
            renderer.setMsgBundle(soyMsgBundleOptional.get());
        }
        final Optional<SoyMapData> globalModel = config.getGlobalModelResolver().resolveData();
        if (globalModel.isPresent()) {
            renderer.setIjData(globalModel.get());
        }
        if (config.isDebugOn()) {
            renderer.setDontAddToCache(true);
        }

        renderer.render(writer);
        writer.flush();
    }

    public void setConfig(final SoyViewConfig config) {
        this.config = config;
    }

    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    public void setCompiledTemplates(final SoyTofu compiledTemplates) {
        this.compiledTemplates = compiledTemplates;
    }

}
