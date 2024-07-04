plugins {
    id("shared")
    kotlin("multiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:components:core"))

                implementation(libs.kotlinxCoroutinesCore)
            }
        }
    }
}
