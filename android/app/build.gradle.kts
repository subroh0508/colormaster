plugins {
    `android-application`
    kotlin("android")
}

android {
    defaultConfig {
        applicationId = Android.applicationId
        versionCode = Android.versionCode
        versionName = Android.versionName
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    packagingOptions {
        resources {
            excludes.add("META-INF/*")
        }
    }
}

dependencies {
    implementation(project(":shared:utilities"))
    implementation(project(":shared:model"))
    implementation(project(":shared:components:core"))
    implementation(project(":shared:infra:repository"))
    implementation(project(":shared:presentation:search"))
    implementation(project(":shared:presentation:preview"))

    implementation(Libraries.Kotlin.reflect)

    implementation(Libraries.Coroutines.android)

    implementation(Libraries.Jetpack.core)
    implementation(Libraries.Jetpack.activity)
    implementation(Libraries.Jetpack.activityCompose)
    implementation(Libraries.Jetpack.material)
    implementation(Libraries.Jetpack.Compose.ui)
    implementation(Libraries.Jetpack.Compose.material)
    implementation(Libraries.Jetpack.Compose.uiTooling)
    implementation(Libraries.Jetpack.Lifecycle.viewModel)

    implementation(Libraries.Koin.android)
    implementation(Libraries.Koin.androidXScope)
    implementation(Libraries.Koin.androidXViewModel)
}
