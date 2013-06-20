package soy.config;

import org.springframework.core.io.ClassPathResource;
import soy.bundle.SoyMsgBundleResolver;
import soy.bundle.SoyMsgBundleResolverImpl;
import soy.compile.DefaultTofuCompiler;
import soy.compile.TofuCompiler;
import soy.data.PojoReflectionToSoyDataConverter;
import soy.data.ToSoyDataConverter;
import soy.locale.AcceptHeaderLocaleResolver;
import soy.locale.LocaleResolver;
import soy.template.DefaultTemplateFilesResolver;
import soy.template.TemplateFilesResolver;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 20:40
 */
public class DefaultSoyViewConfig implements SoyViewConfig {

    private boolean isDebugOn;

    private LocaleResolver localeResolver = new AcceptHeaderLocaleResolver();

    private TofuCompiler tofuCompiler = new DefaultTofuCompiler();

    private SoyMsgBundleResolver soyMsgBundleResolver = new SoyMsgBundleResolverImpl();

    private TemplateFilesResolver templateFilesResolver = new DefaultTemplateFilesResolver(new ClassPathResource("/WEB-INF/templates"), true);

    private ToSoyDataConverter toSoyDataConverter = new PojoReflectionToSoyDataConverter();

    public void setDebugOn(final boolean debugOn) {
        isDebugOn = debugOn;
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
