//package pl.matisoft.soy.example;
//
//import java.util.Properties;
//
//import com.google.common.collect.Lists;
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
//import org.springframework.web.servlet.View;
//import org.springframework.web.servlet.ViewResolver;
//import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
//import org.springframework.web.servlet.view.json.MappingJacksonJsonView;
//import pl.matisoft.soy.ajax.auth.AuthManager;
//import pl.matisoft.soy.ajax.auth.ConfigurableAuthManager;
//import pl.matisoft.soy.ajax.hash.HashFileGenerator;
//import pl.matisoft.soy.ajax.hash.MD5HashFileGenerator;
//import pl.matisoft.soy.ajax.process.OutputProcessor;
//import pl.matisoft.soy.ajax.process.google.GoogleClosureOutputProcessor;
//import pl.matisoft.soy.ajax.url.DefaultTemplateUrlComposer;
//import pl.matisoft.soy.ajax.url.TemplateUrlComposer;
//import pl.matisoft.soy.configurer.SimpleSoyConfigurer;
//import pl.matisoft.soy.global.DefaultGlobalModelResolver;
//import pl.matisoft.soy.global.GlobalModelResolver;
//import pl.matisoft.soy.global.compile.CompileTimeGlobalModelResolver;
//import pl.matisoft.soy.global.compile.EmptyCompileTimeGlobalModelResolver;
//
///**
// * Created with IntelliJ IDEA.
// * User: mati
// * Date: 27/06/2013
// * Time: 23:02
// */
//@Configuration
//@EnableWebMvc
//@ComponentScan(basePackages = {"pl.matisoft.soy.example"})
//public class SimpleSoyConfiguration extends WebMvcConfigurerAdapter {
//
//    @Autowired
//    private javax.servlet.ServletContext context;
//
//    private boolean debugOn = true;
//
//    @Override
//    public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
//        configurer
//                .defaultContentType(MediaType.TEXT_HTML)
//                .favorPathExtension(true)
//                .favorParameter(true)
//                .ignoreAcceptHeader(true)
//                .mediaType("html", MediaType.TEXT_HTML)
//                .mediaType("json", MediaType.APPLICATION_JSON)
//                .mediaType("xml", MediaType.APPLICATION_XML);
//    }
//
//    @Bean
//    public SimpleSoyConfigurer simpleSoyConfigurer() {
//        final SimpleSoyConfigurer simpleSoyConfigurer = new SimpleSoyConfigurer();
//        simpleSoyConfigurer.setCompileTimeGlobalModelResolver(compileTimeGlobalModelResolver());
//        simpleSoyConfigurer.setRuntimeModelResolver(globalModelResolver());
//
//        return simpleSoyConfigurer;
//    }
//
//    @Bean
//    public CompileTimeGlobalModelResolver compileTimeGlobalModelResolver() {
//        return new EmptyCompileTimeGlobalModelResolver();
//    }
//
//    @Bean
//    public GlobalModelResolver globalModelResolver() {
//        return new DefaultGlobalModelResolver();
//    }
//
//    @Bean
//    public OutputProcessor closureCompilerProcessor() {
//        return new GoogleClosureOutputProcessor();
//    }
//
//    @Bean
//    public FactoryBean contentNegotiationManagerFactoryBean() {
//        final Properties props = new Properties();
//        props.put("html", "text/html");
//        props.put("json", "application/json");
//        props.put("xml", "application/xml");
//
//        final ContentNegotiationManagerFactoryBean contentNegotiationManagerFactoryBean = new ContentNegotiationManagerFactoryBean();
//        contentNegotiationManagerFactoryBean.setFavorPathExtension(true);
//        contentNegotiationManagerFactoryBean.setFavorParameter(true);
//        contentNegotiationManagerFactoryBean.setMediaTypes(props);
//        contentNegotiationManagerFactoryBean.setIgnoreAcceptHeader(true);
//
//        return contentNegotiationManagerFactoryBean;
//    }
//
//    @Bean
//    public ViewResolver contentNegotiatingViewResolver() {
//        final ContentNegotiatingViewResolver contentNegotiatingViewResolver = new ContentNegotiatingViewResolver();
//        final ViewResolver viewResolver = simpleSoyConfigurer().soyTemplateViewResolver();
//        contentNegotiatingViewResolver.setViewResolvers(Lists.newArrayList(viewResolver));
//        contentNegotiatingViewResolver.setDefaultViews(Lists.<View>newArrayList(new MappingJacksonJsonView()));
//
//        return contentNegotiatingViewResolver;
//    }
//
//    @Bean
//    public TemplateUrlComposer templateUrlComposer() {
//        final DefaultTemplateUrlComposer urlComposer = new DefaultTemplateUrlComposer();
//        urlComposer.setSiteUrl("http://localhost:8080/spring-soy-view-example/app");
//        //urlComposer.setTemplateFilesResolver(templa());
//        urlComposer.setHashFileGenerator(hashFileGenerator());
//
//        return urlComposer;
//    }
//
//    @Bean
//    public HashFileGenerator hashFileGenerator() {
//        final MD5HashFileGenerator md5HashFileGenerator = new MD5HashFileGenerator();
//        md5HashFileGenerator.setDebugOn(true);
//
//        return md5HashFileGenerator;
//    }
//
////    @Bean
////    public SoyAjaxController soyAjaxController() {
////        final SoyAjaxController soyAjaxController = new SoyAjaxController();
////        soyAjaxController.setDebugOn(debugOn);
////        soyAjaxController.setLocaleProvider(localeProvider());
////        soyAjaxController.setSoyMsgBundleResolver(soyMsgBundleResolver());
////        soyAjaxController.setTemplateFilesResolver(templateFilesResolver());
////        soyAjaxController.setTofuCompiler(tofuCompiler());
////        soyAjaxController.setOutputProcessors(Lists.newArrayList(closureCompilerProcessor()));
////        soyAjaxController.setAuthManager(authManager());
////
////        return soyAjaxController;
////    }
//
//    @Bean
//    public AuthManager authManager() {
//        final ConfigurableAuthManager configurableAuthManager = new ConfigurableAuthManager();
//        configurableAuthManager.setAllowedTemplates(Lists.newArrayList("client-words", "server-time"));
//
//        return configurableAuthManager;
//    }
//
//}
