
@Suppress("unused")
object Libraries {
    object GradlePlugin {
        const val buildToolsVersion = "3.5.2"

        const val android = "com.android.tools.build:gradle:$buildToolsVersion"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"
        const val kotlinSerialization = "org.jetbrains.kotlin:kotlin-serialization:${Kotlin.version}"
    }

    object Kotlin {
        const val version = "1.3.61"

        const val common = "org.jetbrains.kotlin:kotlin-stdlib-common:$version"
        const val jvm = "org.jetbrains.kotlin:kotlin-stdlib:$version"
        const val js = "org.jetbrains.kotlin:kotlin-stdlib-js:$version"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
        const val androidExtensions = "org.jetbrains.kotlin:kotlin-android-extensions-runtime:$version"
        const val test = "org.jetbrains.kotlin:kotlin-test:$version"
    }

    object Serialization {
        const val version = "0.14.0"

        const val common = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$version"
        const val jvm = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:$version"
        const val js = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:$version"
        const val ios = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:$version"
    }

    object Coroutines {
        const val version = "1.3.3"

        const val common = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val js = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$version"
    }

    object Ktor {
        const val version = "1.3.1"

        const val clientCommon = "io.ktor:ktor-client-core:$version"
        const val clientAndroid = "io.ktor:ktor-client-okhttp:$version"
        const val clientIos = "io.ktor:ktor-client-ios:$version"
        const val clientJs = "io.ktor:ktor-client-js:$version"
        const val loggingJvm = "io.ktor:ktor-client-logging-jvm:$version"
        const val jsonCommon = "io.ktor:ktor-client-json:$version"
        const val jsonAndroid = "io.ktor:ktor-client-json-jvm:$version"
        const val jsonNative = "io.ktor:ktor-client-json-native:$version"
        const val jsonJs = "io.ktor:ktor-client-json-js:$version"
        const val serializationCommon = "io.ktor:ktor-client-serialization:$version"
        const val serializationAndroid = "io.ktor:ktor-client-serialization-jvm:$version"
        const val serializationIosArm64 = "io.ktor:ktor-client-serialization-iosarm64:$version"
        const val serializationIosX64 = "io.ktor:ktor-client-serialization-iosx64:$version"
        const val serializationJs = "io.ktor:ktor-client-serialization-js:$version"
    }
}
