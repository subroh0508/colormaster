plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

androidBaseExt()
android {
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}
