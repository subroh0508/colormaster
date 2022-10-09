plugins {
    `shared-api`
    kotlin("plugin.serialization")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libraries.Ktor.client)
                implementation(Libraries.Ktor.json)
                implementation(Libraries.Ktor.serialization)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libraries.Ktor.clientOkHttp)
                implementation(Libraries.Okhttp3.client)
                implementation(Libraries.Okhttp3.loggingIntercerptor)
            }
        }
    }
}
