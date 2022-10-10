import org.jetbrains.compose.compose

plugins {
    `android-application`
    kotlin("android")
    id("org.jetbrains.compose")
    id("com.google.gms.google-services") apply false
}

dependencies {
    implementation(project(":shared:data:model"))
    implementation(project(":shared:components:core"))
    implementation(project(":shared:data:repository"))
    implementation(project(":shared:presentation:common"))
    implementation(project(":shared:presentation:search"))
    implementation(project(":shared:presentation:preview"))

    implementation(Libraries.Coroutines.android)

    implementation(Libraries.Jetpack.core)
    implementation(Libraries.Jetpack.activity)
    implementation(Libraries.Jetpack.activityCompose)
    implementation(Libraries.Jetpack.material)
    implementation(compose.ui)
    implementation(compose.material)
    implementation(compose.uiTooling)
    implementation(compose(Libraries.Compose.util))
    implementation(Libraries.Jetpack.Lifecycle.viewModel)

    implementation(Libraries.Koin.android)

    // Workaround
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.4")
}

apply(plugin = "com.google.gms.google-services")
