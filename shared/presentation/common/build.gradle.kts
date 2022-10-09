plugins {
    kotlin("multiplatform")
    `android-multiplatform`
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("io.kotest.multiplatform")
}

kotlinMpp {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:base"))
                implementation(Libraries.Coroutines.core)

                implementation(compose.runtime)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libraries.Jetpack.Lifecycle.viewModel)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(enforcedPlatform(kotlinWrappersBom))
                implementation(Libraries.JsWrappers.extensions)

                implementation(npm(Libraries.Npm.I18next.core, Libraries.Npm.I18next.version))
                implementation(npm(Libraries.Npm.I18next.httpBackend, Libraries.Npm.I18next.httpBackendVersion))
            }
        }
    }
}
