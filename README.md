# README Plugin for Gradle

## Usage
```groovy
plugins {
    id 'com.github.simkuenzi.readme' version '0.0'
}

readme {
    overwrite = true
}
```

```
$ ./gradlew readme

BUILD SUCCESSFUL in 4s
```

## Configuration
```groovy
readme {
    readmeFile = file('README.md')
    templateFile = file('src/readme/README.md')
    propertiesFile = file('gradle.properties')
    overwrite = false
    encoding = 'UTF-8'
}
```