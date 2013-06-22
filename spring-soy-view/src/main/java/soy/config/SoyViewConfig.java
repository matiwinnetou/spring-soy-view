package soy.config;

import com.google.template.soy.jssrc.SoyJsSrcOptions;
import soy.bundle.SoyMsgBundleResolver;
import soy.compile.TofuCompiler;
import soy.data.ToSoyDataConverter;
import soy.locale.LocaleResolver;
import soy.model.GlobalModelResolver;
import soy.template.TemplateFilesResolver;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 19:53
 */
public interface SoyViewConfig {

    boolean isDebugOn();

    LocaleResolver getLocaleResolver();

    TemplateFilesResolver getTemplateFilesResolver();

    TofuCompiler getTofuCompiler();

    SoyMsgBundleResolver getSoyMsgBundleResolver();

    ToSoyDataConverter getToSoyDataConverter();

    GlobalModelResolver getGlobalModelResolver();

    SoyJsSrcOptions getJsSrcOptions();

}
