package pl.matisoft.soy.config;

import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.tofu.SoyTofuOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.ViewResolver;
import pl.matisoft.soy.ContentNegotiator;
import pl.matisoft.soy.DefaultContentNegotiator;
import pl.matisoft.soy.SoyTemplateViewResolver;
import pl.matisoft.soy.bundle.DefaultSoyMsgBundleResolver;
import pl.matisoft.soy.bundle.SoyMsgBundleResolver;
import pl.matisoft.soy.compile.DefaultTofuCompiler;
import pl.matisoft.soy.compile.TofuCompiler;
import pl.matisoft.soy.data.DefaultToSoyDataConverter;
import pl.matisoft.soy.data.ToSoyDataConverter;
import pl.matisoft.soy.data.adjust.ModelAdjuster;
import pl.matisoft.soy.data.adjust.SpringModelAdjuster;
import pl.matisoft.soy.global.compile.CompileTimeGlobalModelResolver;
import pl.matisoft.soy.global.compile.EmptyCompileTimeGlobalModelResolver;
import pl.matisoft.soy.global.runtime.EmptyGlobalRuntimeModelResolver;
import pl.matisoft.soy.global.runtime.GlobalRuntimeModelResolver;
import pl.matisoft.soy.holder.CompiledTemplatesHolder;
import pl.matisoft.soy.holder.DefaultCompiledTemplatesHolder;
import pl.matisoft.soy.locale.LocaleProvider;
import pl.matisoft.soy.locale.SpringLocaleProvider;
import pl.matisoft.soy.render.DefaultTemplateRenderer;
import pl.matisoft.soy.render.TemplateRenderer;
import pl.matisoft.soy.template.DefaultTemplateFilesResolver;
import pl.matisoft.soy.template.TemplateFilesResolver;

import javax.inject.Inject;
import javax.servlet.ServletContext;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 12/11/2013
 * Time: 19:55
 */
@Configuration
public class SpringSoyViewBaseConfig {

    @Value("${soy.hot.reload.mode:false}")
    private boolean hotReloadMode;

    @Value("${soy.templates.resolve.recursively:true}")
    private boolean recursive;

    @Value("${soy.templates.file.extension:soy}")
    private String fileExtension;

    @Value("${soy.templates.directory:/WEB-INF/templates}")
    private String templatesPath;

    @Value("${soy.i18n.xliff.path:xliffs/messages}")
    private String messagesPath;

    @Value("${soy.encoding:utf-8}")
    private String encoding;

    @Value("${soy.i18n.fallback.to.english:true}")
    private boolean fallbackToEnglish;

    @Value("${soy.preCompile.templates:false}")
    private boolean preCompileTemplates;

    @Value("${soy.indexView:index}")
    private String indexView;

    @Value("${soy.logical.prefix:soy:}")
    private String logicalPrefix;

    @Value("${soy.resolver.order:2147483647}")
    private int order;

    @Inject
    private ServletContext servletContext;

    @Bean
    public LocaleProvider soyLocaleProvider() {
        return new SpringLocaleProvider();
    }

    @Bean
    public DefaultTemplateFilesResolver soyTemplateFilesResolver() throws Exception {
        final DefaultTemplateFilesResolver defaultTemplateFilesResolver = new DefaultTemplateFilesResolver();
        defaultTemplateFilesResolver.setHotReloadMode(hotReloadMode);
        defaultTemplateFilesResolver.setRecursive(recursive);
        defaultTemplateFilesResolver.setFilesExtension(fileExtension);
        defaultTemplateFilesResolver.setTemplatesLocation(new ServletContextResource(servletContext, templatesPath));

        return defaultTemplateFilesResolver;
    }

    @Bean
    public CompileTimeGlobalModelResolver soyCompileTimeGlobalModelResolver() {
        return new EmptyCompileTimeGlobalModelResolver();
    }

    @Bean
    public ToSoyDataConverter soyToSoyDataConverter() {
        return new DefaultToSoyDataConverter();
    }

    @Bean
    public SoyJsSrcOptions soyJsSourceOptions() {
        return new SoyJsSrcOptions();
    }

    @Bean
    public SoyTofuOptions soyTofuOptions() {
        final SoyTofuOptions soyTofuOptions = new SoyTofuOptions();
        soyTofuOptions.setUseCaching(!hotReloadMode);

        return soyTofuOptions;
    }

