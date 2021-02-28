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
                implementation(npm(Libraries.Npm.Firebase.auth, Libraries.Npm.Firebase.authVersion))
            }
        }
    }
}

firebaseDependencies {
    implementation(Libraries.Firebase.auth)
}
