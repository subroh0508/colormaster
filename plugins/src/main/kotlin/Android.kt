@file:Suppress("HardcodedStringLiteral")

import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

object Android {
    const val applicationId = "net.subroh0508.colormaster"
    const val versionCode = 1
    const val versionName = "0.0.1"

    object Versions {
        const val compileSdk = 34
        const val minSdk = 23
        const val targetSdk = 34
    }
}

fun Project.firebaseDependencies(configuration: DependencyHandlerScope.() -> Unit) = dependencies {
    implementation(dependencies.platform(libs.firebaseBom))
    configuration()
}

internal fun Project.androidExt(configure: BaseExtension.() -> Unit) = (this as ExtensionAware).extensions.configure("android", configure)

internal fun Project.androidBaseExt() = androidExt {
    compileSdkVersion(Android.Versions.compileSdk)

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
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

    sourceSets.forEach {
        it.java.setSrcDirs(files("src/${it.name}/kotlin"))
    }
}

private fun DependencyHandler.implementation(dependencyNotation: Any): Dependency? = add("implementation", dependencyNotation)
