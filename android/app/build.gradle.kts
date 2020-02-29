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
    implementation(project(":android:corecomponent"))
    implementation(project(":shared:domain:valueobject"))
    implementation(project(":shared:repository"))

    implementation(Libraries.Kotlin.android)
    implementation(Libraries.Kotlin.reflect)

    implementation(Libraries.Coroutines.android)

    implementation(Libraries.Ktor.clientAndroid)
    implementation(Libraries.Ktor.jsonAndroid)
    implementation(Libraries.Ktor.serializationAndroid)

    implementation(Libraries.Serialization.android)

    implementation(Libraries.Okhttp3.client)
    implementation(Libraries.Okhttp3.loggingIntercerptor)

    implementation(Libraries.AndroidX.appCompat)
    implementation(Libraries.AndroidX.coreKtx)
    implementation(Libraries.AndroidX.constraintLayout)
    implementation(Libraries.AndroidX.recyclerView)
    implementation(Libraries.AndroidX.lifecycleViewModel)
    implementation(Libraries.AndroidX.lifecycleLiveData)

    implementation(Libraries.Kodein.android)
    implementation(Libraries.Kodein.frameworkAndroidX)
}
