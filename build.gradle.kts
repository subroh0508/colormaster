plugins {
    alias(libs.plugins.kotlin.mpp) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.kotlin.js) apply false
    alias(libs.plugins.kotest) apply false
    id("colormaster.primitive.test.report")
}
