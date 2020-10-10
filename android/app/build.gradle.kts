plugins {
    `android-application`
    kotlin("android")
    kotlin("android.extensions")
}

android {
    defaultConfig {
        applicationId = Android.applicationId
        versionCode = Android.versionCode
        versionName = Android.versionName
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packagingOptions {
        exclude("META-INF/*")
    }
}

dependencies {
    implementation(project(":android:widget"))
    implementation(project(":android:features:idol"))
    implementation(project(":shared:model"))
    implementation(project(":shared:components:core"))
    implementation(project(":shared:infra:repository"))

    implementation(Libraries.Kotlin.android)
    implementation(Libraries.Kotlin.reflect)

    implementation(Libraries.Coroutines.android)

    implementation(Libraries.Jetpack.appCompat)
    implementation(Libraries.Jetpack.coreKtx)
    implementation(Libraries.Jetpack.Compose.ui)
    implementation(Libraries.Jetpack.Compose.material)
    implementation(Libraries.Jetpack.Compose.uiTooling)
    implementation(Libraries.Jetpack.constraintLayout)
    implementation(Libraries.Jetpack.drawerLayout)
    implementation(Libraries.Jetpack.recyclerView)
    implementation(Libraries.Jetpack.lifecycleViewModel)

    implementation(Libraries.Koin.android)
    implementation(Libraries.Koin.androidXScope)
    implementation(Libraries.Koin.androidXViewModel)
}
