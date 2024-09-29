plugins {
    id("shared")
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
    namespace = "net.subroh0508.colormaster.network.authentication"

    buildFeatures.buildConfig = true
    defaultConfig {
        buildConfigField("String", "VERSION_CODE", "\"${Android.versionCode}\"")
    }
}
