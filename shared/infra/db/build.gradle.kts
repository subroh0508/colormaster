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

                implementation(Libraries.Serialization.core)
                implementation(Libraries.Serialization.protobuf)

                implementation(Libraries.Koin.common)
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(Libraries.Kotlin.android)
                implementation(Libraries.Kotlin.reflect)

                implementation(Libraries.Coroutines.android)

                implementation(Libraries.Jetpack.core)

                implementation(Libraries.Koin.android)
            }
        }
        val jsMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(Libraries.Kotlin.js)

                implementation(Libraries.Coroutines.js)

                implementation(Libraries.Koin.js)
            }
        }
    }
}
