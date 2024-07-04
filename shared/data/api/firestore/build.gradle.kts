plugins {
    id("shared-api")
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
                implementation(libs.kotlinx.coroutines.play.services)
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
    implementation(libs.firebase.firestore)
}
