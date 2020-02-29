plugins {
    `android-library`
    kotlin("android")
    kotlin("android.extensions")
}

dependencies {
    implementation(project(":shared:domain:valueobject"))
    implementation(project(":shared:repository"))
    implementation(project(":shared:api"))

    implementation(Libraries.Kotlin.android)
    implementation(Libraries.Kotlin.reflect)

    implementation(Libraries.Ktor.clientAndroid)
    implementation(Libraries.Ktor.jsonAndroid)
    implementation(Libraries.Ktor.serializationAndroid)

    implementation(Libraries.Kodein.android)
    implementation(Libraries.Kodein.frameworkAndroidX)
}
