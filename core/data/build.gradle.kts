plugins {
    id("colormaster.convention.data")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core:model"))
                implementation(project(":core:network:imasparql"))
                implementation(project(":core:network:authentication"))
                implementation(project(":core:network:firestore"))

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

android { namespace = "net.subroh0508.colormaster.data" }
