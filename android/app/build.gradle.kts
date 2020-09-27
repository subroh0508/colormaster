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

    implementation(Libraries.AndroidX.appCompat)
    implementation(Libraries.AndroidX.coreKtx)
    implementation(Libraries.AndroidX.constraintLayout)
    implementation(Libraries.AndroidX.drawerLayout)
    implementation(Libraries.AndroidX.recyclerView)
    implementation(Libraries.AndroidX.lifecycleViewModel)
    implementation(Libraries.AndroidX.lifecycleLiveData)

    implementation(Libraries.Koin.android)
}
