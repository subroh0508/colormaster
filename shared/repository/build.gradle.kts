plugins {
    kotlin("multiplatform")
    `android-library`
    id("kotlinx-serialization")
}

kotlin {
    android("android")
    js { nodejs {} }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libraries.Kotlin.common)

                implementation(Libraries.Ktor.clientCommon)
                implementation(Libraries.Ktor.jsonCommon)
                implementation(Libraries.Ktor.serializationCommon)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libraries.Kotlin.jvm)

                implementation(Libraries.Ktor.clientAndroid)
                implementation(Libraries.Ktor.jsonAndroid)
                implementation(Libraries.Ktor.serializationAndroid)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(Libraries.Kotlin.js)

                implementation(Libraries.Ktor.clientJs)
                implementation(Libraries.Ktor.jsonJs)
                implementation(Libraries.Ktor.serializationJs)
            }
        }
    }
}
