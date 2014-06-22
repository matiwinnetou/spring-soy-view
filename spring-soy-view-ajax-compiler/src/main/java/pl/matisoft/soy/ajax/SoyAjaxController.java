package pl.matisoft.soy.ajax;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.template.soy.msgs.SoyMsgBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import pl.matisoft.soy.ajax.auth.AuthManager;
import pl.matisoft.soy.ajax.auth.PermissableAuthManager;
import pl.matisoft.soy.ajax.process.OutputProcessor;
import pl.matisoft.soy.ajax.utils.I18nUtils;
import pl.matisoft.soy.ajax.utils.PathUtils;
import pl.matisoft.soy.bundle.EmptySoyMsgBundleResolver;
import pl.matisoft.soy.bundle.SoyMsgBundleResolver;
import pl.matisoft.soy.compile.EmptyTofuCompiler;
import pl.matisoft.soy.compile.TofuCompiler;
import pl.matisoft.soy.config.SoyViewConfigDefaults;
import pl.matisoft.soy.locale.EmptyLocaleProvider;
import pl.matisoft.soy.locale.LocaleProvider;
import pl.matisoft.soy.template.EmptyTemplateFilesResolver;
import pl.matisoft.soy.template.TemplateFilesResolver;

import javax.annotation.PostConstruct;
import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@ThreadSafe
public class SoyAjaxController {

    private final static int DEF_CACHE_MAX_SIZE = 10000;

    private final static String DEF_TIME_UNIT = "DAYS";

    private final static int DEF_EXPIRE_AFTER_WRITE = 1;

    private static final Logger logger = LoggerFactory.getLogger(SoyAjaxController.class);

    private String cacheControl = "no-cache";

    private String expiresHeaders = "";

    /** maximum number of entries this cache will hold */
    private int cacheMaxSize = DEF_CACHE_MAX_SIZE;

    /** number of time units after which once written entries will expire */
    private int expireAfterWrite = DEF_EXPIRE_AFTER_WRITE;

    /** String used to denote a TimeUnit */
    private String expireAfterWriteUnit = DEF_TIME_UNIT;

    /**
     * This is a compiled to javascript cache (compiled soy templates), which consists of key: hash
     * and as a value we have a Map<String,String>
     * In this map, a key is an array to path, example: server-time,client-words and value:
     * is a String with compiled template.
     * To prevent DDOS attack we model the first cache as a limited cache with maximum number of entries
     * and also an expire after write to cache
     */
    private Cache<String, Map<String,String>> cachedJsTemplates = CacheBuilder.newBuilder()
            .expireAfterWrite(expireAfterWrite, TimeUnit.valueOf(expireAfterWriteUnit))
            .maximumSize(cacheMaxSize)
            .concurrencyLevel(1) //look up a constant class, 1 is not very clear
            .build();

    private TemplateFilesResolver templateFilesResolver = new EmptyTemplateFilesResolver();

    private TofuCompiler tofuCompiler = new EmptyTofuCompiler();

    private SoyMsgBundleResolver soyMsgBundleResolver = new EmptySoyMsgBundleResolver();

    private LocaleProvider localeProvider = new EmptyLocaleProvider();

    /**
     * whether debug is on or off, if it is on then caching of entries will not work
     * to support hot reloading while developing, if it is on, then we assume it is
     * like production mode and caching of compiled soy to JavaScript templates
     * will be working. In addition in production mode (debug off)
     * CacheControl (Http 1.1) and Expires (Http 1.0) http headers
     * will be set to user configured values.
     */
    private boolean hotReloadMode = SoyViewConfigDefaults.DEFAULT_HOT_RELOAD_MODE;

    /**
     * character encoding, by default utf-8
     */
    private String encoding = SoyViewConfigDefaults.DEFAULT_ENCODING;

    /**
     * List of output processors, output processors typically perform obfuscation
     * of generated JavaScript code
     */
    private List<OutputProcessor> outputProcessors = new ArrayList<OutputProcessor>();

    /**
     * By default there is no AuthManager and an external user can compile all templates to JavaScript
     * This can pose security risk and therefore it is possible to change this and inject
     * an AuthManager implementation that will only allow to compile those templates that a developer agreed to.
     */

    private AuthManager authManager = new PermissableAuthManager();

    public SoyAjaxController() {
    }

    @PostConstruct
    public void init() {
        this.cachedJsTemplates = CacheBuilder.newBuilder()
                .expireAfterWrite(expireAfterWrite, TimeUnit.valueOf(expireAfterWriteUnit))
                .maximumSize(cacheMaxSize)
                .concurrencyLevel(1) //look up a constant class, 1 is not very clear
                .build();
    }

