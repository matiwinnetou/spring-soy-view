package pl.matisoft.soy.ajax.url;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import pl.matisoft.soy.ajax.hash.EmptyHashFileGenerator;
import pl.matisoft.soy.ajax.hash.HashFileGenerator;
import pl.matisoft.soy.template.EmptyTemplateFilesResolver;
import pl.matisoft.soy.template.TemplateFilesResolver;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public Optional<String> compose(final HttpServletRequest request, final Collection<String> soyTemplateFileNames) throws IOException {
        final Optional<String> md5 = hashHelper(soyTemplateFileNames);
        if (!md5.isPresent()) {
            return Optional.absent();
        }

        final StringBuilder builder = new StringBuilder();
        builder.append(siteUrl);
        builder.append("/soy/compileJs?");
        builder.append("hash=");
        builder.append(md5.get());

        for (final String soyTemplateFileName : soyTemplateFileNames) {
            builder.append("&");
            builder.append("file=");
            builder.append(soyTemplateFileName);
        }

        return Optional.of(builder.toString());
    }

    @Override
    public Optional<String> compose(final HttpServletRequest request, final String soyTemplateFileName) throws IOException {
        return compose(request, Lists.newArrayList(soyTemplateFileName));
    }

    private Optional<String> hashHelper(final Collection<String> soyTemplateFileNames) throws IOException {
        final List<URL> urls = new ArrayList<URL>();
        for (final String file : soyTemplateFileNames) {
            final Optional<URL> url = templateFilesResolver.resolve(file);
            if (url.isPresent()) {
                urls.add(url.get());
            }
        }

        return hashFileGenerator.hashMulti(urls);
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
