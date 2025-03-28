package net.subroh0508.colormaster.convention

import net.subroh0508.colormaster.primitive.android.Android
import net.subroh0508.colormaster.primitive.android.androidApplication
import net.subroh0508.colormaster.primitive.android.setupAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class AndroidAppModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with (target) {
            with (plugins) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("com.google.gms.google-services")
            }

            setupAndroid(isMinifyEnabled = true)
            androidApplication {
                defaultConfig {
                    minSdk = Android.Versions.minSdk
                    targetSdk = Android.Versions.targetSdk

                    applicationId = Android.applicationId
                    versionCode = Android.versionCode
                    versionName = Android.versionName
                    testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
                }

                packaging {
                    resources {
                        excludes.add("META-INF/*")
                    }
                }
            }
        }
    }
}
