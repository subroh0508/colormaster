plugins {
    kotlin("multiplatform")
    `android-multiplatform`
}

kotlinMpp {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libraries.Coroutines.core)

                implementation(Libraries.Koin.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libraries.Coroutines.playServices)
                implementation(Libraries.GoogleServices.auth)
            }
        }
        val jsMain by getting {
            dependencies {
                api(project(":shared:api:jsfirebaseapp"))
            }
        }
    }
}

firebaseDependencies {
    implementation(Libraries.Firebase.auth)
}
