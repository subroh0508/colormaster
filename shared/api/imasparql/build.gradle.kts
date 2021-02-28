plugins {
    kotlin("multiplatform")
    `android-multiplatform`
    id("kotlinx-serialization")
}

kotlinMpp {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libraries.Coroutines.core)

                implementation(Libraries.Serialization.core)

                implementation(Libraries.Ktor.client)
                implementation(Libraries.Ktor.json)
                implementation(Libraries.Ktor.serialization)

                implementation(Libraries.Koin.core)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libraries.Ktor.clientOkHttp)
                implementation(Libraries.Okhttp3.client)
                implementation(Libraries.Okhttp3.loggingIntercerptor)
            }
        }
        val jsMain by getting
    }
}
