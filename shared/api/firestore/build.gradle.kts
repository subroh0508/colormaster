plugins {
    kotlin("multiplatform")
    `android-multiplatform`
    kotlin("plugin.serialization")
}

kotlinMpp {
    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.Experimental")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
                optIn("kotlinx.serialization.InternalSerializationApi")
            }
        }

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
