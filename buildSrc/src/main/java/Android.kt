@file:Suppress("HardcodedStringLiteral")

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware

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

internal fun Project.androidAppExt(configure: BaseAppModuleExtension.() -> Unit) = (this as ExtensionAware).extensions.configure("android", configure)
internal fun Project.androidLibExt(configure: LibraryExtension.() -> Unit) = (this as ExtensionAware).extensions.configure("android", configure)
