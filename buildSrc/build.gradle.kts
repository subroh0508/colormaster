import java.util.*

plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

val props = Properties().apply {
    file("../gradle.properties").inputStream().use(this::load)
}

fun version(target: String) = props.getProperty("${target}.version")

dependencies {
    implementation("com.android.tools.build:gradle:${version("android-gradle-plugin")}")
    implementation("com.android.tools.build:builder:${version("android-gradle-plugin")}")
    implementation("com.android.tools.build:builder-model:${version("android-gradle-plugin")}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${version("kotlin")}")
}
