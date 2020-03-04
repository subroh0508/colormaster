buildscript {
    repositories {
        google()
        jcenter()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    }

    dependencies {
        classpath(Libraries.GradlePlugin.kotlin)
        classpath(Libraries.GradlePlugin.android)
        classpath(Libraries.GradlePlugin.kotlinSerialization)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
    }
}
