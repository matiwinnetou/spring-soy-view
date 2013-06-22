package pl.matisoft.soy.config;

import com.google.template.soy.jssrc.SoyJsSrcOptions;
import org.springframework.core.io.ClassPathResource;
import pl.matisoft.soy.bundle.EmptySoyMsgBundleResolver;
import pl.matisoft.soy.bundle.SoyMsgBundleResolver;
import pl.matisoft.soy.compile.DefaultTofuCompiler;
import pl.matisoft.soy.compile.TofuCompiler;
import pl.matisoft.soy.data.DefaultToSoyDataConverter;
import pl.matisoft.soy.data.ToSoyDataConverter;
import pl.matisoft.soy.global.compile.CompileTimeGlobalModelResolver;
import pl.matisoft.soy.global.compile.EmptyCompileTimeGlobalModelResolver;
import pl.matisoft.soy.locale.EmptyLocaleResolver;
import pl.matisoft.soy.locale.LocaleResolver;
import pl.matisoft.soy.global.EmptyGlobalModelResolver;
import pl.matisoft.soy.global.GlobalModelResolver;
import pl.matisoft.soy.render.DefaultTemplateRenderer;
import pl.matisoft.soy.render.TemplateRenderer;
import pl.matisoft.soy.template.DefaultTemplateFilesResolver;
import pl.matisoft.soy.template.TemplateFilesResolver;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 20:40
 */
public class DefaultSoyViewConfig implements SoyViewConfig {

    public static final String DEFAULT_ENCODING = "utf-8";

    private boolean isDebugOn;

    private LocaleResolver localeResolver = new EmptyLocaleResolver();

    private TofuCompiler tofuCompiler = new DefaultTofuCompiler();

    private SoyMsgBundleResolver soyMsgBundleResolver = new EmptySoyMsgBundleResolver();

    private TemplateFilesResolver templateFilesResolver = new DefaultTemplateFilesResolver(new ClassPathResource("/WEB-INF/templates"), true);

    private ToSoyDataConverter toSoyDataConverter = new DefaultToSoyDataConverter();

    private GlobalModelResolver globalModelResolver = new EmptyGlobalModelResolver();

    private CompileTimeGlobalModelResolver compileTimeGlobalModelResolver = new EmptyCompileTimeGlobalModelResolver();

    private TemplateRenderer templateRenderer = new DefaultTemplateRenderer();

    private SoyJsSrcOptions soyJsSrcOptions = new SoyJsSrcOptions();

    private String encoding = DEFAULT_ENCODING;

    public DefaultSoyViewConfig() {
    }

    public String getEncoding() {
        return encoding;
    }

    public TemplateRenderer getTemplateRenderer() {
        return templateRenderer;
    }

    public void setTemplateRenderer(TemplateRenderer templateRenderer) {
        this.templateRenderer = templateRenderer;
    }

    public CompileTimeGlobalModelResolver getCompileTimeGlobalModelResolver() {
        return compileTimeGlobalModelResolver;
    }

    public void setCompileTimeGlobalModelResolver(CompileTimeGlobalModelResolver compileTimeGlobalModelResolver) {
        this.compileTimeGlobalModelResolver = compileTimeGlobalModelResolver;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public void setDebugOn(final boolean debugOn) {
        isDebugOn = debugOn;
    }

    public SoyJsSrcOptions getJsSrcOptions() {
        return soyJsSrcOptions;
    }

    public GlobalModelResolver getGlobalModelResolver() {
        return globalModelResolver;
    }

    public void setGlobalModelResolver(GlobalModelResolver globalModelResolver) {
        this.globalModelResolver = globalModelResolver;
    }

    public SoyJsSrcOptions getSoyJsSrcOptions() {
        return soyJsSrcOptions;
    }

    public void setSoyJsSrcOptions(final SoyJsSrcOptions soyJsSrcOptions) {
        this.soyJsSrcOptions = soyJsSrcOptions;
    }

    public ToSoyDataConverter getToSoyDataConverter() {
        return toSoyDataConverter;
    }

    public void setToSoyDataConverter(ToSoyDataConverter toSoyDataConverter) {
        this.toSoyDataConverter = toSoyDataConverter;
    }

    public TemplateFilesResolver getTemplateFilesResolver() {
        return templateFilesResolver;
    }

    public void setTemplateFilesResolver(TemplateFilesResolver templateFilesResolver) {
        this.templateFilesResolver = templateFilesResolver;
    }

    public void setLocaleResolver(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

    public void setTofuCompiler(TofuCompiler tofuCompiler) {
        this.tofuCompiler = tofuCompiler;
    }

    public void setSoyMsgBundleResolver(SoyMsgBundleResolver soyMsgBundleResolver) {
        this.soyMsgBundleResolver = soyMsgBundleResolver;
    }

    @Override
    public boolean isDebugOn() {
        return isDebugOn;
    }

    @Override
    public LocaleResolver getLocaleResolver() {
        return localeResolver;
    }

    @Override
    public TofuCompiler getTofuCompiler() {
        return tofuCompiler;
    }

    @Override
    public SoyMsgBundleResolver getSoyMsgBundleResolver() {
        return soyMsgBundleResolver;
    }

}
