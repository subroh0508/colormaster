import net.subroh0508.colormaster.primitive.android.Android

plugins {
    id("colormaster.convention.api")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.json)
                implementation(libs.ktor.serialization.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.okhttp3.client)
                implementation(libs.okhttp3.logging.interceptor)
            }
        }
    }
}

android {
    namespace = "net.subroh0508.colormaster.network.imasparql"

    buildFeatures.buildConfig = true
    defaultConfig {
        buildConfigField("String", "VERSION_CODE", "\"${Android.versionCode}\"")
    }
}
