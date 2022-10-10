plugins {
    `node-convensions`
    `ci-report-all-tests`
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(kotlinGradlePlugin)
        classpath(androidGradlePlugin)
        classpath(googleServicesPlugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://s01.oss.sonatype.org/content/repositories/releases")
        maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
