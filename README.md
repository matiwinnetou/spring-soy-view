#Introduction

Implementation of Spring's MVC View in Google Closure (soy)

It is highly customizable via pluggable interfaces, for which a default implementation is provided. It distinguishes itself from other frameworks, which are often "black boxes" and do not allow easy customisation. It was created due to specific requirements, which no other framework at that time could provide.

#### Supported
* POJO rendering and flat structure rendering (ToSoyDataConverter interface)
* I18N - SoyMsgBundle based on resolvable locale (SoyMsgBundleResolver interface)
* JavaScript compilation via AJAX endpoint using a Spring Controller (SoyAjaxController)
* JavaScript compilation which supports browser caching (hash url part)
* JavaScript obfuscation (via either google closure or yahoo js obfuscator) and combination of generated urls via Ajax
* Soy Global variables ($ij) supported with many out of the box runtime data resolvers (e.g. http session, request parameters, request headers, servlet context, etc) via (GlobalModelResolver interface)
* Soy Compile time global variables supported (CompileTimeGlobalModelResolver interface)
* Debug flag support, allows editing and changing soy files in dev mode with immediate reflection and recompilation of soy
* Ability to provide own template file resolver (TemplateFilesResolver interface)
* Model transformation available (ModelAdjuster interface) since spring wraps the real backing model in it's own map  

# User's Guide

In order to use spring-soy-view one has to wire beans. Since the library allows to adjust and plug an implementation for almost any internal function the configuration may seem to be verbose. It is recommended to keep spring-soy-view configuration in either separate XML file or Java Class, so that it does not obscure your internal application with it's wiring classes. Since most user's will want to adjust and possibly plug an own implementation of a certain function (seems to be the case for complex projects) the typical xml file is not part of jar distribution.

### Available modules
- __spring-soy-view__ - this module is the core of the library, contains the main functionality including SoyTemplateFilesResolver 
- __spring-soy-view-ajax-compiler__ - this module contains ajax compiler, it makes sense to use this modue only if you want to compile soy files to JavaScript
- __spring-soy-view-min-google__ - this module allows minimification of soy to javascript code using google closure library. It makes sense to use this module only together with ajax-compiler module.
- __spring-soy-view-min-yahoo__ - this module allows minimification of soy to javascript code using yahoo library. It makes sense to use this module only together with ajax-compiler module. Both google and yahoo libraries are mutually exclusive, it doesn't makse sense to use both at the same time

### Maven configuration

All artefacts have been pushed to maven central repository:

### pom.xml

<code>

        <dependency>
            <groupId>pl.matisoft</groupId>
            <artifactId>spring-soy-view</artifactId>
            <version>1.20.0</version>
        </dependency>

        <dependency>
            <groupId>pl.matisoft</groupId>
            <artifactId>spring-soy-view-ajax-compiler</artifactId>
            <version>1.20.0</version>
        </dependency>

        <dependency>
            <groupId>pl.matisoft</groupId>
            <artifactId>spring-soy-view-min-google</artifactId>
            <version>1.20.0</version>
        </dependency>

        <dependency>
            <groupId>pl.matisoft</groupId>
            <artifactId>spring-soy-view-min-yahoo</artifactId>
            <version>1.20.0</version>
        </dependency>

</code>

### XML Configuration Example
To use spring-soy-view via XML in Spring it is recommended to take this template as an example and adjust it accordingly. This example is taken from a real project and adjusted for the purpouses of this guide.

### spring-soy-view.xml

