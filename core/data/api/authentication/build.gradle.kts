plugins {
    id("shared")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.koin.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.play.services)
                implementation(dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.auth)
                implementation(libs.google.services.auth)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(project(":core:data:api:jsfirebaseapp"))
            }
        }
    }
}

android {
    namespace = "net.subroh0508.colormaster.api.authentication"

    buildFeatures.buildConfig = true
    defaultConfig {
        buildConfigField("String", "VERSION_CODE", "\"${Android.versionCode}\"")
    }
}
