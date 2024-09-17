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

        val androidMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.play.services)
                implementation(dependencies.platform(libs.firebase.bom))
                implementation(libs.firebase.firestore)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(project(":shared:data:api:jsfirebaseapp"))
            }
        }
    }
}

android { namespace = "net.subroh0508.colormaster.api.firestore" }
