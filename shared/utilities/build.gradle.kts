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
                implementation(Libraries.Kotlin.common)
                implementation(Libraries.Coroutines.common)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libraries.Kotlin.android)
                implementation(Libraries.Coroutines.android)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(Libraries.Kotlin.js)
                implementation(Libraries.Coroutines.js)
            }
        }
    }
}
