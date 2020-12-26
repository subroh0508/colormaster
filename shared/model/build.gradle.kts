plugins {
    kotlin("multiplatform")
    `android-library`
    id("kotlinx-serialization")
}

kotlinMpp {
    sourceSets {
        val commonMain by getting
        val androidMain by getting
        val jsMain by getting

        all {
            languageSettings.enableLanguageFeature("InlineClasses")
        }
    }
}
