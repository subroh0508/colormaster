plugins {
    kotlin("multiplatform")
    `android-multiplatform`
    id("kotlinx-serialization")
}

kotlinMpp {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libraries.Coroutines.core)

                implementation(Libraries.Serialization.core)

                implementation(Libraries.Koin.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libraries.Kotlin.reflect)

                implementation(Libraries.Jetpack.core)
            }
        }
        val jsMain by getting
    }
}
