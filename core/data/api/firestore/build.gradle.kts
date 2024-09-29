plugins {
    id("shared-api")
    alias(libs.plugins.kotlinx.serialization)
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

        val commonMain by getting {
            dependencies {
                implementation(libs.firebase.firestore)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.play.services)
            }
        }
    }
}

android { namespace = "net.subroh0508.colormaster.api.firestore" }
