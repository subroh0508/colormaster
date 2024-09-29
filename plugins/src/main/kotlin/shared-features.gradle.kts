plugins {
    id("shared")
    kotlin("multiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core:components:core"))

                implementation(libs.kotlinxCoroutinesCore)
            }
        }
    }
}
