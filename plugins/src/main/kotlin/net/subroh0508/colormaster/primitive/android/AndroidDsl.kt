package net.subroh0508.colormaster.primitive.android

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

fun Project.androidApplication(action: BaseAppModuleExtension.() -> Unit) = extensions.configure(action)

fun Project.androidLibrary(action: LibraryExtension.() -> Unit) = extensions.configure(action)

fun Project.android(action: TestedExtension.() -> Unit) = extensions.configure(action)

fun Project.setupAndroid(isMinifyEnabled: Boolean = false) {
    android {
        compileSdkVersion(Android.Versions.compileSdk)

        buildTypes {
            getByName("release") {
                this.isMinifyEnabled = isMinifyEnabled
                proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }

        tasks.withType<Test> {
            useJUnitPlatform()
            testLogging {
                events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
            }
        }
    }
}
