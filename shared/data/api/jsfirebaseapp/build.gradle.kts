plugins {
    kotlin("js")
}

kotlin {
    js(IR) { nodejs {} }
}

dependencies {
    implementation(libs.kotlinx.coroutines.js)
    api(npm("firebase", libs.versions.npm.firebase.get()))
}
