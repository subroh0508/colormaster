buildscript {
    repositories {
        google()
        jcenter()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    }

    dependencies {
        classpath(kotlinGradlePlugin)
        classpath(androidGradlePlugin)
        classpath(Libraries.GradlePlugin.kotlinSerialization)
        classpath(Libraries.GradlePlugin.koin)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        maven(url = "http://dl.bintray.com/kotlin/kotlin-js-wrappers")
        maven(url = "https://dl.bintray.com/kodein-framework/kodein-dev")
        maven(url = "https://dl.bintray.com/subroh0508/maven")
        maven("https://dl.bintray.com/ekito/koin")
    }
}
