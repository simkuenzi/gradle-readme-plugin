package com.github.simkuenzi.readme;

import org.gradle.api.Project;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import java.io.File;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class ReadmeExtension {

    private final Property<File> readmeFile;
    private final Property<File> templateFile;
    private final Property<File> propertiesTemplateFile;
    private final Property<File> propertiesFile;
    private final Property<File> releasePropertiesFile;
    private final Property<Boolean> overwrite;
    private final Property<String> encoding;

    public ReadmeExtension(ObjectFactory objectFactory, Project project) {
        readmeFile = objectFactory.property(File.class);
        propertiesTemplateFile = objectFactory.property(File.class);
        propertiesTemplateFile.set(project.getRootDir().toPath().resolve(Path.of("src", "readme", "release.properties.txt")).toFile());
        readmeFile.set(project.getRootDir().toPath().resolve("README.md").toFile());
        templateFile = objectFactory.property(File.class);
        templateFile.set(project.getRootDir().toPath().resolve(Path.of("src", "readme", "README.md")).toFile());
        propertiesFile = objectFactory.property(File.class);
        propertiesFile.set(project.getRootDir().toPath().resolve("gradle.properties").toFile());
        releasePropertiesFile = objectFactory.property(File.class);
        releasePropertiesFile.set(project.getRootDir().toPath().resolve(Path.of("src", "readme", "release.properties")).toFile());
        encoding = objectFactory.property(String.class);
        encoding.set("UTF-8");
        overwrite = objectFactory.property(Boolean.class);
        overwrite.set(false);
    }

    public Property<File> getReadmeFile() {
        return readmeFile;
    }

    public void setReadmeFile(File readmeFile) {
        this.readmeFile.set(readmeFile);
    }

    public Property<File> getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(File templateFile) {
        this.templateFile.set(templateFile);
    }

    public Property<File> getPropertiesTemplateFile() {
        return propertiesTemplateFile;
    }

    public void setPropertiesTemplateFile(File propertiesTemplateFile) {
        this.propertiesTemplateFile.set(propertiesTemplateFile);
    }

    public Property<File> getPropertiesFile() {
        return propertiesFile;
    }

    public void setPropertiesFile(File propertiesFile) {
        this.propertiesFile.set(propertiesFile);
    }

    public Property<File> getReleasePropertiesFile() {
        return releasePropertiesFile;
    }

    public void setReleasePropertiesFile(File releasePropertiesFile) {
        this.releasePropertiesFile.set(releasePropertiesFile);
    }

    public Property<String> getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding.set(encoding);
    }

    public Property<Boolean> getOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite.set(overwrite);
    }
}
