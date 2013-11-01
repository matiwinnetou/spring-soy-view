#Introduction

Implementation of Spring's MVC View in Google Closure (soy)

It is highly customizable via pluggable interfaces, for which a default implementation is provided.

#### Supported
* POJO rendering and flat structure rendering (ToSoyDataConverter interface)
* I18N - SoyMsgBundle based on resolvable locale (SoyMsgBundleResolver interface)
* JavaScript compilation via AJAX endpoint using a Spring Controller (SoyAjaxController)
* JavaScript compilation which supports browser caching (hash url part)
* JavaScript obfuscation (via either google closure or yahoo js obfuscator) and combination of generated urls via Ajax
* Soy Global variables supported (GlobalModelResolver interface)
* Soy Compile time global variables supported (CompileTimeGlobalModelResolver interface)
* Debug flag support
* Ability to provide own template file resolver (TemplateFilesResolver interface)
* Model transformation available (ModelAdjuster interface)
* Default implementation that serializes request parameters, cookies and headers to Soy's $ij (Injected Data - Runtime parameters) data.

#### ChangeLog
1.13.0
* support for js obfuscation and combination in SoyAjaxController using Google Closure Library, e.g. via:
http://localhost:8080/spring-soy-view-example/app/soy/server-time.soy,client-words.soy
Possible to extend this and provide own output processors implementations
* normalize slashes to dots, remove starting and ending slashes, and provide default view name.
* pom.xml: scope changed to <compile> for major big libraries
* if a template fails to compile, throw an exception and propagate rather than swallow it
* cosmetic fixes

1.13.1
* support for yahoo and google closure obfuscation
* separated google and yahoo obfuscation as optional maven modules

1.13.2
* SoyAjaxController can be more secure by injecting AuthManager, which controls which soy files can be compiled to js files
* It should be possible to pass *.js extension to SoyAjaxController just for convenience, it is still better to use it without extension
* Moved classes to an existing ajax subpackage as this was incorrectly grouped as a library code, where as it is ajax controller specific code 

1.13.3
* Bug fix for GoogleClosureOutputProcessor -> it was not thread safe as promised, which resulted in many ConcurrentModificationExceptions in prod
* Removed a strange DDOS check, we need to think over how to protect for this type of attack

1.14.0
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

master - will be part of (1.20.0) release
* some changes are not backwards compatible, migration easy though
* it will be necessary to append soy: prefix for templates to be resolver properly, e.g. "soy:soy.example.serverTime" instead of just "soy.example.serverTime", a Soy template resolver needed to be changed
and now if it won't be able to match on soy template, it will delegate to other template resolvers in the chain
* Moved some implementation from upper classes and simplified inheritance hierarchy, since soy does not allow to pass parameters from views to methods, a lot of implementation is no longer valid for us
* moved some of the implementation from upper classes of Spring's TemplateFileResolver and AbstractTemplateView to RuntimeResolvers, basically now one can use number of RuntimeResolvers to provide global runtime data
* Modified interface of GlobalModelResolver that is now more generic, takes HttpServletRequest, HttpServletResponse and Map<String,Object> model
* SoyTemplateViewResolver now extends from Spring's AbstractCachingViewResolver, which means we were able to get rid of two upper classes in inheritance hierarchy, in which some abstractions did not apply to us
* SoyView now doesn't extend from Spring's abstract classes but needless to say implements Spring's view interface

#### Known issues:
* SoyAjaxController may not work under windows server, i.e. it may only work with linux style file paths

#### Example project:
* https://github.com/mati1979/spring-soy-view-example

author: Mateusz Szczap<br>
<mati@sz.home.pl>