<code>

    <bean id="soyViewConfig" class="pl.matisoft.soy.config.SoyViewConfig">
        <property name="debugOn" value="${myapp.dev.mode.on}" />
    </bean>

    <bean id="globalCompileVariablesProvider" class="pl.matisoft.soy.global.compile.DefaultCompileTimeGlobalModelResolver">
        <property name="data">
            <map>
                <entry key="siteUrl" value="http://www.mysite.com" />
            </map>
        </property>
    </bean>

    <bean id="localeProvider" class="pl.matisoft.soy.locale.SpringLocaleProvider" />

    <bean id="globalRuntimeVariablesProvider" class="pl.matisoft.soy.global.runtime.DefaultGlobalModelResolver">
        <property name="resolvers">
            <list>
                <bean class="pl.matisoft.soy.global.runtime.resolvers.RequestParametersResolver" />
                <bean class="pl.matisoft.soy.global.runtime.resolvers.RequestHeadersResolver" />
                <bean class="pl.matisoft.soy.global.runtime.resolvers.CookieResolver" />
            </list>
        </property>
    </bean>

    <bean id="templateFileResolver" class="pl.matisoft.soy.template.DefaultTemplateFilesResolver">
        <property name="debugOn">
            <bean class="org.springframework.beans.factory.config.PropertyPathFactoryBean">
                <property name="targetBeanName" value="soyViewConfig" />
                <property name="propertyPath" value="debugOn" />
            </bean>
        </property>
        <property name="templatesLocation" value="/WEB-INF/templates" />
    </bean>

    <bean id="tofuCompiler" class="pl.matisoft.soy.compile.DefaultTofuCompiler">
        <property name="compileTimeGlobalModelResolver" ref="globalCompileVariablesProvider" />
        <property name="debugOn">
            <bean class="org.springframework.beans.factory.config.PropertyPathFactoryBean">
                <property name="targetBeanName" value="soyViewConfig" />
                <property name="propertyPath" value="debugOn" />
            </bean>
        </property>
    </bean>

    <bean id="soyMsgBundleResolver" class="pl.matisoft.soy.bundle.DefaultSoyMsgBundleResolver">
        <property name="messagesPath" value="xliffs/messages" />
    </bean>

    <bean id="toSoyDataConverter" class="pl.matisoft.soy.data.DefaultToSoyDataConverter" />

    <bean id="soyRenderer" class="pl.matisoft.soy.render.DefaultTemplateRenderer">
        <property name="debugOn">
            <bean class="org.springframework.beans.factory.config.PropertyPathFactoryBean">
                <property name="targetBeanName" value="soyViewConfig" />
                <property name="propertyPath" value="debugOn" />
            </bean>
        </property>
        <property name="toSoyDataConverter" ref="toSoyDataConverter" />
    </bean>

    <bean id="compiledTemplatesHolder" class="pl.matisoft.soy.holder.DefaultCompiledTemplatesHolder">
        <property name="debugOn">
            <bean class="org.springframework.beans.factory.config.PropertyPathFactoryBean">
                <property name="targetBeanName" value="soyViewConfig" />
                <property name="propertyPath" value="debugOn" />
            </bean>
        </property>
        <property name="preCompileTemplates" value="false" />
        <property name="tofuCompiler" ref="tofuCompiler" />
        <property name="templatesFileResolver" ref="templateFileResolver" />
    </bean>

    <bean id="viewResolver" class="pl.matisoft.soy.SoyTemplateViewResolver">
        <property name="compiledTemplatesHolder" ref="compiledTemplatesHolder" />
        <property name="templateRenderer" ref="soyRenderer" />
        <property name="debugOn">
            <bean class="org.springframework.beans.factory.config.PropertyPathFactoryBean">
                <property name="targetBeanName" value="soyViewConfig" />
                <property name="propertyPath" value="debugOn" />
            </bean>
        </property>
        <property name="modelAdjuster">
            <bean class="pl.matisoft.soy.data.adjust.SpringModelAdjuster" />
        </property>
        <property name="localeProvider" ref="localeProvider" />
        <property name="globalModelResolver" ref="globalRuntimeVariablesProvider" />
        <property name="soyMsgBundleResolver" ref="soyMsgBundleResolver" />
    </bean>

    <bean id="hashFileGenerator" class="pl.matisoft.soy.ajax.hash.MD5HashFileGenerator">
        <property name="debugOn">
            <bean class="org.springframework.beans.factory.config.PropertyPathFactoryBean">
                <property name="targetBeanName" value="soyViewConfig" />
                <property name="propertyPath" value="debugOn" />
            </bean>
        </property>
    </bean>

    <bean id="templateUrlComposer" class="pl.matisoft.soy.ajax.url.DefaultTemplateUrlComposer">
        <property name="hashFileGenerator" ref="hashFileGenerator" />
        <property name="templateFilesResolver" ref="templateFileResolver" />
        <property name="siteUrl" value="http://www.mysite.com" />
    </bean>

    <bean id="ajaxSoyController" class="pl.matisoft.soy.ajax.SoyAjaxController">
        <property name="localeProvider" ref="localeProvider" />
        <property name="soyMsgBundleResolver" ref="soyMsgBundleResolver" />
        <property name="templateFilesResolver" ref="templateFileResolver" />
        <property name="tofuCompiler" ref="tofuCompiler" />
        <property name="debugOn">
            <bean class="org.springframework.beans.factory.config.PropertyPathFactoryBean">
                <property name="targetBeanName" value="soyViewConfig" />
                <property name="propertyPath" value="debugOn" />
            </bean>
        </property>
        <property name="cacheControl" value="public, max-age=86400" /> <!-- one year -->
        <property name="expiresHeaders" value="Mon, 16 May 2050 20:00:00 GMT" />
        <property name="outputProcessors">
            <list>
                <bean class="pl.matisoft.soy.ajax.process.google.GoogleClosureOutputProcessor" />
            </list>
        </property>
        <property name="authManager">
            <bean class="pl.matisoft.soy.ajax.auth.ConfigurableAuthManager">
                <property name="allowedTemplates">
                    <list>
                        <value>ajax_search_makes</value>
                        <value>ajax_search_models</value>
                        <value>ajax_check_distance</value>
                        <value>ajax_adnav</value>
                        <value>ajax_similar_ads</value>
                        <value>ajax_google_ad_info</value>
                    </list>
                </property>
            </bean>
        </property>
    </bean>

