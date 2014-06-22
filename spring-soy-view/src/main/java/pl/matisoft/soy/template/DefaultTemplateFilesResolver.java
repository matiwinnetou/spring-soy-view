package pl.matisoft.soy.template;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResource;
import pl.matisoft.soy.config.SoyViewConfigDefaults;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 19:58
 *
 * An implementation that will recursively search (and resolve)
 * for soy files based on provided templatesLocation path
 */
@ParametersAreNonnullByDefault
@ThreadSafe
public class DefaultTemplateFilesResolver implements TemplateFilesResolver, ServletContextAware, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(DefaultTemplateFilesResolver.class);

    /** spring resource that points to a root path, in which soy templates are located */
    private Resource templatesLocation = null;

    private boolean recursive = true;

    private boolean hotReloadMode = SoyViewConfigDefaults.DEFAULT_HOT_RELOAD_MODE;

    /** a thread safe cache for resolved templates, no need to worry of ddos attack */
    /** friendly */ CopyOnWriteArrayList<URL> cachedFiles = new CopyOnWriteArrayList<URL>();

    private String filesExtension = SoyViewConfigDefaults.DEFAULT_FILES_EXTENSION;

    @Inject
    private ServletContext servletContext;

    public DefaultTemplateFilesResolver() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (templatesLocation == null) {
            templatesLocation = new ServletContextResource(servletContext, SoyViewConfigDefaults.DEFAULT_TEMPLATE_FILES_PATH);
        }
    }

    @Override
    public Collection<URL> resolve() throws IOException {
        Preconditions.checkNotNull(templatesLocation, "templatesLocation cannot be null!");

        if (hotReloadMode) {
            final List<URL> files = toFiles(templatesLocation);
            logger.debug("Debug on - resolved files:" + files.size());

            return files;
        }

        //no debug
        synchronized (cachedFiles) {
            if (cachedFiles.isEmpty()) {
                final List<URL> files = toFiles(templatesLocation);
                logger.debug("templates location:" + templatesLocation);
                logger.debug("Using cache resolve, debug off, urls:" + files.size());
                cachedFiles.addAll(files);
            }
        }

        return cachedFiles;
    }

    @Override
    public Optional<URL> resolve(final @Nullable String templateFileName) throws IOException {
        if (templateFileName == null) {
            return Optional.absent();
        }

        final Collection<URL> files = resolve();

        final URL templateFile = Iterables.find(files, new Predicate<URL>() {

            @Override
            public boolean apply(final URL url) {
                final String fileName = url.getFile();
                final File file = new File(fileName);

                return file.toURI().toString().endsWith(normalizeTemplateName(templateFileName));
            }

        }, null);

        return Optional.fromNullable(templateFile);
    }

    private String normalizeTemplateName(final String templateFileName) {
        String normalizedTemplateName = templateFileName;
        if (!templateFileName.endsWith(dotWithExtension())) {
            normalizedTemplateName = templateFileName + dotWithExtension();
        }

        return normalizedTemplateName;
    }

    private List<URL> toFiles(final Resource templatesLocation) {
        final List<URL> templateFiles = Lists.newArrayList();
        try {
            File baseDirectory = templatesLocation.getFile();
            if (baseDirectory.isDirectory()) {
                templateFiles.addAll(findSoyFiles(baseDirectory, recursive));
            } else {
                throw new IllegalArgumentException("Soy template base directory:" + templatesLocation + "' is not a directory");
            }
        } catch (final IOException e) {
            throw new IllegalArgumentException("Soy template base directory '" + templatesLocation + "' does not exist", e);
        }

        return templateFiles;
    }

    protected List<URL> findSoyFiles(final File baseDirectory, final boolean recursive) throws MalformedURLException {
        final List<URL> soyFiles = new ArrayList<URL>();
        findSoyFiles(soyFiles, baseDirectory, recursive);

        return soyFiles;
    }

    protected void findSoyFiles(final List<URL> soyFiles, final File baseDirectory, final boolean recursive) throws MalformedURLException {
        final File[] files = baseDirectory.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith(dotWithExtension())) {
                        soyFiles.add(file.toURI().toURL());
                    }
                } else if (file.isDirectory() && recursive) {
                    findSoyFiles(soyFiles, file, recursive);
                }
            }
        } else {
            throw new IllegalArgumentException("Unable to retrieve contents of:" + baseDirectory);
        }
    }

    private String dotWithExtension() {
        return "." + filesExtension;
    }

    public void setTemplatesLocation(Resource templatesLocation) {
        this.templatesLocation = templatesLocation;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public void setHotReloadMode(boolean hotReloadMode) {
        this.hotReloadMode = hotReloadMode;
    }

    public Resource getTemplatesLocation() {
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

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
