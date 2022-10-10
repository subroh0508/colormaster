import org.gradle.kotlin.dsl.dependencies

plugins {
    id("shared")
    kotlin("multiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:base"))
            }
        }
    }
}
