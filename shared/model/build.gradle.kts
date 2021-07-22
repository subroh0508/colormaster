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
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(Libraries.JsWrappers.MaterialUi.core)
            }
        }
    }
}
