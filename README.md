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

author: Mateusz Szczap<br>
<mati@sz.home.pl>