</code>

### Java Bean Config
```

package pl.matisoft.soy.example;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;
import pl.matisoft.soy.SoyTemplateViewResolver;
import pl.matisoft.soy.ajax.SoyAjaxController;
import pl.matisoft.soy.ajax.auth.AuthManager;
import pl.matisoft.soy.ajax.auth.ConfigurableAuthManager;
import pl.matisoft.soy.ajax.hash.HashFileGenerator;
import pl.matisoft.soy.ajax.hash.MD5HashFileGenerator;
import pl.matisoft.soy.ajax.process.OutputProcessor;
import pl.matisoft.soy.ajax.process.google.GoogleClosureOutputProcessor;
import pl.matisoft.soy.ajax.url.DefaultTemplateUrlComposer;
import pl.matisoft.soy.ajax.url.TemplateUrlComposer;
import pl.matisoft.soy.bundle.EmptySoyMsgBundleResolver;
import pl.matisoft.soy.compile.DefaultTofuCompiler;
import pl.matisoft.soy.compile.TofuCompiler;
import pl.matisoft.soy.data.DefaultToSoyDataConverter;
import pl.matisoft.soy.data.ToSoyDataConverter;
import pl.matisoft.soy.global.compile.CompileTimeGlobalModelResolver;
import pl.matisoft.soy.global.compile.EmptyCompileTimeGlobalModelResolver;
import pl.matisoft.soy.global.runtime.DefaultGlobalModelResolver;
import pl.matisoft.soy.global.runtime.GlobalModelResolver;
import pl.matisoft.soy.global.runtime.resolvers.*;
import pl.matisoft.soy.holder.CompiledTemplatesHolder;
import pl.matisoft.soy.holder.DefaultCompiledTemplatesHolder;
import pl.matisoft.soy.locale.LocaleProvider;
import pl.matisoft.soy.locale.SpringLocaleProvider;
import pl.matisoft.soy.render.DefaultTemplateRenderer;
import pl.matisoft.soy.render.TemplateRenderer;
import pl.matisoft.soy.template.DefaultTemplateFilesResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"pl.matisoft.soy.example"})
public class SoyConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private javax.servlet.ServletContext context;

    private boolean debugOn = false;

    @Bean
    public DefaultTemplateFilesResolver templateFilesResolver() throws Exception {
        final DefaultTemplateFilesResolver defaultTemplateFilesResolver = new DefaultTemplateFilesResolver();
        defaultTemplateFilesResolver.setDebugOn(debugOn);
        defaultTemplateFilesResolver.setRecursive(true);
        defaultTemplateFilesResolver.setServletContext(context);
        defaultTemplateFilesResolver.afterPropertiesSet();

        return defaultTemplateFilesResolver;
    }

    @Bean
    public CompileTimeGlobalModelResolver compileTimeGlobalModelResolver() {
        return new EmptyCompileTimeGlobalModelResolver();
    }

    @Bean
    public GlobalModelResolver globalModelResolver() {
        final DefaultGlobalModelResolver defaultGlobalModelResolver = new DefaultGlobalModelResolver();
        defaultGlobalModelResolver.setResolvers(Lists.newArrayList(
                new RequestParametersResolver(),
                new RequestHeadersResolver(),
                new CookieResolver(),
                requestContextResolver(),
                new HttpSessionResolver(),
                webApplicationContextResolver(),
                servletContextResolver()
        ));

        return defaultGlobalModelResolver;
    }

    @Bean
    public ServletContextResolver servletContextResolver() {
        return new ServletContextResolver();
    }

    @Bean
    public WebApplicationContextResolver webApplicationContextResolver() {
        return new WebApplicationContextResolver();
    }

    @Bean
    public RequestContextResolver requestContextResolver() {
        return new RequestContextResolver();
    }

    @Bean
    public LocaleProvider localeProvider() {
        return new SpringLocaleProvider();
    }

    @Bean
    public EmptySoyMsgBundleResolver soyMsgBundleResolver() {
        return new EmptySoyMsgBundleResolver();
    }

    @Bean
    public ToSoyDataConverter toSoyDataConverter() {
        return new DefaultToSoyDataConverter();
    }

    public TemplateRenderer templateRenderer() {
        final DefaultTemplateRenderer defaultTemplateRenderer = new DefaultTemplateRenderer();
        defaultTemplateRenderer.setToSoyDataConverter(toSoyDataConverter());
        defaultTemplateRenderer.setDebugOn(debugOn);

        return defaultTemplateRenderer;
    }

    @Bean
    public TofuCompiler tofuCompiler() {
        final DefaultTofuCompiler defaultTofuCompiler = new DefaultTofuCompiler();
        defaultTofuCompiler.setCompileTimeGlobalModelResolver(compileTimeGlobalModelResolver());
        defaultTofuCompiler.setDebugOn(debugOn);

        return defaultTofuCompiler;
    }

    @Bean
    public ViewResolver viewResolver() throws Exception {
        final SoyTemplateViewResolver soyTemplateViewResolver = new SoyTemplateViewResolver();
        soyTemplateViewResolver.setDebugOn(debugOn);
        soyTemplateViewResolver.setTemplateRenderer(templateRenderer());
        soyTemplateViewResolver.setGlobalModelResolver(globalModelResolver());
        soyTemplateViewResolver.setLocaleProvider(localeProvider());
        soyTemplateViewResolver.setSoyMsgBundleResolver(soyMsgBundleResolver());
        soyTemplateViewResolver.setCompiledTemplatesHolder(compiledTemplatesHolder());

        return soyTemplateViewResolver;
    }

    @Bean
    public CompiledTemplatesHolder compiledTemplatesHolder() throws Exception {
        final DefaultCompiledTemplatesHolder defaultCompiledTemplatesHolder = new DefaultCompiledTemplatesHolder();
        defaultCompiledTemplatesHolder.setDebugOn(debugOn);
        defaultCompiledTemplatesHolder.setTemplatesFileResolver(templateFilesResolver());
        defaultCompiledTemplatesHolder.setTofuCompiler(tofuCompiler());

        return defaultCompiledTemplatesHolder;
    }

    @Bean
    public OutputProcessor closureCompilerProcessor() {
        return new GoogleClosureOutputProcessor();
    }

    @Bean
    public ViewResolver contentNegotiatingViewResolver() throws Exception {
        final ContentNegotiatingViewResolver contentNegotiatingViewResolver = new ContentNegotiatingViewResolver();
        contentNegotiatingViewResolver.setViewResolvers(Lists.newArrayList(viewResolver()));
        contentNegotiatingViewResolver.setDefaultViews(Lists.<View>newArrayList(new MappingJacksonJsonView()));

        return contentNegotiatingViewResolver;
    }

    @Bean
    public TemplateUrlComposer templateUrlComposer() throws Exception {
        final DefaultTemplateUrlComposer urlComposer = new DefaultTemplateUrlComposer();
        urlComposer.setSiteUrl("http://localhost:8080/spring-soy-view-example/app");
        urlComposer.setTemplateFilesResolver(templateFilesResolver());
        urlComposer.setHashFileGenerator(hashFileGenerator());

        return urlComposer;
    }

    @Bean
    public HashFileGenerator hashFileGenerator() {
        final MD5HashFileGenerator md5HashFileGenerator = new MD5HashFileGenerator();
        md5HashFileGenerator.setDebugOn(debugOn);
        md5HashFileGenerator.afterPropertiesSet();

        return md5HashFileGenerator;
    }

    @Bean
    public SoyAjaxController soyAjaxController() throws Exception {
        final SoyAjaxController soyAjaxController = new SoyAjaxController();
        soyAjaxController.setDebugOn(debugOn);
        soyAjaxController.setLocaleProvider(localeProvider());
        soyAjaxController.setSoyMsgBundleResolver(soyMsgBundleResolver());
        soyAjaxController.setTemplateFilesResolver(templateFilesResolver());
        soyAjaxController.setTofuCompiler(tofuCompiler());
        soyAjaxController.setOutputProcessors(Lists.newArrayList(closureCompilerProcessor()));
        soyAjaxController.setAuthManager(authManager());

        return soyAjaxController;
    }

    @Bean
    public AuthManager authManager() {
        return new ConfigurableAuthManager(Lists.newArrayList("templates/client-words.soy", "templates/server-time.soy"));
    }
}

```

