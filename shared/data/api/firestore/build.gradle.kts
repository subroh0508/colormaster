plugins {
    `shared-api`
    kotlin("plugin.serialization")
}

kotlin {
    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.Experimental")
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                optIn("kotlinx.serialization.ExperimentalSerializationApi")
                optIn("kotlinx.serialization.InternalSerializationApi")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Libraries.Coroutines.playServices)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(project(":shared:data:api:jsfirebaseapp"))
            }
        }
    }
}

firebaseDependencies {
    implementation(Libraries.Firebase.firestore)
}
