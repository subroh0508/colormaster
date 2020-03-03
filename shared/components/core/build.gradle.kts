plugins {
    kotlin("multiplatform")
    `android-library`
}

kotlin {
    android("android")
    js { nodejs {} }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:model"))
                implementation(project(":shared:infra:repository"))
                implementation(project(":shared:infra:api"))

                implementation(Libraries.Kotlin.common)

                implementation(Libraries.Coroutines.common)

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

                implementation(Libraries.AndroidX.lifecycleLiveData)

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
