plugins {
    kotlin("js")
}

kotlin {
    js(LEGACY) { nodejs {} }
}

dependencies {
    implementation(Libraries.Coroutines.core)
    api(npm(Libraries.Npm.firebase, Libraries.Npm.firebaseVersion))
}
