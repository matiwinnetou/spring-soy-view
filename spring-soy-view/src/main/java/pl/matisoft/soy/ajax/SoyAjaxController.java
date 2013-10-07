package pl.matisoft.soy.ajax;

import javax.annotation.concurrent.ThreadSafe;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.template.soy.msgs.SoyMsgBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import pl.matisoft.soy.bundle.EmptySoyMsgBundleResolver;
import pl.matisoft.soy.bundle.SoyMsgBundleResolver;
import pl.matisoft.soy.compile.EmptyTofuCompiler;
import pl.matisoft.soy.compile.TofuCompiler;
import pl.matisoft.soy.config.SoyViewConfig;
import pl.matisoft.soy.locale.EmptyLocaleProvider;
import pl.matisoft.soy.locale.LocaleProvider;
import pl.matisoft.soy.process.OutputProcessor;
import pl.matisoft.soy.template.EmptyTemplateFilesResolver;
import pl.matisoft.soy.template.TemplateFilesResolver;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@Controller
@ThreadSafe
public class SoyAjaxController {

    private static final Logger logger = LoggerFactory.getLogger(SoyAjaxController.class);

    private String cacheControl = "no-cache";

    private String expiresHeaders = "";

    private ConcurrentHashMap<String, ConcurrentHashMap> cachedJsTemplates = new ConcurrentHashMap<String, ConcurrentHashMap>();

    private TemplateFilesResolver templateFilesResolver = new EmptyTemplateFilesResolver();

    private TofuCompiler tofuCompiler = new EmptyTofuCompiler();

    private SoyMsgBundleResolver soyMsgBundleResolver = new EmptySoyMsgBundleResolver();

    private LocaleProvider localeProvider = new EmptyLocaleProvider();

    private boolean debugOn = false;

    private String encoding = SoyViewConfig.DEFAULT_ENCODING;

    private List<OutputProcessor> outputProcessors = new ArrayList<OutputProcessor>();

    public SoyAjaxController() {
    }

    @RequestMapping(value="/soy/{templateFileNames}", method=GET)
    public ResponseEntity<String> getJsForTemplateFiles(@PathVariable final String[] templateFileNames,
                                                        final HttpServletRequest request,
                                                        @RequestParam(required = false, value = "disableProcessors") String disableProcessors)
            throws IOException {

        return compileJs(templateFileNames, "", new Boolean(disableProcessors).booleanValue(), request);
    }

    @RequestMapping(value="/soy/{hash}/{templateFileNames}", method=GET)
    public ResponseEntity<String> getJsForTemplateFilesHash(@PathVariable final String hash,
                                                            @PathVariable final String[] templateFileNames,
                                                            @RequestParam(required = false, value = "disableProcessors") String disableProcessors,
                                                            final HttpServletRequest request) throws IOException {
        return compileJs(templateFileNames, hash, new Boolean(disableProcessors).booleanValue(), request);
    }

    private ResponseEntity<String> compileJs(final String[] templateFileNames,
                                             final String hash,
                                             final boolean disableProcessors,
                                             final HttpServletRequest request
    ) throws IOException {
        Preconditions.checkNotNull(templateFilesResolver, "templateFilesResolver cannot be null");

//        if (isProdMode()) {
//            final ConcurrentHashMap<URL, String> map = cachedJsTemplates.get(hash);
//            if (map != null) {
//                final String[] templates = map.get(templateFileNames);
//                if (templates != null) {
//                    return prepareResponseFor(template, disableProcessors);
//                }
//            }
//        }

        final String allCompiledTemplates = compileAndCombineAll(templateFileNames, request);

//        if (isProdMode()) {
//                ConcurrentHashMap<URL, String> map = cachedJsTemplates.get(hash);
//                if (map == null) {
//                    map = new ConcurrentHashMap<String[], String>();
//                } else {
//                    map.put(templateFileNames, allJsTemplates.toString());
//                }
//                cachedJsTemplates.putIfAbsent(hash, map);
//        }

        return prepareResponseFor(allCompiledTemplates, disableProcessors);
    }

    private String compileAndCombineAll(final String[] templateFileNames, final HttpServletRequest request) throws IOException {
        final StringBuilder allJsTemplates = new StringBuilder();

        for (final String templateFileName : stripExtensions(templateFileNames)) {
            final Optional<URL> templateUrl = templateFilesResolver.resolve(templateFileName);
            if (templateFileNames.length == 1 && !templateUrl.isPresent()) {
                throw notFound("File not found:" + templateFileName + ".soy");
            }

            logger.debug("Compiling JavaScript template:" + templateUrl.orNull());
            final String templateContent = compileTemplateAndAssertSuccess(request, templateUrl);
            allJsTemplates.append(templateContent);
        }

        return allJsTemplates.toString();
    }

    private ResponseEntity<String> prepareResponseFor(final String templateContent, final boolean disableProcessors) throws IOException {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/javascript; charset=" + encoding);
        headers.add("Cache-Control", debugOn ? "no-cache" : cacheControl);
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

    private String compileTemplateAndAssertSuccess(final HttpServletRequest request, final Optional<URL> templateFile) throws IOException {
        Preconditions.checkNotNull(localeProvider, "localeProvider cannot be null");
        Preconditions.checkNotNull(soyMsgBundleResolver, "soyMsgBundleResolver cannot be null");
        Preconditions.checkNotNull(tofuCompiler, "tofuCompiler cannot be null");

        final Optional<Locale> locale = localeProvider.resolveLocale(request);
        final Optional<SoyMsgBundle> soyMsgBundle = soyMsgBundleResolver.resolve(locale);
        final List<String> compiledTemplates = tofuCompiler.compileToJsSrc(templateFile.orNull(), soyMsgBundle.orNull());

        final Iterator<String> it = compiledTemplates.iterator();
        if (!it.hasNext()) {
            throw notFound("No compiled templates found!");
        }

        return it.next();
    }

    private boolean isProdMode() {
        return !debugOn;
    }

    /**/ String[] stripExtensions(final String[] exts) {
        if (exts == null) {
            return new String[0];
        }
        final String[] newStrippedExts = new String[exts.length];
        for (int i = 0; i<newStrippedExts.length; i++) {
            if (exts[i].contains(".soy")) {
                newStrippedExts[i] = exts[i].replace(".soy", "");
            } else {
                newStrippedExts[i] = exts[i];
            }
        }

        return newStrippedExts;
    }

    private HttpClientErrorException notFound(final String file) {
        return new HttpClientErrorException(NOT_FOUND, file);
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

    public void setDebugOn(final boolean debugOn) {
        this.debugOn = debugOn;
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

}
