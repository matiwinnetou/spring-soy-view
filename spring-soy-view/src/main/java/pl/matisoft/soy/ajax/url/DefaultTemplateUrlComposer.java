package pl.matisoft.soy.ajax.url;

import com.google.common.base.Optional;
import pl.matisoft.soy.ajax.hash.EmptyHashFileGenerator;
import pl.matisoft.soy.ajax.hash.HashFileGenerator;
import pl.matisoft.soy.template.EmptyTemplateFilesResolver;
import pl.matisoft.soy.template.TemplateFilesResolver;

import java.io.IOException;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: mszczap
 * Date: 03.07.13
 * Time: 20:17
 */
public class DefaultTemplateUrlComposer implements TemplateUrlComposer {

    private TemplateFilesResolver templateFilesResolver = new EmptyTemplateFilesResolver();

    private HashFileGenerator hashFileGenerator = new EmptyHashFileGenerator();

    private String siteUrl = "";

    public Optional<String> compose(final String soyTemplateFileName) throws IOException {
        final Optional<URL> url = templateFilesResolver.resolve(soyTemplateFileName);

        if (!url.isPresent()) {
            return Optional.absent();
        }

        final Optional<String> md5 = hashFileGenerator.hash(url);
        if (!md5.isPresent()) {
            return Optional.absent();
        }

        final String newUrl = new StringBuilder()
                .append(siteUrl)
                .append("/soy/")
                .append(md5.get())
                .append("/")
                .append(soyTemplateFileName)
                .toString();

        return Optional.of(newUrl);
    }

    public void setTemplateFilesResolver(final TemplateFilesResolver templateFilesResolver) {
        this.templateFilesResolver = templateFilesResolver;
    }

    public void setHashFileGenerator(final HashFileGenerator hashFileGenerator) {
        this.hashFileGenerator = hashFileGenerator;
    }

    public void setSiteUrl(final String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public String getSiteUrl() {
        return siteUrl;
    }

}
