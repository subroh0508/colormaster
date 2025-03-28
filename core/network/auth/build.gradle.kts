import net.subroh0508.colormaster.primitive.android.Android

plugins {
    id("colormaster.primitive.kmp")
    id("colormaster.primitive.kmp.android")
    id("colormaster.primitive.kmp.js")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.firebase.auth)

                implementation(libs.koin.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.play.services)
            }
        }
    }
}

android {
    namespace = "net.subroh0508.colormaster.network.auth"

    buildFeatures.buildConfig = true
    defaultConfig {
        buildConfigField("String", "VERSION_CODE", "\"${Android.versionCode}\"")
    }
}
