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
                implementation(project(":shared:data"))

                implementation(Libraries.Kotlin.common)

                implementation(Libraries.Serialization.common)

                implementation(Libraries.Ktor.clientCommon)
                implementation(Libraries.Ktor.jsonCommon)
                implementation(Libraries.Ktor.serializationCommon)
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(Libraries.Kotlin.android)
                implementation(Libraries.Kotlin.reflect)

                implementation(Libraries.Serialization.android)

                implementation(Libraries.Ktor.clientAndroid)
                implementation(Libraries.Ktor.jsonAndroid)
                implementation(Libraries.Ktor.serializationAndroid)
            }
        }
        val jsMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(Libraries.Kotlin.js)

                implementation(Libraries.Serialization.js)

                implementation(Libraries.Ktor.clientJs)
                implementation(Libraries.Ktor.jsonJs)
                implementation(Libraries.Ktor.serializationJs)
            }
        }
    }
}
