buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath(kotlinGradlePlugin)
        classpath(androidGradlePlugin)
        classpath(kotlinxSerializationGradlePlugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")
        maven("https://dl.bintray.com/ekito/koin")
    }
}

testDebugUnitTestReport()
jsNodeTestReport()
