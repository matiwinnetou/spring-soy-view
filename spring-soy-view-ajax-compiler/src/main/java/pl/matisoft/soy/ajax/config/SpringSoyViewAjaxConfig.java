package pl.matisoft.soy.ajax.config;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.matisoft.soy.ajax.SoyAjaxController;
import pl.matisoft.soy.ajax.auth.AuthManager;
import pl.matisoft.soy.ajax.auth.PermissableAuthManager;
import pl.matisoft.soy.ajax.hash.HashFileGenerator;
import pl.matisoft.soy.ajax.hash.MD5HashFileGenerator;
import pl.matisoft.soy.ajax.process.OutputProcessor;
import pl.matisoft.soy.ajax.process.google.GoogleClosureOutputProcessor;
import pl.matisoft.soy.ajax.url.DefaultTemplateUrlComposer;
import pl.matisoft.soy.ajax.url.TemplateUrlComposer;
import pl.matisoft.soy.bundle.SoyMsgBundleResolver;
import pl.matisoft.soy.compile.TofuCompiler;
import pl.matisoft.soy.config.SpringSoyViewBaseConfig;
import pl.matisoft.soy.locale.LocaleProvider;
import pl.matisoft.soy.template.TemplateFilesResolver;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 12/11/2013
 * Time: 22:15
 */
@Configuration
@Import(SpringSoyViewBaseConfig.class)
public class SpringSoyViewAjaxConfig {

    @Value("${soy.hot.reload.mode:false}")
    private boolean hotReloadMode;

    @Value("${soy.site.url:}")
    private String siteUrl;

    @Value("${soy.encoding:utf-8}")
    private String encoding;

    @Value("${soy.cache.busting.cache.control:public, max-age=86400}") //one year
    private String cacheControl;

    @Bean
    public HashFileGenerator soyMd5HashFileGenerator() {
        final MD5HashFileGenerator md5HashFileGenerator = new MD5HashFileGenerator();
        md5HashFileGenerator.setHotReloadMode(hotReloadMode);

        return md5HashFileGenerator;
    }

    @Bean
    public TemplateUrlComposer soyTemplateUrlComposer(final HashFileGenerator hashFileGenerator, final TemplateFilesResolver templateFilesResolver) {
        final DefaultTemplateUrlComposer defaultTemplateUrlComposer = new DefaultTemplateUrlComposer();
        defaultTemplateUrlComposer.setHashFileGenerator(hashFileGenerator);
        defaultTemplateUrlComposer.setTemplateFilesResolver(templateFilesResolver);
        defaultTemplateUrlComposer.setSiteUrl(siteUrl);

        return defaultTemplateUrlComposer;
    }

    @Bean
    public SoyAjaxController soyAjaxController(final AuthManager authManager,
                                               final LocaleProvider localeProvider,
                                               final TemplateFilesResolver templateFilesResolver,
                                               final TofuCompiler tofuCompiler,
                                               final SoyMsgBundleResolver soyMsgBundleResolver) {
        final GoogleClosureOutputProcessor googleClosureOutputProcessor = new GoogleClosureOutputProcessor();
        googleClosureOutputProcessor.setEncoding(encoding);

        final SoyAjaxController soyAjaxController = new SoyAjaxController();
        soyAjaxController.setAuthManager(authManager);
        soyAjaxController.setEncoding(encoding);
        soyAjaxController.setHotReloadMode(hotReloadMode);
        soyAjaxController.setLocaleProvider(localeProvider);
        soyAjaxController.setOutputProcessors(Lists.<OutputProcessor>newArrayList(googleClosureOutputProcessor));
        soyAjaxController.setCacheControl(cacheControl);
        soyAjaxController.setTemplateFilesResolver(templateFilesResolver);
        soyAjaxController.setTofuCompiler(tofuCompiler);
        soyAjaxController.setSoyMsgBundleResolver(soyMsgBundleResolver);

        return soyAjaxController;
    }

    @Bean
    public AuthManager soyAuthManager() {
        return new PermissableAuthManager();
    }

}
