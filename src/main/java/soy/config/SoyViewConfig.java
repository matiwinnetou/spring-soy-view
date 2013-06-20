package soy.config;

import soy.bundle.SoyMsgBundleResolver;
import soy.compile.TofuCompiler;
import soy.data.PojoToSoyDataConverter;
import soy.locale.LocaleResolver;
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

    PojoToSoyDataConverter getPojoToSoyDataConverter();

}
