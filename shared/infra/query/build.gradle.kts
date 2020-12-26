plugins {
    kotlin("multiplatform")
    `android-library`
}

kotlin {
    android("android")
    js(LEGACY) { nodejs {} }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:model"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libraries.Kotlin.reflect)
            }
        }
        val jsMain by getting
    }
}
