plugins {
    id("shared-repository")
    alias(libs.plugins.kotest)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core:data:api:imasparql"))
                implementation(project(":core:data:api:authentication"))
                implementation(project(":core:data:api:firestore"))
                implementation(project(":core:model"))

                implementation(libs.firebase.auth)
                implementation(libs.firebase.firestore)

                implementation(libs.ktor.client.core)
                implementation(libs.kotlinx.serialization)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.ktor.client.mock)
            }
        }
    }
}

android { namespace = "net.subroh0508.colormaster.repository" }