    /**
     * An endpoint to compile an array of soy templates to JavaScript.
     *
     * This endpoint is a preferred way of compiling soy templates to JavaScript but it requires a user to compose a url
     * on their own or using a helper class TemplateUrlComposer, which calculates checksum of a file and puts this in url
     * so that whenever a file changes, after a deployment a JavaScript, url changes and a new hash is appended to url, which enforces
     * getting of new compiles JavaScript resource.
     *
     * Invocation of this url may throw two types of http exceptions:
     * 1. notFound - usually when a TemplateResolver cannot find a template with an associated name
     * 2. error - usually when there is a permission error and a user is not allowed to compile a template into a JavaScript
     *
     * @param hash - some unique number that should be used when we are caching this resource in a browser and we use http cache headers
     * @param templateFileNames - an array of template names, e.g. client-words,server-time, which may or may not contain extension
     *                          currently three modes are supported - soy extension, js extension and no extension, which is preferred
     * @param disableProcessors - whether the controller should run registered outputProcessors after the compilation is complete.
     * @param request - HttpServletRequest
     * @param locale - locale
     * @return response entity, which wraps a compiled soy to JavaScript files.
     * @throws IOException - io error
     */
    @RequestMapping(value="/soy/compileJs", method=GET)
    public ResponseEntity<String> compile(@RequestParam(required = false, value="hash", defaultValue = "") final String hash,
                                                            @RequestParam(required = true, value = "file") final String[] templateFileNames,
                                                            @RequestParam(required = false, value = "locale") String locale,
                                                            @RequestParam(required = false, value = "disableProcessors", defaultValue = "false") String disableProcessors,
                                                            final HttpServletRequest request) throws IOException {
        return compileJs(templateFileNames, hash, new Boolean(disableProcessors).booleanValue(), request, locale);
    }

