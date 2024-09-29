import java.util.*

plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.android.gradle.build.tools)
    implementation(libs.android.gradle.build.tools.builder)
    implementation(libs.android.gradle.build.tools.builder.model)
    implementation(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "colormaster.primitive.android.libarary"
            implementationClass = "net.subroh0508.colormaster.primitive.android.AndroidLibraryPlugin"
        }
        register("kmp") {
            id = "colormaster.primitive.kmp"
            implementationClass = "net.subroh0508.colormaster.primitive.kmp.KmpPlugin"
        }
        register("kmpAndroid") {
            id = "colormaster.primitive.kmp.android"
            implementationClass = "net.subroh0508.colormaster.primitive.kmp.KmpAndroidPlugin"
        }
        register("kmpJs") {
            id = "colormaster.primitive.kmp.js"
            implementationClass = "net.subroh0508.colormaster.primitive.kmp.KmpJsPlugin"
        }

        register("model") {
            id = "colormaster.convention.model"
            implementationClass = "net.subroh0508.colormaster.convention.ModelModulePlugin"
        }
        register("api") {
            id = "colormaster.convention.api"
            implementationClass = "net.subroh0508.colormaster.convention.ApiModulePlugin"
        }
    }
}
