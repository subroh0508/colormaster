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
    implementation(libs.android.gradle.build.tools)
    implementation(libs.android.gradle.build.tools.builder)
    implementation(libs.android.gradle.build.tools.builder.model)
    implementation(libs.kotlin.gradle.plugin)
}
