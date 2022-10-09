plugins {
    kotlin("multiplatform")
    `android-multiplatform`
    kotlin("plugin.serialization")
    id("io.kotest.multiplatform")
}

kotlinMpp {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:data:model"))
                implementation(project(":shared:data:repository"))
                implementation(project(":shared:presentation:common"))

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
