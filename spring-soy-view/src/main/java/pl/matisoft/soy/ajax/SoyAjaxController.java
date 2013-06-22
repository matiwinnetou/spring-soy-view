package pl.matisoft.soy.ajax;

import com.google.common.base.Optional;
import com.google.template.soy.msgs.SoyMsgBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import pl.matisoft.soy.SoyUtils;
import pl.matisoft.soy.compile.TofuCompiler;
import pl.matisoft.soy.config.AbstractSoyConfigEnabled;
import pl.matisoft.soy.template.TemplateFilesResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class SoyAjaxController extends AbstractSoyConfigEnabled {

    private static final Logger logger = LoggerFactory.getLogger(SoyAjaxController.class);

    private String cacheControl = "public, max-age=3600";

	private ConcurrentHashMap<File, String> cachedJsTemplates = new ConcurrentHashMap<File, String>();

	public SoyAjaxController() {
	}

//    @RequestMapping(value="/soy/render/{templateName}.soy", method=GET)
//    public ResponseEntity<String> renderTemplate(@PathVariable final String templateName, final HttpServletRequest request) throws IOException {
//        SoyUtils.checkSoyViewConfig(config);
//
//        final HttpHeaders headers = new HttpHeaders();
//        headers.add("Content-Type", "text/javascript");
//        headers.add("Cache-Control", config.isDebugOn() ? "no-cache" : cacheControl);
//
//        final TemplateFilesResolver templateFilesResolver = config.getTemplateFilesResolver();
//        final Optional<File> templateFile = templateFilesResolver.resolve(templateName);
//
//        if (!templateFile.isPresent()) {
//            throw notFound(templateName);
//        }
//
//        final Optional<SoyMsgBundle> soyMsgBundle = SoyUtils.soyMsgBundle(config, request);
//        final TofuCompiler tofuCompiler = config.getTofuCompiler();
//        final Optional<SoyTofu> soyTofu = tofuCompiler.compile(Lists.newArrayList(templateFile.get()));
//        if (soyTofu.isPresent()) {
//            final TemplateRenderer templateRenderer = config.getTemplateRenderer();
//            templateRenderer.render(soyTofu.get(), te)
//        }
//
//        final SoyTofu.Renderer renderer = tofuData.newRenderer(templateName);
//        final SoyMapData soyMapData = config.getToSoyDataConverter().toSoyMap() //model resolver?
//
//        final List<String> compiledTemplates = tofuCompiler.compileToJsSrc(templateFile.orNull(), soyMsgBundle.orNull());
//
//        return new ResponseEntity<String>(templateContent, headers, OK);
//    }

    @RequestMapping(value="/soy/{templateFileName}.js", method=GET)
	public ResponseEntity<String> getJsForTemplateFile(@PathVariable String templateFileName, final HttpServletRequest request) throws IOException {
        SoyUtils.checkSoyViewConfig(config);
        final TemplateFilesResolver templateFilesResolver = config.getTemplateFilesResolver();
        final Optional<File> templateFile = templateFilesResolver.resolve(templateFileName);

		if (!config.isDebugOn() && cachedJsTemplates.containsKey(templateFile.get())) {
            logger.debug("Debug off and returning cached compiled file:" + templateFile.get());
			return prepareResponseFor(cachedJsTemplates.get(templateFile.get()));
		}

        logger.debug("Compiling JavaScript template:" + templateFile.orNull());

        if (!templateFile.isPresent()) {
            throw notFound("File not found:" + templateFileName + ".soy");
        }

        final String templateContent = compileTemplateAndAssertSuccess(request, templateFile);
        if (!config.isDebugOn()) {
            if (templateFile.isPresent()) {
                logger.debug("Debug off adding to templateFile to cache:" + templateFile.get());
                cachedJsTemplates.putIfAbsent(templateFile.get(), templateContent);
            }
        }

        return prepareResponseFor(templateContent);
    }

	private ResponseEntity<String> prepareResponseFor(final String templateContent) {
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/javascript");
		headers.add("Cache-Control", config.isDebugOn() ? "no-cache" : cacheControl);

		return new ResponseEntity<String>(templateContent, headers, OK);
	}

	private String compileTemplateAndAssertSuccess(final HttpServletRequest request, Optional<File> templateFile) throws IOException {
        final Optional<SoyMsgBundle> soyMsgBundle = SoyUtils.soyMsgBundle(config, request);
        final TofuCompiler tofuCompiler = config.getTofuCompiler();

		final List<String> compiledTemplates = tofuCompiler.compileToJsSrc(templateFile.orNull(), soyMsgBundle.orNull());

        final Iterator it = compiledTemplates.iterator();
		if (!it.hasNext()) {
			throw notFound("No compiled templates found!");
		}

		return (String) it.next();
	}

	private HttpClientErrorException notFound(String file) {
		return new HttpClientErrorException(NOT_FOUND, file);
	}

	public void setCacheControl(final String cacheControl) {
		this.cacheControl = cacheControl;
	}

}
