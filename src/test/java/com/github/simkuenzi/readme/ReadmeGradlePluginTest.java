package com.github.simkuenzi.readme;

import org.gradle.testkit.runner.GradleRunner;
import org.gradle.testkit.runner.TaskOutcome;
import org.gradle.testkit.runner.UnexpectedBuildFailure;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
                .withDebug(true)
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("readme", "--stacktrace")
                .withPluginClasspath()
                .build()
                .getTasks().forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));

        assertDir(
                checkFile("expected.md", "README.md"),
                checkFile("defaults.gradle.txt", "build.gradle"),
                checkFile("gradle.properties", "gradle.properties"),
                noCheck(".gradle"),
                checkDir("src",
                        checkDir("readme",
                                checkFile("template.md", "README.md")
                        )
                )
        );
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
                .withDebug(true)
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("readme", "--stacktrace")
                .withPluginClasspath()
                .build()
                .getTasks().forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));

        assertDir(
                checkFile("explicit.gradle.txt", "build.gradle"),
                checkDir("templates",
                        checkFile("template.md", "README-template.md")
                ),
                checkDir("props",
                        checkFile("gradle.properties", "model.properties")
                ),
                checkDir("finalFiles",
                        checkFile("expected.md", "myReadme.md")
                ),
                noCheck(".gradle")
        );
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
                .withDebug(true)
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("readme", "--stacktrace")
                .withPluginClasspath()
                .build()
                .getTasks().forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));

        assertDir(
                checkFile("expected.md", "README.md"),
                checkFile("overwrite.gradle.txt", "build.gradle"),
                checkFile("gradle.properties", "gradle.properties"),
                noCheck(".gradle"),
                checkDir("src",
                        checkDir("readme",
                                checkFile("template.md", "README.md")
                        )
                )
        );
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
                .withDebug(true)
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("readme", "--stacktrace")
                .withPluginClasspath()
                .build();
    }

    @Test(expected = UnexpectedBuildFailure.class)
    public void overwriteReleasePropertiesFail() throws Exception {
        setup(
                file("defaults.gradle.txt", "build.gradle"),
                file("gradle.properties", "gradle.properties"),
                file("gradle.properties", "src/readme/release.properties"),
                file("release.properties.txt", "src/readme/release.properties.txt")
        );

        GradleRunner.create()
                .forwardOutput()
                .withDebug(true)
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("updateReleaseProperties", "--stacktrace")
                .withPluginClasspath()
                .build();
    }

    @Test
    public void releaseDefaults() throws Exception {
        setup(
                file("overwrite.gradle.txt", "build.gradle"),
                file("gradle.properties", "gradle.properties"),
                file("releaseTemplate.md", "README.md"),
                file("releaseTemplate.md", "src/readme/README.md"),
                file("gradle.properties", "src/readme/release.properties"),
                file("release.properties.txt", "src/readme/release.properties.txt")
        );

        GradleRunner.create()
                .forwardOutput()
                .withDebug(true)
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("updateReleaseProperties", "updateReleaseReadme", "--stacktrace")
                .withPluginClasspath()
                .build()
                .getTasks().forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));

        assertDir(
                checkFile("overwrite.gradle.txt", "build.gradle"),
                checkFile("gradle.properties", "gradle.properties"),
                noCheck(".gradle"),
                checkDir("src",
                        checkDir("readme",
                                checkFile("release.properties.txt", "release.properties.txt"),
                                checkFile("release.properties", "release.properties"),
                                checkFile("releaseTemplate.md", "README.md")
                        )
                ),
                checkFile("expected.md", "README.md")
        );
    }

    @Test
    public void releaseExplicit() throws Exception {
        setup(
                file("explicit.gradle.txt", "build.gradle"),
                file("releaseTemplate.md", "templates/README-template.md"),
                file("release.properties.txt", "templates/release-template.properties"),
                file("gradle.properties", "props/model.properties")
        );

        GradleRunner.create()
                .forwardOutput()
                .withDebug(true)
                .withProjectDir(testProjectDir.getRoot())
                .withArguments("updateReleaseProperties", "updateReleaseReadme", "--stacktrace")
                .withPluginClasspath()
                .build()
                .getTasks().forEach(t -> assertEquals(TaskOutcome.SUCCESS, t.getOutcome()));

        assertDir(
                checkFile("explicit.gradle.txt", "build.gradle"),
                checkDir("templates",
                        checkFile("releaseTemplate.md", "README-template.md"),
                        checkFile("release.properties.txt", "release-template.properties")
                ),
                checkDir("props",
                        checkFile("gradle.properties", "model.properties"),
                        checkFile("release.properties", "release.properties")
                ),
                checkDir("finalFiles",
                        checkFile("expected.md", "myReadme.md")
                ),
                noCheck(".gradle")
        );
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

    private void assertDir(FileAssertion... fileAssertions) throws IOException {
        assertDir(testProjectDir.getRoot().toPath(), fileAssertions);
    }

    private void assertDir(Path dir, FileAssertion... fileAssertions) throws IOException {
        assertExists(dir);

        List<Path> consumedPaths = Arrays.stream(fileAssertions)
                .map(a -> a.assertThis(dir))
                .collect(Collectors.toList());

        List<String> unconsumedPaths = Files.list(dir)
                .filter(p -> consumedPaths.stream().noneMatch(op -> {
                    try {
                        return Files.isSameFile(p, op);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }))
                .map(Path::toString)
                .collect(Collectors.toList());

        assertEquals(
                String.format("Directory %s has files not covered by assertions: %s", dir, String.join(", ", unconsumedPaths)),
                0, unconsumedPaths.size());
    }

    private void assertFile(String expected, Path actual) throws IOException {
        assertExists(actual);
        InputStream in = getClass().getResourceAsStream(expected);
        if (in == null) {
            throw new IllegalArgumentException(String.format("Resource %s not found.", expected));
        }
        String expectedContent = new BufferedReader(new InputStreamReader(in))
                .lines().collect(Collectors.joining(System.lineSeparator()));
        assertEquals(
                String.format("%s does not match the expectation.", actual),
                expectedContent, Files.readString(actual).lines().collect(Collectors.joining(System.lineSeparator())));
    }

    private void assertExists(Path actual) {
        assertTrue(String.format("%s does not exist.", actual), Files.exists(actual));
    }

    private FileAssertion checkDir(String name, FileAssertion... fileAssertions) {
        return parent -> {
            Path path = parent.resolve(name);
            try {
                assertDir(path, fileAssertions);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return path;
        };
    }

    private FileAssertion checkFile(String expected, String name) {
        return parent -> {
            Path path = parent.resolve(name);
            try {
                assertFile(expected, path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return path;
        };
    }

    private FileAssertion noCheck(String name) {
        return parent -> parent.resolve(name);
    }

    private interface FileAssertion {
        Path assertThis(Path parent);
    }
}