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
                implementation(project(":shared:infra:api"))
                implementation(project(":shared:infra:db"))
                implementation(project(":shared:infra:query"))

                implementation(Libraries.Coroutines.core)

                implementation(Libraries.Koin.core)
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
