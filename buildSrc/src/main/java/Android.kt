@file:Suppress("HardcodedStringLiteral")

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object Android {
    const val applicationId = "net.subroh0508.colormaster"
    const val versionCode = 1
    const val versionName = "0.0.1"

    object Versions {
        const val compileSdk = 28
        const val minSdk = 21
        const val targetSdk = 28
    }
}

val Project.androidGradlePlugin get() = "com.android.tools.build:gradle:${version("android-gradle-plugin")}"

internal fun Project.androidExt(configure: BaseExtension.() -> Unit) = (this as ExtensionAware).extensions.configure("android", configure)
internal fun Project.androidAppExt(configure: BaseAppModuleExtension.() -> Unit) = (this as ExtensionAware).extensions.configure("android", configure)
internal fun Project.androidLibExt(configure: LibraryExtension.() -> Unit) = (this as ExtensionAware).extensions.configure("android", configure)

internal fun Project.androidBaseExt() = androidExt {
    compileSdkVersion(Android.Versions.compileSdk)

    defaultConfig {
        minSdkVersion(Android.Versions.minSdk)
        targetSdkVersion(Android.Versions.targetSdk)

        buildConfigField("String", "VERSION_CODE", "\"${Android.versionCode}\"")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
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
    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
        }
    }

    sourceSets.forEach {
        it.java.setSrcDirs(files("src/${it.name}/kotlin"))
    }

    buildFeatures.compose = true

    composeOptions {
        kotlinCompilerExtensionVersion = Libraries.Jetpack.Compose.version
        kotlinCompilerVersion = kotlinVersion
    }

    configurations {
        // Workaround
        // @see https://youtrack.jetbrains.com/issue/KT-43944
        create("androidTestApi")
        create("androidTestDebugApi")
        create("androidTestReleaseApi")
        create("testApi")
        create("testDebugApi")
        create("testReleaseApi")
    }
}
