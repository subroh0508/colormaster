plugins {
    id("com.android.application")
}

androidBaseExt()
android {
    defaultConfig {
        minSdk = Android.Versions.minSdk
        targetSdk = Android.Versions.targetSdk

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