## ChangeLog

### 1.13.0
* support for js obfuscation and combination in SoyAjaxController using Google Closure Library, e.g. via:
http://localhost:8080/spring-soy-view-example/app/soy/server-time.soy,client-words.soy
Possible to extend this and provide own output processors implementations
* normalize slashes to dots, remove starting and ending slashes, and provide default view name.
* pom.xml: scope changed to <compile> for major big libraries
* if a template fails to compile, throw an exception and propagate rather than swallow it
* cosmetic fixes

### 1.13.1
* support for yahoo and google closure obfuscation
* separated google and yahoo obfuscation as optional maven modules

### 1.13.2
* SoyAjaxController can be more secure by injecting AuthManager, which controls which soy files can be compiled to js files
* It should be possible to pass *.js extension to SoyAjaxController just for convenience, it is still better to use it without extension
* Moved classes to an existing ajax subpackage as this was incorrectly grouped as a library code, where as it is ajax controller specific code 

### 1.13.3
* Bug fix for GoogleClosureOutputProcessor -> it was not thread safe as promised, which resulted in many ConcurrentModificationExceptions in prod
* Removed a strange DDOS check, we need to think over how to protect for this type of attack

### 1.14.0
* some changes are not backwards compatible, migration easy.
* Many unit tests + tweaks and small bugs detected as part of unit test coverage - still many more to come soon
* Added JavaDocs
* Remove strange check in SoyTemplateViewResolver if a view is a html view
* Introduced Guava cache instead of ConcurrentHashMap in number of classes to prevent DDOS attack (Guava cache was added only in those classes in which data could leak due to DDOS)
* Added PrependAppendOutputProcessor and unit test for it. This class allows an arbitrary text to be added before and after the compiled JavaScript soy template, e.g. defining require js module
* Added cobertura maven plugin to produce code coverage reports
* Tweaks in DefaultSoyMsgBundleResolver to get rid of static variable for a static cache
* Removed example project and will be moving this to another project
* Extracted ajax-compiler as a separate project as some people may not need to use this but may want to use the core library
* Changed the interface of SoyAjaxController, now the endpoint will be e.g. /soy/compileJs?hash=xxx&file=templates/abc.soy&file=templates/abc2.soy
* DefaultTemplateFilesResolver supports full paths, e.g. templates/abc.soy rather than relaying on an unique file name (may not work under windows yet)
* SoyAjaxController has been greatly improved, support for .js and without extension has been dropped to simplify code
* SoyAjaxController will now not show stack traces but http error codes and messages (it was a security loop hole)
* Ability to provide a locale as a request parameter to SoyAjaxController other then the one resolved by LocaleProvider
* Ability to disable obfuscation via request parameter: ?disableProcessors=true parameter passed as a param to compileJs endpoint

