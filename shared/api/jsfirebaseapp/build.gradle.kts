plugins {
    kotlin("js")
}

kotlin {
    js(LEGACY) { nodejs {} }
}

dependencies {
    implementation(Libraries.Coroutines.core)
    implementation(npm(Libraries.Npm.firebase, Libraries.Npm.firebaseVersion))
}
