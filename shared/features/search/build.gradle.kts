plugins {
    id("shared-features")
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:data:model"))
                implementation(project(":shared:data:repository"))

                implementation(compose.runtime)

                implementation(libs.koin.core)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.ui)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(compose.web.core)
            }
        }
    }
}

android { namespace = "net.subroh0508.colormaster.features.search" }
