package com.github.simkuenzi.readme;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@SuppressWarnings("unused")
public class ReadmeGradlePlugin implements Plugin<Project> {
    @Override
    public void apply(@Nullable Project project) {
        ReadmeExtension config = new ReadmeExtension(Objects.requireNonNull(project).getObjects(), project);
        project.getExtensions().add("readme", config);

        project.getTasks().register("readme", task -> {
            task.setGroup("documentation");
            task.setDescription("Generates the README.md");

            FileTemplateResolver templateResolver = new FileTemplateResolver();
            templateResolver.setCacheable(false);
            templateResolver.setForceTemplateMode(true);
            templateResolver.setTemplateMode(TemplateMode.TEXT);
            TemplateEngine templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(templateResolver);

            task.doLast(exec -> {
                Path readmeFile = config.getReadmeFile().get().toPath();

                if (Files.exists(readmeFile) && !config.getOverwrite().get()) {
                    throw new GradleException(String.format(
                            "%s already exists. Configure overwrite = true to overwrite an existing file.", config.getReadmeFile().get()));
                }

                try {
                    Properties properties = new Properties();
                    try (Reader propertiesIn = new FileReader(config.getPropertiesFile().get(), Charset.forName(config.getEncoding().get()))) {
                        properties.load(propertiesIn);
                    }

                    Map<String, Object> vars = new HashMap<>();
                    properties.forEach((key, value) -> vars.put(key.toString(), value));
                    Context context = new Context(Locale.US, vars);

                    Files.createDirectories(readmeFile.getParent());
                    try (Writer writer = Files.newBufferedWriter(readmeFile, Charset.forName(config.getEncoding().get()))) {
                        templateEngine.process(config.getTemplateFile().get().toString(), context, writer);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }
}
