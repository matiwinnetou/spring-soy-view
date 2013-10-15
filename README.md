#Introduction

Implementation of Spring's MVC View in Google Closure (soy)

It is highly customizable via pluggable interfaces, for which a default implementation is provided.

#### Supported
* POJO rendering and flat structure rendering (ToSoyDataConverter interface)
* I18N - SoyMsgBundle based on resolvable locale (SoyMsgBundleResolver interface)
* JavaScript compilation via AJAX endpoint using a Spring Controller (SoyAjaxController)
* Soy Global variables supported (GlobalModelResolver interface)
* Soy Compile time global variables supported (CompileTimeGlobalModelResolver interface)
* Debug flag support
* Ability to provide own template file resolver (TemplateFilesResolver interface)
* Model transformation available (ModelAdjuster interface)

#### ChangeLog
1.13.0
* support for js minimification and combination in SoyAjaxController using Google Closure Library, e.g. via:
http://localhost:8080/spring-soy-view-example/app/soy/server-time.soy,client-words.soy
Possible to extend this and provide own output processors implementations
* normalize slashes to dots, remove starting and ending slashes, and provide default view name.
* pom.xml: scope changed to <compile> for major big libraries
* if a template fails to compile, throw an exception and propagate rather than swallow it
* cosmetic fixes

1.13.1
* support for yahoo and google closure minimification
* separated google and yahoo min modules

1.13.2
* SoyAjaxController can be more secure by injecting AuthManager, which controls which soy files can be compiled to js files
* It should be possible to pass *.js extension to SoyAjaxController just for convienience, it is still better to use it without extension
* Moved classes to an existing ajax subpackage as this was incorrectly grouped as a library code, where as it is ajax controller specific code 

1.13.3
* Bug fix for GoogleClosureOutputProcessor -> it was not thread safe as promised, which resulted in many ConcurrentModificationExceptions in prod
* Removed a strange DDOS check, we need to think over how to protect for this type of attack

# Running example (dev)
* clone the git repository
* cd spring-soy-view-example
* invoke: mvn jetty:run
* Navigate to http://localhost:8080/spring-soy-view-example/app/

author: Mateusz Szczap<br>
<mati@sz.home.pl>
