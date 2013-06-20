package soy.template;

import com.google.common.collect.Lists;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mati
 * Date: 20/06/2013
 * Time: 19:58
 */
public class DefaultTemplateFilesResolver implements TemplateFilesResolver {

    private Resource templatesLocation;

    private boolean recursive = true;

    private String extension = "soy";

    public DefaultTemplateFilesResolver() {
    }

    public DefaultTemplateFilesResolver(final Resource templatesLocation, final boolean recursive) {
        this.templatesLocation = templatesLocation;
        this.recursive = recursive;
    }

    @Override
    public Collection<File> resolve() {
        if (templatesLocation == null) throw new IllegalArgumentException("templates location not defined");

        return toFiles(templatesLocation);
    }

    private List<File> toFiles(final Resource templatesLocation) {
        final List<File> templateFiles = Lists.newArrayList();
        try {
            File baseDirectory = templatesLocation.getFile();
            if (baseDirectory.isDirectory()) {
                templateFiles.addAll(findSoyFiles(baseDirectory, recursive));
            } else {
                throw new IllegalArgumentException("Soy template base directory '" + templatesLocation + "' is not a directory");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Soy template base directory '" + templatesLocation + "' does not exist", e);
        }

        return templateFiles;
    }

    protected List<File> findSoyFiles(final File baseDirectory, final boolean recursive) {
        final List<File> soyFiles = new ArrayList<File>();
        findSoyFiles(soyFiles, baseDirectory, recursive);

        return soyFiles;
    }

    protected void findSoyFiles(final List<File> soyFiles, final File baseDirectory, final boolean recursive) {
        final File[] files = baseDirectory.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isFile()) {
                    if (file.getName().endsWith("." + extension)) {
                        soyFiles.add(file);
                    }
                } else if (file.isDirectory() && recursive) {
                    findSoyFiles(soyFiles, file, recursive);
                }
            }
        } else {
            throw new IllegalArgumentException("Unable to retrieve contents of:" + baseDirectory);
        }
    }

    public void setTemplatesLocation(final Resource templatesLocation) {
        this.templatesLocation = templatesLocation;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
