plugins {
    id("shared")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.koin.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.play.services)
                implementation(dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.auth)
                implementation(libs.google.services.auth)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(project(":shared:data:api:jsfirebaseapp"))
            }
        }
    }
}
