plugins {
    id("shared-repository")
    alias(libs.plugins.kotest)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:data:api:imasparql"))
                implementation(project(":shared:data:api:authentication"))
                implementation(project(":shared:data:api:firestore"))
                implementation(project(":shared:data:model"))

                implementation(libs.kotlinx.serialization)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.mock)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.auth)
            }
        }
    }
}
