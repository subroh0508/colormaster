plugins {
    kotlin("multiplatform")
    `android-library`
    id("kotlinx-serialization")
}

kotlin {
    android("android")
    js { nodejs {} }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libraries.Kotlin.common)
                implementation(Libraries.Serialization.common)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libraries.Kotlin.jvm)
                implementation(Libraries.Serialization.jvm)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(Libraries.Kotlin.js)
                implementation(Libraries.Serialization.js)
            }
        }
    }
}
