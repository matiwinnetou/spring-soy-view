package soy.config;

import com.google.template.soy.jssrc.SoyJsSrcOptions;
import soy.bundle.SoyMsgBundleResolver;
import soy.compile.TofuCompiler;
import soy.data.ToSoyDataConverter;
import soy.global.compile.CompileTimeGlobalModelResolver;
import soy.locale.LocaleResolver;
import soy.global.GlobalModelResolver;
import soy.template.TemplateFilesResolver;

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

    SoyJsSrcOptions getJsSrcOptions();

}
