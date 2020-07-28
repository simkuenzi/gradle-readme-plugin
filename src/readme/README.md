# README Plugin for Gradle
![Java CI with Gradle](https://github.com/simkuenzi/gradle-readme-plugin/workflows/Java%20CI%20with%20Gradle/badge.svg)

## Summary
Generates the `README.md` using a [Thymeleaf](https://www.thymeleaf.org/) template.

## Usage
Configure the plugin in your `build.gradle` file.
```groovy
plugins {
    id 'com.github.simkuenzi.readme' version '[(${version})]'
}

readme {
    overwrite = true // Overwrite existing README.md
}
```
You need a `gradle.properties` file in your project root directory.

```
version = 1.1-SNAPSHOT
releaseInfo = many new features
```

The template for your `README.md` goes to `src/readme/README.md`.
The template uses the syntax of 
[Thymeleaf's textual templates](https://www.thymeleaf.org/doc/tutorials/3.0/usingthymeleaf.html#textual-template-modes).

```markdown
# My Project
This version is [(${'[(${version})]'})] and contains [(${'[(${releaseInfo})]'})]
```

**Warning:** With this configuration, the `README.md` file in your project will be overwritten. 
Don't proceed if you have uncommitted changes in your `README.md`.

Start the `readme` task to produce the `README.md`.
```
$ ./gradlew readme

BUILD SUCCESSFUL in 4s
```

## Update README.md on release
The most common use case for the README Plugin is updating the version number on a new release. 
The snippet below shows, how to use the README Plugin together with the 
[Gradle Release Plugin](https://github.com/researchgate/gradle-release).

```groovy
plugins {
    id 'net.researchgate.release' version '2.8.1'
    id 'com.github.simkuenzi.readme' version '[(${version})]'
}

release {
    buildTasks = ['readme']
}

readme {
    overwrite = true
}
```

Your `README.md` will be updated on release and the 
[Gradle Release Plugin](https://github.com/researchgate/gradle-release) will commit the change.

## Configuration
The README plugin can be configured in your `build.gradle` file. All fields are optional.
The default values below will be used as a fallback.
```groovy
readme {
    // Location of the final README.md
    readmeFile = file('README.md') 
    
    // Location of the template
    templateFile = file('src/readme/README.md')

    // Properties used as data for the template 
    propertiesFile = file('gradle.properties') 

    // The readme task will fail if a README.md exists and this field is set to false.
    overwrite = false 
    
    // Encoding of your README.md and template.
    encoding = 'UTF-8'
}
```