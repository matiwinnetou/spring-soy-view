#Introduction

Implementation of Spring's MVC View Resolver using Google Closure (soy)

It is highly customizable via pluggable interfaces, for which a default implementation is provided.
It distinguishes itself from other frameworks, which are often "black boxes" and do not allow easy customisation.
It was created due to specific requirements, which no other framework at that time could provide.

#### Features
* POJO rendering and flat structure rendering (ToSoyDataConverter interface)
* I18N - SoyMsgBundle based on resolvable locale (SoyMsgBundleResolver interface)
* Soy Global variables ($ij) supported with many out of the box runtime data resolvers (e.g. http session, request parameters, request headers, servlet context, etc) via (GlobalModelResolver interface)
* Soy Compile time global variables supported (CompileTimeGlobalModelResolver interface)
* Debug flag support, allows editing and changing soy files in dev mode with immediate reflection and recompilation of soy
* Ability to provide own template file resolver (TemplateFilesResolver interface)
* Model transformation available (ModelAdjuster interface) since spring wraps the real backing model in it's own map  
* JavaScript compilation via AJAX endpoint using a Spring Controller (SoyAjaxController)
* JavaScript compilation which supports browser caching (hash url part)
* JavaScript obfuscation (via either google closure or yahoo js obfuscator) and combination of generated urls via Ajax
* It allows to work in a "black-box" (auto import beans) or "white-box" mode, fine tune a configuration and manually create and wire beans

#### Google Closure Soy

Google Closure Soy is an implementation of logic-less templates by Google. Templates can be used on the server side and
on the client side using soy to JavaScript compiler.

##### Example:

```
{namespace soy.example}

/**
 * @param words The words to display
 */
{template .clientWords}
  <ul>
    {foreach $word in $words}
      <li>{$word}</li>
    {/foreach}
  </ul>
{/template}
```

# User's Guide

To use Spring-Soy View in "white-box" mode one has to manually wire beans. Conversely to use library in "back-box" mode all you have to do is to import pertinent @Configuration objects with bean definitions.

First we will look at "black-box" configuration as it is easier to begin with.
Available @Configuration in library itself:
* pl.matisoft.soy.config.SpringSoyViewBaseConfig (core)
* pl.matisoft.soy.ajax.config.SpringSoyViewAjaxConfig (ajax)

It is important to notice that if you decide to import SpringSoyViewAjaxConfig then you will automatically import SpringSoyViewBaseConfig since there is a dependency on it.
It makes sense to use SpringSoyViewAjaxConfig only if you are interested in ajax compiler, which is effectively a spring controller compiles soy templates to javascript at runtime.


### Example import usage: (Black-Box Mode)

```java
@Configuration
@Import(SpringSoyViewAjaxConfig.class)
@PropertySource("classpath:spring-soy-view-example.properties")
@EnableWebMvc
@ComponentScan(basePackages = {"pl.matisoft.soy.example"})
public class SoyConfiguration extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    @Primary
    public ViewResolver contentNegotiatingViewResolver(final ViewResolver soyViewResolver) throws Exception {
        final ContentNegotiatingViewResolver contentNegotiatingViewResolver = new ContentNegotiatingViewResolver();
        contentNegotiatingViewResolver.setViewResolvers(Lists.newArrayList(soyViewResolver));
        contentNegotiatingViewResolver.setDefaultViews(Lists.<View>newArrayList(new MappingJacksonJsonView()));

        return contentNegotiatingViewResolver;
    }

}

```

