plugins {
    id("colormaster.primitive.kmp")
    id("colormaster.primitive.kmp.android")
    id("colormaster.primitive.kmp.js")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":core:model"))
                implementation(project(":core:network:auth"))
                implementation(project(":core:network:firestore"))
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.mock)
                implementation(libs.firebase.firestore)
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.assertions.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.annotation)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

android { namespace = "net.subroh0508.colormaster.test" }
