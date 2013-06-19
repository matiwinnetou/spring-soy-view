package spring.soy.templates;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.tofu.SoyTofu;
import org.springframework.web.servlet.view.AbstractTemplateView;
import spring.soy.templates.bundle.SoyMsgBundleResolver;
import spring.soy.templates.locale.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 19/06/2013
 * Time: 23:32
 */
public class SoyView extends AbstractTemplateView {

    private SoyTofu compiledTemplates;

    private String templateName;

    private SoyMsgBundleResolver soyMsgBundleResolver;

    private LocaleResolver localeResolver;

    @Override
    protected void renderMergedTemplateModel(final Map<String, Object> model,
                                             final HttpServletRequest request,
                                             final HttpServletResponse response) throws Exception {
        final Writer writer = response.getWriter();

        final Locale locale = localeResolver.resolveLocale(request);

        compiledTemplates.newRenderer(templateName)
                .setData(convertToMap(model))
                .setMsgBundle(soyMsgBundleResolver.resolve(locale))
                .render(writer);
    }

    private Map<String, ?> convertToMap(Object model) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        Method[] methods = model.getClass().getMethods();
        Map<String, Object> soyParameters = new HashMap<String, Object>();

        for (final Method method : methods) {
            String name = method.getName();
            if (!name.equals("getClass") && name.startsWith("get")) {
                Object parameter = method.invoke(model);
                String parameterName = name.substring(3, 4).toLowerCase() + name.substring(4);

                if (parameter instanceof List) {
                    List<Object> newParameter = new ArrayList<Object>();
                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    List<Object> asList = (List) parameter;
                    for (Object p : asList) {
                        try {
                            SoyData soyData = SoyData.createFromExistingData(p);
                            newParameter.add(soyData);
                        } catch (SoyDataException e) {
                            Map<String, ?> asMap = convertToMap(p);
                            newParameter.add(asMap);
                        }
                    }
                    soyParameters.put(parameterName, newParameter);
                } else {
                    try {
                        SoyData soyData = SoyData.createFromExistingData(parameter);
                        soyParameters.put(parameterName, soyData);
                    } catch (SoyDataException e) {
                        Map<String, ?> asMap = convertToMap(parameter);
                        soyParameters.put(parameterName, asMap);
                    }
                }
            }
        }

        return soyParameters;
    }

    public void setTemplateName(final String templateName) {
        this.templateName = templateName;
    }

    public void setCompiledTemplates(final SoyTofu compiledTemplates) {
        this.compiledTemplates = compiledTemplates;
    }

    public void setSoyMsgBundleResolver(final SoyMsgBundleResolver soyMsgBundleResolver) {
        this.soyMsgBundleResolver = soyMsgBundleResolver;
    }

    public void setLocaleResolver(final LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }

}