     private ResponseEntity<String> compileJs(final String[] templateFileNames,
                                             final String hash,
                                             final boolean disableProcessors,
                                             final HttpServletRequest request,
                                             final String locale
    ) throws IOException {
        Preconditions.checkNotNull(templateFilesResolver, "templateFilesResolver cannot be null");

        if (isHotReloadModeOff()) {
            final Optional<String> template = extractAndCombineAll(hash, templateFileNames);
            if (template.isPresent()) {
                return prepareResponseFor(template.get(), disableProcessors);
            }
        }

        try {
            final Map<URL,String> compiledTemplates = compileTemplates(templateFileNames, request, locale);
            final Optional<String> allCompiledTemplates = concatCompiledTemplates(compiledTemplates);
            if (!allCompiledTemplates.isPresent()) {
                throw notFound("Template file(s) could not be resolved.");
            }
            if (isHotReloadModeOff()) {
                synchronized (cachedJsTemplates) {
                    Map<String, String> map = cachedJsTemplates.getIfPresent(hash);
                    if (map == null) {
                        map = new ConcurrentHashMap<String, String>();
                    } else {
                        map.put(PathUtils.arrayToPath(templateFileNames), allCompiledTemplates.get());
                    }
                    this.cachedJsTemplates.put(hash, map);
                }
            }

            return prepareResponseFor(allCompiledTemplates.get(), disableProcessors);
        } catch (final SecurityException ex) {
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (final HttpClientErrorException ex) {
            return new ResponseEntity<String>(ex.getMessage(), ex.getStatusCode());
        }
    }

    private Optional<String> extractAndCombineAll(final String hash, final String[] templateFileNames) throws IOException {
        synchronized (cachedJsTemplates) {
            final Map<String, String> map = cachedJsTemplates.getIfPresent(hash);
            if (map != null) {
                final String template = map.get(PathUtils.arrayToPath(templateFileNames));

                return Optional.fromNullable(template);
            }
        }

        return Optional.absent();
    }

    private Map<URL,String> compileTemplates(final String[] templateFileNames, final HttpServletRequest request, final String locale) {
        final HashMap<URL,String> map = new HashMap<URL,String>();
        for (final String templateFileName : templateFileNames) {
            try {
                final Optional<URL> templateUrl = templateFilesResolver.resolve(templateFileName);
                if (!templateUrl.isPresent()) {
                    throw notFound("File not found:" + templateFileName);
                }
                if (!authManager.isAllowed(templateFileName)) {
                    throw error("no permission to compile:" + templateFileName);
                }
                logger.debug("Compiling JavaScript template:" + templateUrl.orNull());
                final Optional<String> templateContent = compileTemplateAndAssertSuccess(request, templateUrl, locale);
                if (!templateContent.isPresent()) {
                    throw notFound("file cannot be compiled:" + templateUrl);
                }

                map.put(templateUrl.get(), templateContent.get());
            } catch (final IOException e) {
                throw error("Unable to find file:" + templateFileName + ".soy");
            }
        }

        return map;
    }

    private Optional<String> concatCompiledTemplates(final Map<URL,String> compiledTemplates) throws IOException, SecurityException {
        if (compiledTemplates.isEmpty()) {
            return Optional.absent();
        }

        final StringBuilder allJsTemplates = new StringBuilder();

        for (final String compiledTemplate : compiledTemplates.values()) {
            allJsTemplates.append(compiledTemplate);
        }

        return Optional.of(allJsTemplates.toString());
    }

    private ResponseEntity<String> prepareResponseFor(final String templateContent, final boolean disableProcessors) throws IOException {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/javascript; charset=" + encoding);
        headers.add("Cache-Control", hotReloadMode ? "no-cache" : cacheControl);
        if (StringUtils.hasText(expiresHeaders)) {
            headers.add("Expires", expiresHeaders);
        }

        if (disableProcessors) {
            return new ResponseEntity<String>(templateContent, headers, OK);
        }

        String processTemplate = templateContent;
        try {
            for (final OutputProcessor outputProcessor : outputProcessors) {
                final StringWriter writer = new StringWriter();
                outputProcessor.process(new StringReader(templateContent), writer);
                processTemplate = writer.getBuffer().toString();
            }

            return new ResponseEntity<String>(processTemplate, headers, OK);
        } catch(final Exception ex) {
            logger.warn("Unable to process template", ex);
            return new ResponseEntity<String>(templateContent, headers, OK);
        }
    }

    private Optional<String> compileTemplateAndAssertSuccess(final HttpServletRequest request, final Optional<URL> templateFile, final String locale) throws IOException {
        Preconditions.checkNotNull(localeProvider, "localeProvider cannot be null");
        Preconditions.checkNotNull(soyMsgBundleResolver, "soyMsgBundleResolver cannot be null");
        Preconditions.checkNotNull(tofuCompiler, "tofuCompiler cannot be null");

        if (!templateFile.isPresent()) {
            return Optional.absent();
        }

        Optional<Locale> localeOptional = Optional.fromNullable(I18nUtils.getLocaleFromString(locale));
        if (!localeOptional.isPresent()) {
            localeOptional = localeProvider.resolveLocale(request);
        }

        final Optional<SoyMsgBundle> soyMsgBundle = soyMsgBundleResolver.resolve(localeOptional);
        final Optional<String> compiledTemplate = tofuCompiler.compileToJsSrc(templateFile.orNull(), soyMsgBundle.orNull());

        return compiledTemplate;
    }

    private boolean isHotReloadMode() {
        return hotReloadMode;
    }

    private boolean isHotReloadModeOff() {
        return !hotReloadMode;
    }

    private HttpClientErrorException notFound(final String file) {
        return new HttpClientErrorException(NOT_FOUND, file);
    }

    private HttpClientErrorException error(final String file) {
        return new HttpClientErrorException(INTERNAL_SERVER_ERROR, file);
    }

    public void setCacheControl(final String cacheControl) {
        this.cacheControl = cacheControl;
    }

    public void setTemplateFilesResolver(final TemplateFilesResolver templateFilesResolver) {
        this.templateFilesResolver = templateFilesResolver;
    }

    public void setTofuCompiler(final TofuCompiler tofuCompiler) {
        this.tofuCompiler = tofuCompiler;
    }

    public void setSoyMsgBundleResolver(final SoyMsgBundleResolver soyMsgBundleResolver) {
        this.soyMsgBundleResolver = soyMsgBundleResolver;
    }

    public void setLocaleProvider(final LocaleProvider localeProvider) {
        this.localeProvider = localeProvider;
    }

    public void setHotReloadMode(final boolean hotReloadMode) {
        this.hotReloadMode = hotReloadMode;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public void setExpiresHeaders(final String expiresHeaders) {
        this.expiresHeaders = expiresHeaders;
    }

    public void setOutputProcessors(List<OutputProcessor> outputProcessors) {
        this.outputProcessors = outputProcessors;
    }

    public void setAuthManager(AuthManager authManager) {
        this.authManager = authManager;
    }

    public void setCacheMaxSize(int cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
    }

    public void setExpireAfterWrite(int expireAfterWrite) {
        this.expireAfterWrite = expireAfterWrite;
    }

    public void setExpireAfterWriteUnit(String expireAfterWriteUnit) {
        this.expireAfterWriteUnit = expireAfterWriteUnit;
    }

}
