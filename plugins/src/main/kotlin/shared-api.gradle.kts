plugins {
    id("shared")
    kotlin("multiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinxCoroutinesCore)
                implementation(libs.kotlinxSerialization)
                implementation(libs.koinCore)
            }
        }
    }
}
