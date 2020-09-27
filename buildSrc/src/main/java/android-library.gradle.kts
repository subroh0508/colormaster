import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
}

androidLibExt {
    compileSdkVersion(Android.Versions.compileSdk)

    defaultConfig {
        minSdkVersion(Android.Versions.minSdk)
        targetSdkVersion(Android.Versions.targetSdk)

        buildConfigField("String", "VERSION_CODE", "\"${Android.versionCode}\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }
    sourceSets.forEach {
        it.java.setSrcDirs(files("src/${it.name}/kotlin"))
    }

    dataBinding {
        isEnabled = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Libraries.Jetpack.Compose.version
        kotlinCompilerVersion = kotlinVersion
    }
}
