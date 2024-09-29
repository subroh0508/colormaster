plugins {
    id("shared")
    kotlin("multiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core:common"))

                implementation(libs.kotlinxCoroutinesCore)
            }
        }
    }
}
