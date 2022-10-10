plugins {
    shared
    id("org.jetbrains.compose")
    id("io.kotest.multiplatform")
}

kotlin {
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

                implementation(compose.ui)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)

                implementation(enforcedPlatform(kotlinWrappersBom))
                implementation(Libraries.JsWrappers.extensions)

                implementation(npm(Libraries.Npm.I18next.core, Libraries.Npm.I18next.version))
                implementation(npm(Libraries.Npm.I18next.httpBackend, Libraries.Npm.I18next.httpBackendVersion))
            }
        }
    }
}
