package com.github.simkuenzi.readme;

import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.gradle.testkit.runner.UnexpectedBuildFailure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ReadmeGradlePluginTest {
    @Rule
    public TemporaryFolder testProjectDir = new TemporaryFolder();

    @Test
    public void defaults() throws Exception {
        setup(
                file("defaults.gradle.txt", "build.gradle"),
                file("template.md", "src/readme/README.md"),
                file("gradle.properties", "gradle.properties")
        );

        GradleRunner.create()
                .forwardOutput()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("readme", "--stacktrace")
                .withPluginClasspath()
                .build()
                .getTasks().forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));

        assertOutput(testProjectDir.getRoot().toPath().resolve("README.md"));
    }

    @Test
    public void explicit() throws Exception {
        setup(
                file("explicit.gradle.txt", "build.gradle"),
                file("template.md", "templates/README-template.md"),
                file("gradle.properties", "props/model.properties")
        );

        GradleRunner.create()
                .forwardOutput()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("readme", "--stacktrace")
                .withPluginClasspath()
                .build()
                .getTasks().forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));

        assertOutput(testProjectDir.getRoot().toPath().resolve("finalFiles/myReadme.md"));
    }

    @Test
    public void overwrite() throws Exception {
        setup(
                file("overwrite.gradle.txt", "build.gradle"),
                file("template.md", "README.md"),
                file("template.md", "src/readme/README.md"),
                file("gradle.properties", "gradle.properties")
        );

        GradleRunner.create()
                .forwardOutput()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("readme", "--stacktrace")
                .withPluginClasspath()
                .build()
                .getTasks().forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));

        assertOutput(testProjectDir.getRoot().toPath().resolve("README.md"));
    }

    @Test(expected = UnexpectedBuildFailure.class)
    public void overwriteFail() throws Exception {
        setup(
                file("defaults.gradle.txt", "build.gradle"),
                file("template.md", "README.md"),
                file("template.md", "src/readme/README.md"),
                file("gradle.properties", "gradle.properties")
        );

        GradleRunner.create()
                .forwardOutput()
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("readme", "--stacktrace")
                .withPluginClasspath()
                .build();

        assertOutput(testProjectDir.getRoot().toPath().resolve("README.md"));
    }

    private void assertOutput(Path actual) throws IOException {
        String expected = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("expected.md")))
                .lines().collect(Collectors.joining(System.lineSeparator()));
        assertEquals(expected, Files.readString(actual));
    }

    private void setup(FileSetup... setup) throws IOException {
        for (FileSetup fileSetup : setup) {
            fileSetup.copyOver();
        }
    }

    private FileSetup file(String origName, String dest) {
        return () -> {
            Path root = testProjectDir.getRoot().toPath();
            Path file = root.resolve(dest);
            Files.createDirectories(file.getParent());
            Files.createFile(file);
            Files.write(file, getClass().getResourceAsStream(origName).readAllBytes());
        };
    }

    private interface FileSetup {
        void copyOver() throws IOException;
    }
}