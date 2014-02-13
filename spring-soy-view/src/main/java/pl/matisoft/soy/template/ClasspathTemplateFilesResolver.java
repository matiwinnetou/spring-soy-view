package pl.matisoft.soy.template;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import pl.matisoft.soy.config.SoyViewConfigDefaults;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An implementation that will recursively search (and resolve)
 * for soy files based on provided templatesLocation path
 * @author: Brandon Zeeb
 */
@ParametersAreNonnullByDefault
@ThreadSafe
public class ClasspathTemplateFilesResolver implements TemplateFilesResolver, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(ClasspathTemplateFilesResolver.class);

    /** spring resource string that points to a root path, in which soy templates are located */
    private String templatesLocation = null;

    private boolean recursive = true;

    private boolean hotReloadMode = SoyViewConfigDefaults.DEFAULT_HOT_RELOAD_MODE;

    private PathMatchingResourcePatternResolver resolver;

    private ResourceLoader resourceLoader = new DefaultResourceLoader(getClass().getClassLoader());

    /** friendly */ Collection<URL> cachedFiles;

    private String filesExtension = SoyViewConfigDefaults.DEFAULT_FILES_EXTENSION;

    @Override
    public void afterPropertiesSet() {
        Preconditions.checkArgument(resourceLoader != null, "A ResourceLoader is expected to have been provided.");
        resolver = new PathMatchingResourcePatternResolver(resourceLoader);
    }

    @Override
    public Collection<URL> resolve() throws IOException {
        Preconditions.checkNotNull(templatesLocation, "templatesLocation cannot be null!");

        return Collections.unmodifiableCollection(handleResolution());
    }

    @Override
    public Optional<URL> resolve(final @Nullable String templateFileName) throws IOException {
        if (templateFileName == null) {
            return Optional.absent();
        }

        final Collection<URL> files = handleResolution();
        final String normalizedFileName = normalizeTemplateName(templateFileName);

        final URL templateFile = Iterables.find(files, new Predicate<URL>() {

            @Override
            public boolean apply(final URL url) {
                final String fileName = url.getFile();
                final File file = new File(fileName);

                return file.toURI().toString().endsWith(normalizedFileName);
            }

        }, null);

        return Optional.fromNullable(templateFile);
    }

    protected Collection<URL> handleResolution() throws IOException {
        Preconditions.checkNotNull(templatesLocation, "templatesLocation cannot be null!");

        if (hotReloadMode) {
            final Collection<URL> files = resolveSoyResources(templatesLocation);
            logger.debug("Debug on - resolved files: {}", files.size());

            return Collections.unmodifiableCollection(files);
        }

        // no debug
        // double check locking to ensure we don't block threads to test if cached files is null
        if (cachedFiles == null) {
            synchronized (this) {
                if (cachedFiles == null) {
                    final Collection<URL> files = resolveSoyResources(templatesLocation);
                    logger.debug("templates location: {}", templatesLocation);
                    logger.debug("Using cache resolve, debug off, urls: {}", files.size());

                    cachedFiles = Collections.unmodifiableCollection(files);
                }
            }
        }

        return cachedFiles;
    }

    private Collection<URL> resolveSoyResources(final String templatesLocation) throws IOException {
        List<URL> urls = new LinkedList<URL>();

        final String search = templateSearchStrings(templatesLocation);
        final Resource[] resources = resolver.getResources(search);

        for (Resource r : resources) {
            urls.add(r.getURL());
        }

        return urls;
    }

    private String normalizeTemplateName(final String templateFileName) {
        String normalizedTemplateName = templateFileName;
        if (!templateFileName.endsWith(dotWithExtension())) {
            normalizedTemplateName = templateFileName + dotWithExtension();
        }

        return normalizedTemplateName;
    }

    private String templateSearchStrings(String resource) {
        if (recursive) {
            return resource + "/**/*" + dotWithExtension();
        }

        return resource + "/*" + dotWithExtension();
    }

    private String dotWithExtension() {
        return "." + filesExtension;
    }

    public void setTemplatesLocation(String templatesLocation) {
        this.templatesLocation = templatesLocation;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public void setHotReloadMode(boolean hotReloadMode) {
        this.hotReloadMode = hotReloadMode;
    }

    public String getTemplatesLocation() {
        return templatesLocation;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public boolean isHotReloadMode() {
        return hotReloadMode;
    }

    public String getFilesExtension() {
        return filesExtension;
    }

    public void setFilesExtension(String filesExtension) {
        this.filesExtension = filesExtension;
    }

}
