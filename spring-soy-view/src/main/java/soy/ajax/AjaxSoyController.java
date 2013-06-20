package soy.ajax;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.msgs.SoyMsgBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import soy.compile.TofuCompiler;
import soy.config.SoyViewConfig;
import soy.locale.LocaleResolver;
import soy.template.TemplateFilesResolver;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
public class AjaxSoyController {
	
	private String cacheControl = "public, max-age=3600";
	
	@Autowired
	private SoyViewConfig config;
	
	private ConcurrentHashMap<String, String> cachedJsTemplates = new ConcurrentHashMap<String, String>();

	@Autowired
	public AjaxSoyController(final SoyViewConfig config) {
		this.config = config;
	}

	@RequestMapping(value="/soy/{templateFileName}.js", method=GET)
	public ResponseEntity<String> getJsForTemplateFile(@PathVariable String templateFileName, final HttpServletRequest request) throws IOException {
		if (!config.isDebugOn() && cachedJsTemplates.containsKey(templateFileName)) {
			return prepareResponseFor(cachedJsTemplates.get(templateFileName));
		}

        File templateFile = getTemplateFileAndAssertExistence(templateFileName);
        String templateContent = compileTemplateAndAssertSuccess(request, templateFile);
        cachedJsTemplates.putIfAbsent(templateFileName, templateContent);
        ResponseEntity<String> response = prepareResponseFor(templateContent);

        return response;
    }

	private ResponseEntity<String> prepareResponseFor(final String templateContent) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "text/javascript");
		headers.add("Cache-Control", config.isDebugOn() ? "no-cache" : cacheControl);

		return new ResponseEntity<String>(templateContent, headers, OK);
	}

	private String compileTemplateAndAssertSuccess(final HttpServletRequest request, File templateFile) throws IOException {
        final Optional<SoyMsgBundle> soyMsgBundle = getSoyBundle(request);
        final SoyJsSrcOptions soyJavaSrcOptions = config.getJsSrcOptions();
        final TofuCompiler tofuCompiler = config.getTofuCompiler();

		final List<String> compiledTemplates = tofuCompiler.compileToJsSrc(templateFile, soyJavaSrcOptions, soyMsgBundle.orNull());

		if (compiledTemplates.size() < 1) {
			throw notFound("No compiled templates found");
		}

		return compiledTemplates.get(0);
	}

    private Optional<SoyMsgBundle> getSoyBundle(HttpServletRequest request) throws IOException {
        final LocaleResolver localeResolver = config.getLocaleResolver();
        final Locale locale = localeResolver.resolveLocale(request);

        if (locale != null) {
            return Optional.fromNullable(config.getSoyMsgBundleResolver().resolve(locale));
        }

        return Optional.absent();
    }

    private File getTemplateFileAndAssertExistence(final String templateFileName) {
        final TemplateFilesResolver templateFilesResolver = config.getTemplateFilesResolver();
        final Collection<File> files = templateFilesResolver.resolve();

        final File templateFile = Iterables.find(files, new Predicate<File>() {
            @Override
            public boolean apply(@Nullable File file) {
                return file.getName().equalsIgnoreCase(templateFileName + ".soy");
            }
        }, null);

        if (templateFile == null) {
            throw notFound(templateFileName);
        }

		return templateFile;
	}

	private HttpClientErrorException notFound(String file) {
		return new HttpClientErrorException(NOT_FOUND, file);
	}

    public void setConfig(final SoyViewConfig config) {
        this.config = config;
    }

	public void setCacheControl(String cacheControl) {
		this.cacheControl = cacheControl;
	}

}
