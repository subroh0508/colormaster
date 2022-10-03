plugins {
    kotlin("multiplatform")
    `android-multiplatform`
    kotlin("plugin.serialization")
}

kotlinMpp {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(project(":shared:base"))
            }
        }
    }
}
