package pl.matisoft.soy.config;

import com.google.template.soy.jssrc.SoyJsSrcOptions;
import pl.matisoft.soy.bundle.SoyMsgBundleResolver;
import pl.matisoft.soy.compile.TofuCompiler;
import pl.matisoft.soy.data.ToSoyDataConverter;
import pl.matisoft.soy.global.compile.CompileTimeGlobalModelResolver;
import pl.matisoft.soy.locale.LocaleResolver;
import pl.matisoft.soy.global.GlobalModelResolver;
import pl.matisoft.soy.render.TemplateRenderer;
import pl.matisoft.soy.template.TemplateFilesResolver;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 19:53
 */
public interface SoyViewConfig {

    boolean isDebugOn();

    String getEncoding(); //e.g. utf-8

    LocaleResolver getLocaleResolver();

    TemplateFilesResolver getTemplateFilesResolver();

    TofuCompiler getTofuCompiler();

    SoyMsgBundleResolver getSoyMsgBundleResolver();

    ToSoyDataConverter getToSoyDataConverter();

    GlobalModelResolver getGlobalModelResolver();

    CompileTimeGlobalModelResolver getCompileTimeGlobalModelResolver();

    TemplateRenderer getTemplateRenderer();

    SoyJsSrcOptions getJsSrcOptions();

}
