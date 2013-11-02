package pl.matisoft.soy.holder;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.template.soy.tofu.SoyTofu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import pl.matisoft.soy.compile.EmptyTofuCompiler;
import pl.matisoft.soy.compile.TofuCompiler;
import pl.matisoft.soy.config.SoyViewConfig;
import pl.matisoft.soy.template.EmptyTemplateFilesResolver;
import pl.matisoft.soy.template.TemplateFilesResolver;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 02/11/2013
 * Time: 14:04
 */
public class DefaultCompiledTemplatesHolder implements InitializingBean, CompiledTemplatesHolder {

    private static final Logger logger = LoggerFactory.getLogger(DefaultCompiledTemplatesHolder.class);

    private boolean debugOn = false;

    private TofuCompiler tofuCompiler = new EmptyTofuCompiler();

    private TemplateFilesResolver templatesFileResolver = new EmptyTemplateFilesResolver();

    private Optional<SoyTofu> compiledTemplates = Optional.absent();

    private boolean preCompileTemplates = SoyViewConfig.DEFAULT_PRECOMPILE_TEMPLATES;

    public Optional<SoyTofu> compiledTemplates() throws IOException {
        if (isDebugOn() || !compiledTemplates.isPresent()) {
            this.compiledTemplates = Optional.fromNullable(compileTemplates());
        }

        return compiledTemplates;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug("TemplatesHolder init...");
        if (preCompileTemplates) {
            this.compiledTemplates = Optional.fromNullable(compileTemplates());
        }
    }

    private SoyTofu compileTemplates() throws IOException {
        Preconditions.checkNotNull(templatesFileResolver, "templatesRenderer cannot be null!");
        Preconditions.checkNotNull(tofuCompiler, "tofuCompiler cannot be null!");

        final Collection<URL> templateFiles = templatesFileResolver.resolve();
        if (templateFiles != null && templateFiles.size() > 0) {
            logger.debug("Compiling templates, no:{}", templateFiles.size());

            return tofuCompiler.compile(templateFiles);
        }

        throw new IOException("0 template files have been found, check your templateFilesResolver!");
    }

    public void setDebugOn(final boolean debugOn) {
        this.debugOn = debugOn;
    }

    public boolean isDebugOn() {
        return debugOn;
    }

    public boolean isDebugOff() {
        return !debugOn;
    }

    public void setTofuCompiler(TofuCompiler tofuCompiler) {
        this.tofuCompiler = tofuCompiler;
    }

    public void setTemplatesFileResolver(TemplateFilesResolver templatesFileResolver) {
        this.templatesFileResolver = templatesFileResolver;
    }

    public void setPreCompileTemplates(boolean preCompileTemplates) {
        this.preCompileTemplates = preCompileTemplates;
    }

}
