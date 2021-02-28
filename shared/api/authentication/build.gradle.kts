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
                implementation(project(":shared:api:jsfirebaseapp"))

                implementation(npm(Libraries.Npm.firebase, Libraries.Npm.firebaseVersion))
            }
        }
    }
}

firebaseDependencies {
    implementation(Libraries.Firebase.auth)
}
