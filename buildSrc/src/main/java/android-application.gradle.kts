import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
}

androidAppExt {
    compileSdkVersion(Android.Versions.compileSdk)

    defaultConfig {
        minSdkVersion(Android.Versions.minSdk)
        targetSdkVersion(Android.Versions.targetSdk)
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
        kotlinOptions.useIR = true
    }
    sourceSets.forEach {
        it.java.setSrcDirs(files("src/${it.name}/kotlin"))
    }

    buildFeatures {
        dataBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Libraries.Jetpack.Compose.version
        kotlinCompilerVersion = kotlinVersion
    }
}
