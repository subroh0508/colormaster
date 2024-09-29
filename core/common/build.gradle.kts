plugins {
    id("shared")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)

                implementation(libs.koin.core)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(project(":core:data:api:jsfirebaseapp"))

                implementation(dependencies.platform(libs.kotlin.wrappers.bom))
                implementation(libs.kotlin.wrappers.js)

                implementation(npm("i18next", libs.versions.npm.i18next.core.get()))
                implementation(npm("i18next-http-backend", libs.versions.npm.i18next.http.backend.get()))
            }
        }
    }
}

android { namespace = "net.subroh0508.colormaster.common" }