    @Bean
    public TofuCompiler soyTofuCompiler(final CompileTimeGlobalModelResolver compileTimeGlobalModelResolver, final SoyJsSrcOptions soyJsSrcOptions, final SoyTofuOptions soyTofuOptions) {
        final DefaultTofuCompiler defaultTofuCompiler = new DefaultTofuCompiler();
        defaultTofuCompiler.setHotReloadMode(hotReloadMode);
        defaultTofuCompiler.setCompileTimeGlobalModelResolver(compileTimeGlobalModelResolver);
        defaultTofuCompiler.setSoyJsSrcOptions(soyJsSrcOptions);
        defaultTofuCompiler.setSoyTofuOptions(soyTofuOptions);

        return defaultTofuCompiler;
    }

    @Bean
    public SoyMsgBundleResolver soyMsgBundleResolver() {
        final DefaultSoyMsgBundleResolver defaultSoyMsgBundleResolver = new DefaultSoyMsgBundleResolver();
        defaultSoyMsgBundleResolver.setHotReloadMode(hotReloadMode);
        defaultSoyMsgBundleResolver.setMessagesPath(messagesPath);
        defaultSoyMsgBundleResolver.setFallbackToEnglish(fallbackToEnglish);

        return defaultSoyMsgBundleResolver;
    }

    @Bean
    public CompiledTemplatesHolder soyTemplatesHolder(final TemplateFilesResolver templateFilesResolver, final TofuCompiler tofuCompiler) throws Exception {
        final DefaultCompiledTemplatesHolder defaultCompiledTemplatesHolder = new DefaultCompiledTemplatesHolder();
        defaultCompiledTemplatesHolder.setHotReloadMode(hotReloadMode);
        defaultCompiledTemplatesHolder.setPreCompileTemplates(preCompileTemplates);
        defaultCompiledTemplatesHolder.setTemplatesFileResolver(templateFilesResolver);
        defaultCompiledTemplatesHolder.setTofuCompiler(tofuCompiler);

        return defaultCompiledTemplatesHolder;
    }

    @Bean
    public TemplateRenderer soyTemplateRenderer(final ToSoyDataConverter toSoyDataConverter) {
        final DefaultTemplateRenderer defaultTemplateRenderer = new DefaultTemplateRenderer();
        defaultTemplateRenderer.setHotReloadMode(hotReloadMode);
        defaultTemplateRenderer.setToSoyDataConverter(toSoyDataConverter);

        return defaultTemplateRenderer;
    }

    @Bean
    public ModelAdjuster soySpringModelAdjuster() {
        return new SpringModelAdjuster();
    }

    @Bean
    public GlobalRuntimeModelResolver soyGlobalRuntimeModelResolver() {
        return new EmptyGlobalRuntimeModelResolver();
    }

    @Bean
    public ContentNegotiator contentNegotiator() {
        return new DefaultContentNegotiator();
    }

    @Bean
    public ViewResolver soyViewResolver(final CompiledTemplatesHolder compiledTemplatesHolder,
                                        final ModelAdjuster modelAdjuster,
                                        final TemplateRenderer templateRenderer,
                                        final LocaleProvider localeProvider,
                                        final GlobalRuntimeModelResolver globalRuntimeModelResolver,
                                        final ContentNegotiator contentNegotiator,
                                        final SoyMsgBundleResolver msgBundleResolver)
                                     throws Exception {
        final SoyTemplateViewResolver soyTemplateViewResolver = new SoyTemplateViewResolver();
        soyTemplateViewResolver.setSoyMsgBundleResolver(msgBundleResolver);
        soyTemplateViewResolver.setCompiledTemplatesHolder(compiledTemplatesHolder);
        soyTemplateViewResolver.setEncoding(encoding);
        soyTemplateViewResolver.setGlobalRuntimeModelResolver(globalRuntimeModelResolver);
        soyTemplateViewResolver.setHotReloadMode(hotReloadMode);
        soyTemplateViewResolver.setIndexView(indexView);
        soyTemplateViewResolver.setLocaleProvider(localeProvider);
        soyTemplateViewResolver.setModelAdjuster(modelAdjuster);
        soyTemplateViewResolver.setTemplateRenderer(templateRenderer);
        soyTemplateViewResolver.setPrefix(logicalPrefix);
        soyTemplateViewResolver.setOrder(order);
        soyTemplateViewResolver.setRedirectContextRelative(true);
        soyTemplateViewResolver.setRedirectHttp10Compatible(true);
        soyTemplateViewResolver.setContentNegotiator(contentNegotiator);

        return soyTemplateViewResolver;
    }

}
