plugins {
    kotlin("multiplatform")
    `android-library`
    id("kotlinx-serialization")
}

kotlin {
    android("android")
    js(LEGACY) { nodejs {} }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libraries.Coroutines.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libraries.Jetpack.Lifecycle.viewModel)
            }
        }
        val jsMain by getting
    }
}
