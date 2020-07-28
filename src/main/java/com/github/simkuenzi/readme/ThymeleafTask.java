package com.github.simkuenzi.readme;

import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Task;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ThymeleafTask implements Action<Task> {
    private final String description;
    private final TemplateEngine templateEngine;
    private final Supplier<File> template;
    private final Supplier<File> model;
    private final Supplier<File> output;
    private final Supplier<String> encoding;
    private final BooleanSupplier overwrite;

    public ThymeleafTask(String description, TemplateEngine templateEngine, Supplier<File> template, Supplier<File> model, Supplier<File> output, Supplier<String> encoding, BooleanSupplier overwrite) {
        this.description = description;
        this.templateEngine = templateEngine;
        this.template = template;
        this.model = model;
        this.output = output;
        this.encoding = encoding;
        this.overwrite = overwrite;
    }

    @Override
    public void execute(Task task) {
        task.setGroup("documentation");
        task.setDescription(description);
        task.doLast(exec -> {
            Path outputFile = output.get().toPath();
            if (Files.exists(outputFile) && !overwrite.getAsBoolean()) {
                throw new GradleException(String.format(
                        "%s already exists. Configure overwrite = true to overwrite an existing file.", outputFile));
            }

            try {
                Properties properties = new Properties();
                try (Reader propertiesIn = new FileReader(model.get(), Charset.forName(encoding.get()))) {
                    properties.load(propertiesIn);
                }

                Map<String, Object> vars = new HashMap<>();
                properties.forEach((key, value) -> vars.put(key.toString(), value));
                Context context = new Context(Locale.US, vars);

                Files.createDirectories(outputFile.getParent());
                try (Writer writer = Files.newBufferedWriter(outputFile, Charset.forName(encoding.get()))) {
                    templateEngine.process(template.get().toString(), context, writer);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
