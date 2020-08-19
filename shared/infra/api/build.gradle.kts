plugins {
    kotlin("multiplatform")
    `android-library`
    id("kotlinx-serialization")
}

kotlin {
    android("android")
    js(LEGACY) { nodejs {} }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:infra:query"))

                implementation(Libraries.Kotlin.common)

                implementation(Libraries.Coroutines.common)

                implementation(Libraries.Serialization.core)
                implementation(Libraries.Serialization.protobuf)

                implementation(Libraries.Ktor.clientCommon)
                implementation(Libraries.Ktor.jsonCommon)
                implementation(Libraries.Ktor.serializationCommon)

                implementation(Libraries.Kodein.common)
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(Libraries.Kotlin.android)
                implementation(Libraries.Kotlin.reflect)

                implementation(Libraries.Coroutines.android)

                implementation(Libraries.Ktor.clientAndroid)
                implementation(Libraries.Ktor.jsonAndroid)
                implementation(Libraries.Ktor.serializationAndroid)

                implementation(Libraries.Okhttp3.client)
                implementation(Libraries.Okhttp3.loggingIntercerptor)

                implementation(Libraries.Kodein.android)
            }
        }
        val jsMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(Libraries.Kotlin.js)

                implementation(Libraries.Coroutines.js)

                implementation(Libraries.Ktor.clientJs)
                implementation(Libraries.Ktor.jsonJs)
                implementation(Libraries.Ktor.serializationJs)

                implementation(Libraries.Kodein.js)
            }
        }
    }
}
