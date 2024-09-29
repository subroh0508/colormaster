plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.compose.multiplatform.gradle.plugin)
    implementation(libs.compose.compiler.gradle.plugin)
    implementation(libs.android.gradle.build.tools)
    implementation(libs.android.gradle.build.tools.builder)
    implementation(libs.android.gradle.build.tools.builder.model)
}

gradlePlugin {
    plugins {
        register("allTestReport") {
            id = "colormaster.primitive.test.report"
            implementationClass = "net.subroh0508.colormaster.primitive.AllTestReportPlugin"
        }
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

        register("androidApp") {
            id = "colormaster.convention.android"
            implementationClass = "net.subroh0508.colormaster.convention.AndroidAppModulePlugin"
        }
        register("api") {
            id = "colormaster.convention.api"
            implementationClass = "net.subroh0508.colormaster.convention.ApiModulePlugin"
        }
        register("common") {
            id = "colormaster.convention.common"
            implementationClass = "net.subroh0508.colormaster.convention.CommonModulePlugin"
        }
        register("data") {
            id = "colormaster.convention.data"
            implementationClass = "net.subroh0508.colormaster.convention.DataModulePlugin"
        }
        register("feature") {
            id = "colormaster.convention.feature"
            implementationClass = "net.subroh0508.colormaster.convention.FeatureModulePlugin"
        }
        register("model") {
            id = "colormaster.convention.model"
            implementationClass = "net.subroh0508.colormaster.convention.ModelModulePlugin"
        }
    }
}
