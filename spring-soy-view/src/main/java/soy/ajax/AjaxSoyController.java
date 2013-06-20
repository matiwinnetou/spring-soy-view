package soy.ajax;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.msgs.SoyMsgBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import soy.config.SoyViewConfig;

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
	
	//TODO: Make properties on this settable via config
	private SoyJsSrcOptions jsSrcOptions = new SoyJsSrcOptions();
	
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
		final SoyFileSet soyFileSet = buildSoyFileSetFrom(templateFile);

        final Locale locale = config.getLocaleResolver().resolveLocale(request);
        SoyMsgBundle soyMsgBundle = null;
        if (locale != null) {
            soyMsgBundle = config.getSoyMsgBundleResolver().resolve(locale);
        }

		final List<String> compiledTemplates = soyFileSet.compileToJsSrc(jsSrcOptions, soyMsgBundle);
		if (compiledTemplates.size() < 1) {
			throw notFound("No compiled templates found");
		}

		return compiledTemplates.get(0);
	}

	private SoyFileSet buildSoyFileSetFrom(final File templateFile) {
        final SoyFileSet.Builder builder = new SoyFileSet.Builder();
        builder.add(templateFile);

        return builder.build();
	}

	private File getTemplateFileAndAssertExistence(final String templateFileName) {
        final Collection<File> files = config.getTemplateFilesResolver().resolve();

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
