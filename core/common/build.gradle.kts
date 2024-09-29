plugins {
    id("colormaster.convention.common")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.firebase.app)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(npm("i18next", libs.versions.npm.i18next.core.get()))
                implementation(npm("i18next-http-backend", libs.versions.npm.i18next.http.backend.get()))
            }
        }
    }
}

android { namespace = "net.subroh0508.colormaster.common" }
