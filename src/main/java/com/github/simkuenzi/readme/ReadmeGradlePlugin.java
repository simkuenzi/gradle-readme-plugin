package com.github.simkuenzi.readme;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.annotation.Nullable;
import java.util.Objects;

@SuppressWarnings("unused")
public class ReadmeGradlePlugin implements Plugin<Project> {
    @Override
    public void apply(@Nullable Project project) {
        ReadmeExtension config = new ReadmeExtension(Objects.requireNonNull(project).getObjects(), project);
        project.getExtensions().add("readme", config);

        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setCacheable(false);
        templateResolver.setForceTemplateMode(true);
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        project.getTasks().register("readme", new ThymeleafTask(
                "Generates the README.md",
                templateEngine,
                () -> config.getTemplateFile().get(),
                () -> config.getPropertiesFile().get(),
                () -> config.getReadmeFile().get(),
                () -> config.getEncoding().get(),
                () -> config.getOverwrite().get()
        ));

        project.getTasks().register("updateReleaseReadme", new ThymeleafTask(
                "Generates the README.md",
                templateEngine,
                () -> config.getTemplateFile().get(),
                () -> config.getReleasePropertiesFile().get(),
                () -> config.getReadmeFile().get(),
                () -> config.getEncoding().get(),
                () -> config.getOverwrite().get()
        ));

        project.getTasks().register("updateReleaseProperties", new ThymeleafTask(
                "Generates the properties file.",
                templateEngine,
                () -> config.getPropertiesTemplateFile().get(),
                () -> config.getPropertiesFile().get(),
                () -> config.getReleasePropertiesFile().get(),
                () -> config.getEncoding().get(),
                () -> config.getOverwrite().get()
        ));
  }
}