### 1.20.0
* some changes are not backwards compatible, migration easy though
* it will be necessary to append soy: prefix for templates to be resolver properly, e.g. "soy:soy.example.serverTime" instead of just "soy.example.serverTime", a Soy template resolver needed to be changed
and now if it won't be able to match on soy template, it will delegate to other template resolvers in the chain
* Moved some implementation from upper classes and simplified inheritance hierarchy, since soy does not allow to pass parameters from views to methods, a lot of implementation is no longer valid for us
* moved some of the implementation from upper classes of Spring's TemplateFileResolver and AbstractTemplateView to RuntimeResolvers, basically now one can use number of RuntimeResolvers to provide global runtime data
* Modified interface of GlobalModelResolver that is now more generic, takes HttpServletRequest, HttpServletResponse and Map<String,Object> model
* SoyTemplateViewResolver now extends from Spring's AbstractCachingViewResolver, which means we were able to get rid of two upper classes in inheritance hierarchy, in which some abstractions did not apply to us
* SoyView now doesn't extend from Spring's abstract classes but needless to say implements Spring's view interface
* Introduced a new concept of CompiledTemplatesHolder, this way we can prevent compilation of all templates to take place multiple times per site, i.e. as many as there are pages (SoyView instances)

#### Known issues:
* SoyAjaxController may not work under windows server, i.e. it may only work with linux style file paths

#### Example project:
* https://github.com/mati1979/spring-soy-view-example

author: Mateusz Szczap<br>
<mati@sz.home.pl>
