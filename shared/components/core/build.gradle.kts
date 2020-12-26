plugins {
    kotlin("multiplatform")
    `android-library`
}

kotlin {
    android("android")
    js(LEGACY) { nodejs {} }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:model"))
                implementation(project(":shared:infra:repository"))
                implementation(project(":shared:infra:api"))
                implementation(project(":shared:infra:db"))

                implementation(Libraries.Kotlin.common)

                implementation(Libraries.Coroutines.common)

                implementation(Libraries.Ktor.clientCommon)
                implementation(Libraries.Ktor.jsonCommon)
                implementation(Libraries.Ktor.serializationCommon)

                implementation(Libraries.Koin.common)
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

                implementation(Libraries.Koin.android)
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

                implementation(Libraries.Koin.js)
            }
        }
    }
}
