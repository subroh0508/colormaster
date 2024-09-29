plugins {
    id("android-application")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:features:preview"))
    implementation(project(":core:features:search"))
    implementation(project(":core:features:myidols"))

    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.androidx.core)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.android.material)
    implementation(compose.ui)
    implementation(compose.material)
    implementation(compose.uiTooling)
    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.koin.android)

    // Workaround
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.4")
}

//apply(plugin = "com.google.gms.google-services")

android { namespace = "net.subroh0508.colormaster.androidapp" }
