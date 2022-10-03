plugins {
    kotlin("multiplatform")
    `android-multiplatform`
    kotlin("plugin.serialization")
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
                implementation(Libraries.Jetpack.core)
            }
        }
        val jsMain by getting
    }
}
