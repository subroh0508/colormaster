plugins {
    id("colormaster.convention.android")
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
}

//apply(plugin = "com.google.gms.google-services")

android { namespace = "net.subroh0508.colormaster.androidapp" }
