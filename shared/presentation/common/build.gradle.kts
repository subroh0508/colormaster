plugins {
    kotlin("multiplatform")
    `android-multiplatform`
    id("kotlinx-serialization")
}

kotlinMpp {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:base"))
                implementation(Libraries.Coroutines.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libraries.Jetpack.Lifecycle.viewModel)
            }
        }
        val jsMain by getting
    }
}
