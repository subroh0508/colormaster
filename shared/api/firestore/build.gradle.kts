plugins {
    kotlin("multiplatform")
    `android-multiplatform`
    kotlin("plugin.serialization")
}

kotlinMpp {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libraries.Coroutines.core)
                implementation(Libraries.Serialization.core)

                implementation(Libraries.Koin.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libraries.Coroutines.playServices)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(project(":shared:api:jsfirebaseapp"))
            }
        }
    }
}

firebaseDependencies {
    implementation(Libraries.Firebase.firestore)
}