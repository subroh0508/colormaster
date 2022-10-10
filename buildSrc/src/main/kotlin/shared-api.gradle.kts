plugins {
    id("shared")
    kotlin("multiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libraries.Coroutines.core)
                implementation(Libraries.Serialization.core)
                implementation(Libraries.Koin.core)
            }
        }
    }
}