### Configurable Parameters (Black-Box Mode) - SpringSoyViewBaseConfig - core
* ${soy.hot.reload.mode:false} - supports developer and recompiles soy templates on every page access (slow but no reason to restart application on soy file change)
* ${soy.templates.resolve.recursively:true} - whether soy files should be recursively resolved based on configured: ${soy.templates.directory}
* ${soy.templates.file.extension:soy} - a soy file extension
* ${soy.templates.directory:/WEB-INF/templates} - directory to look for template files
* ${soy.i18n.xliff.path:xliffs/messages} - path to xliff messages files (Spring's Resource abstraction)
* ${soy.encoding:utf-8} - encoding of soy templates and output responses
* ${soy.i18n.fallback.to.english:true} - whether it should fallback to english on missing keys for a current locale
* ${soy.preCompile.templates:false} - whether a library should precompile soy templates on startup or lazily on first access
* ${soy.indexView:index} - a default index view - this is used in only special cases (TODO: )
* ${soy.logical.prefix:soy:} - a prefix that is used to uniquely identify a logical soy template name
* ${soy.resolver.order:2147483647} - order of this ViewResolver

### Configurable Parameters (Black-Box Mode) - SpringSoyViewBaseConfig - ajax
* ${soy.hot.reload.mode:false} - supports developer and recompiles soy templates to java script otherwise it caches them
* ${soy.site.url:} - points to absolute site urls, used only for an optional TemplateComposer, which most likely will be deprecated in next release
* ${soy.cache.busting.cache.control:public, max-age=86400} - one year cache control, which is only used in production mode (not hot reload mode)
* ${soy.encoding:utf-8} - http response encoding

As you can see, we are using @Bean @Primary annotation to override the implementation which is imported from the file. In case of negotiatingViewResolver, we need to do that
because ViewResolver is already provided within a SpringSoyViewAjaxConfig inside a library. The ViewResolver which is passed as a function argument is in fact a soyViewResolver thanks
to Spring dependency resolution.

In white-box mode the configuration may seem to be verbose but the advantage is that a developer is in full control which beans get instiantiated and wired up together.
It is recommended that beginner users start with black-box approach and progress to white-box approach if they wish to be in full control over instantiated beans.

### Available maven modules

* __spring-soy-view__ - this module is the core of the library, contains the main functionality including SoyTemplateFilesResolver 
* __spring-soy-view-ajax-compiler__ - this module contains ajax compiler, it makes sense to use this modue only if you want to compile soy files to JavaScript. This modules includes Yahoo and Google Closure obfuscation support

### Maven configuration

All artefacts have been pushed to maven central repository:

### Maven's pom.xml

```xml

        <dependency>
            <groupId>pl.matisoft</groupId>
            <artifactId>spring-soy-view</artifactId>
            <version>1.25.1</version>
        </dependency>

        <dependency>
            <groupId>pl.matisoft</groupId>
            <artifactId>spring-soy-view-ajax-compiler</artifactId>
            <version>1.25.1</version>
        </dependency>

```

### XML Configuration Example (White-Box Mode)

To use spring-soy-view via XML in Spring it is recommended to take this template as an example and adjust it accordingly. This example is taken from a real project and adjusted for the purposes of this guide.

Gist: [spring-soy-view.xml](https://gist.github.com/mati1979/74e4d2b1c479dbf4a8ec "spring-soy-view.xml") (*updated for: version 1.25.1 *)

### Java Bean Config Example (White-Box Mode)

In the same way it is possible to use XML wiring a better and more modern way to include soy-spring-view library is to use __@Configuration__ annotations from spring.

Gist: [SoyConfiguration.java](https://gist.github.com/mati1979/7291135 "SoyConfiguration.java") (*updated for: version 1.25.1 *)

### Templates location

By default a DefaultTemplatesFileResolver will look for __*.soy__ templates stored inside __/WEB-INF/templates__ folder, it will recursively resolve them, the location of files, an extension can be configured. At any given point in time it is also possible provide own implementation of TemplatesFileResolver if a default one doesn't meet our needs.

### Usage from Spring's Controller
To render templates, one has to use by default (can be customized) a special prefix, i.e. __"soy:"__ followed by a __logical__ template name.

This can be best illustrated by an example:

```java
	@RequestMapping(value="/server-time")
	public String getServerTime(final Model model) {
		return "soy:soy.example.serverTime";
	}
```

As indicated in the above example, this will yield rendering of a template defined in soy.example.serverTime, mind you that it is a logical template name and it doesn't matter in which file it is stored as long as the file is resolveable by a TemplatesFileResolver. In the mentioned example a string with a template name is returned, as it is normal in spring mvc world that when a method returns a string, it indicates a path to a template.

### Spring's Template resolvers chaining

In Spring MVC, templates resolvers can be chained in a sense of __Chain of Responsibility__ pattern (GoF), this is the reason a "soy:" prefix for a template name was introduced,
when __SoyTemplateViewResolver__ cannot see that a template name have this prefix, it is skipping to next handler in the handler chain inside spring (ie. other template view resolvers).

### Compile Time Global Parameters
There are parameters that are static in nature and they can be only changed upon restarts of the application. An example of such parameters could be: project directory, dev mode, google analytics token, etc.
Normally these values are available via Spring's application properties. What you may notice is that from time to time you would like to reference this globally available data from your soy files, those parameters then are available in soy files in the form:
{parameter.name}, note lack of $ sign.

e.g.

```xml
    <bean id="globalCompileVariablesProvider" class="pl.matisoft.soy.global.compile.DefaultCompileTimeGlobalModelResolver">
        <property name="data">
            <map>
                <entry key="app.global.siteUrl" value="http://www.mysite.com" />
            </map>
        </property>
    </bean>
```

Please note it is also possible to inject all spring properties via properties property. The data can be referenced in soy files in the following manner: __{app.global.siteUrl}__

### Internationalization (i18n)
The library supports templates to be in many languages, all you need to do is to reference your language specific messages inside a soy file with a special {msg} tag.

Consider example:
```html
<h3>{msg meaning="I18N.VEHICLE.Further_Technical_Data" desc=""}Further Technical Data{/msg}</h3>
```

Note that at the moment this library requires that you precompile all files that contain {msg} tags into so called xliff format, which is
a standard format that describes internationalized messages.
More information on: http://en.wikipedia.org/wiki/XLIFF

You can extract xliff messages from your soy file using a tool provided by google:
https://developers.google.com/closure/templates/docs/translation

At the moment the library does not contain a maven-plugin that would extract i18n messages and create xliff file(s) for it, it also appears to be outside of it's core functionality.

The library contains a class: __DefaultSoyMsgBundleResolver__, which reads a xliff messages from classpath and makes it accessible to spring-soy-view library. By default a class will read messages
stores in the classpath under: __messages.xlf__ file, this can be configured to reflect project settings.

The library needs to lookup a locale and for this it uses an interface __LocaleProvider__, which comes with number of implementations. What is suggested and recommended is to use __SpringLocaleProvider__, which
using spring resolver will lookup a current locale and load locale's xliff messages to be displayed for a user.

### Developer and Production mode

In order to support hot-reloading of soy files a library supports a developer mode, which is controller via debugOn flag. A name of this property can be misleading but it is for historical reasons.
Usually when this property is set to true on certain classes it means the caching will be disabled, note that you can set a debugOn property on some classes and always leave it set to true or false on others.
This way you can control certain aspects of developer mode, some classes may continue to cache results others will constantly recompile or build pertinent objects.

### Runtime Global Parameters
Google's Soy Templates support a notion of globally injected parameters, which are available under a special namespace: ${ij}, contrary to other spring's view resolver library this library makes use of this and that way this allows us to keep SoyVIew implementation clean. By default an implementation delegates to a __DefaultGlobalModelResolver__, which in turn contains a list of RuntimeResolvers Out of the box the library provides a resolution for the following runtime information:

* __Servlet's servletContext__ via ServletContextDataResolver class and by default data is bound to: __{$ij._servlet.context}__
* __Spring's WebApplicationContext__ via WebApplicationContextDataResolver class and by default data is bound to: __{$ij._web.app.context}__
* __Http cookies__ via CookieDataResolver and by default data is bound to: __{$ij._request.cookie}__
* __Http session__ data via HttpSessionDataResolver and by default data is bound to: __{$ij._http.session}__. Please note this runtime resolver does not resolve objects only primitives, it is therefore needed that you extend from this runtime resolver
* __Spring's RequestContext__ via RequestContextDataResolver and data bound to: __{$ij._request.context}__
* __Request parameters__ via RequestParametersDataResolver and data bound to: __{$ij._request.parameter}__
* __Request headers__ via RequestHeadersDataResolver and data bound to: __{$ij._request.header}__

Note that since by design a Soy __does not__ allow any code from the templates to be executes in java (without writing a plugin for it), all of those implementation support only getting data without input parameters.

It is expected that if you need your own domain specific runtime data resolver, you simply write a new class and implement the interface __pl.matisoft.soy.global.runtime.resolvers.RuntimeResolver__, once done wire this via configuration of __DefaultGlobalModelResolver__

```java
public interface RuntimeDataResolver {

   void resolveData(HttpServletRequest request, HttpServletResponse response, Map<String, ? extends Object> model, SoyMapData root);

}
```

### Model adjuster (ModelAdjuster)

This concept has been created because Spring MVC will often wrap your real domain object inside own model. Soy needs to
access a path to your domain object compiled to soy compatible data structures, not internal spring model.
To use it, just wire __SpringModelAdjuster__ and specify your model property (typically 'model').

### POJO Data Conversion (ToSoyDataConverter)

A library by default will convert your POJO domain objects to soy compatible data structures. A default implementation
of ToSoyDataConverter that will recursively inspect a passed in model and build a nested structure of SoyMapData objects,
which consist only of primitives supported by Soy and thus can be rendered.

Warning: be careful when your domain objects perform an expensive calls, e.g. hibernate db load. ToSoyDataConverter
will __recursively__ invoke getters and is (for booleans) methods automatically to build SoyMapData object structure.

### Ajax JavaScript compilation

This is an optional module, in which compilation of soy files can be done via a Spring MVC controller. Typically, however,
some project may choose to use a maven plugin or grunt task that compiles soy files to javascript or even not
compilation to JavaScript at all (not recommended - usage allows performance tweaks and lazy loading)

To use an ajax compiler it is necessary to wire or include SoyAjaxController in application's configuration files:

```xml
<bean id="ajaxSoyController" class="pl.matisoft.soy.ajax.SoyAjaxController">
        <property name="localeProvider" ref="localeProvider" />
        <property name="soyMsgBundleResolver" ref="soyMsgBundleResolver" />
        <property name="templateFilesResolver" ref="templateFileResolver" />
        <property name="tofuCompiler" ref="tofuCompiler" />
        <property name="hotReloadMode">
            <bean class="org.springframework.beans.factory.config.PropertyPathFactoryBean">
                <property name="targetBeanName" value="soyViewConfig" />
                <property name="propertyPath" value="hotReloadMode" />
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
```

* cacheControl - indicates how long the resource should not expire after it has been compiled. This parameter is only useful if hotReloadMode is false
* expireHeaders - similar to cache control but for HTML 1.0
* outputProcessors - a list of processors that should be applied after js compilation takes place. Out of the box three implementations are provided: __GoogleClosureOutputProcessor__, __YahooOutputProcessor__ and __PrependAppendOutputProcessor__, which allows to append and prepend an arbitrary JavaScript code, which could be useful if one uses __requirejs__.
* authManager - an implementation that allows to specify a list of allowedTemplates to be compiled, this is a security measure so that only certain templates are allowed to be compiled to JavaScript.

An example from a html document:

```html
<script type="text/javascript" src="../bower_components/soyutils/soyutils.js"></script>
<script type="text/javascript" src="soy/compileJs?file=templates/client-words.soy&amp;file=templates/server-time.soy"></script>
```

* In the first line it is necessary to note here that this example uses __required__ soyutils using bower package manager but obviously it can be linked in an _old fashioned_ way. soyutils is available in bower as "soyutils" package
* In second line, we can see a call to soy/compileJs?, which will compile both templates/client-words.soy and templates/server-time.soy using SoyAjaxController. Please note this code will also combine and potentially obfuscate (if OutputProcessor has been configured for this SoyAjaxController)

#### SoyAjaxController supports the following endpoints:
##### __/soy/compileJs__
with the following parameters:

* __hash__ (optional) - indicates a release hash or a hash sum of the file, this is used for cache busting
* __file__ (mandatory) - a list of one or multiple pathd pointing to soy files to be compiled to JavaScript
* __locale__ (optional) - allows to override a default locale provided by __LocaleProvider_, .e.g locale=pl_PL
* __disableProcessors__ (optional) - a boolean, true or false, enabled by default, which controls whether this ajax controller should pass the result to configured output processors

### Google groups forum

If you find issues, cannot understand something, please post a question to a Google Groups forum, I will try to help
you with the problem.

[Google groups forum](https://groups.google.com/forum/#!forum/spring-soy-view "Google groups forum")

### Git pull requests

Git pull requests are welcome and I am happy to discuss and integrate them to the project

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

### 1.25.1
* debugOn is now called: hotReloadMode (it was badly named to begin with)
* RuntimeResolver is now called RuntimeDataResolver (it reflects what it does)
* @Configuration objects are available for spring-soy-view and ajax module, so they can be imported from a main xml or @Configuration file
* google min and yahoo min have been merged into ajax-compiler module. There was a circular dependency problem otherwise.
* Updated example project to reflect new changes
* Injected Data should now be used to generate file hashes for ajax compiler module.

### 1.25.2
* Backwards compatible changes
* Introduced new class ClasspathTemplateResolver as per Pull Request
* Minor improvements to pom.xml files as per Pull Request

### License

Apache License Version 2.0

### Known issues:
* SoyAjaxController may not work under windows, i.e. it may only work with linux style file paths

### Example project (shows Black-Box mode in action)
* https://github.com/mati1979/spring-soy-view-example

author: Mateusz Szczap<br>
<mati@sz.home.pl>
