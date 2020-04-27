
@Suppress("unused")
object Libraries {
    object GradlePlugin {
        const val buildToolsVersion = "3.6.1"

        const val android = "com.android.tools.build:gradle:$buildToolsVersion"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Kotlin.version}"
        const val kotlinSerialization = "org.jetbrains.kotlin:kotlin-serialization:${Kotlin.version}"
        const val navigationSafeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:${AndroidX.Navigation.version}"
    }

    object Kotlin {
        const val version = "1.3.72"

        const val common = "org.jetbrains.kotlin:kotlin-stdlib-common:$version"
        const val android = "org.jetbrains.kotlin:kotlin-stdlib:$version"
        const val js = "org.jetbrains.kotlin:kotlin-stdlib-js:$version"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
        const val androidExtensions = "org.jetbrains.kotlin:kotlin-android-extensions-runtime:$version"
        const val test = "org.jetbrains.kotlin:kotlin-test:$version"
    }

    object Serialization {
        const val version = "0.20.0"

        const val common = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:$version"
        const val js = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:$version"
        const val ios = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:$version"
    }

    object Coroutines {
        const val version = "1.3.5"

        const val common = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val js = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$version"
    }

    object Ktor {
        const val version = "1.3.2"

        const val clientCommon = "io.ktor:ktor-client-core:$version"
        const val clientAndroid = "io.ktor:ktor-client-okhttp:$version"
        const val clientIos = "io.ktor:ktor-client-ios:$version"
        const val clientJs = "io.ktor:ktor-client-js:$version"
        const val loggingAndroid = "io.ktor:ktor-client-logging-jvm:$version"
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

    object Kodein {
        const val version = "6.5.4"

        const val common = "org.kodein.di:kodein-di-erased:$version"
        const val android = "org.kodein.di:kodein-di-generic-jvm:$version"
        const val frameworkAndroidX = "org.kodein.di:kodein-di-framework-android-x:$version"
        const val js = "org.kodein.di:kodein-di-erased-js:$version"
    }

    object Okhttp3 {
        const val version = "3.14.2"

        const val client = "com.squareup.okhttp3:okhttp:$version"
        const val loggingIntercerptor = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    object AndroidX {
        const val coreVersion = "1.2.0"
        const val coreKtx = "androidx.core:core-ktx:$coreVersion"

        const val appCompatVersion = "1.1.0"
        const val appCompat = "androidx.appcompat:appcompat:$appCompatVersion"

        const val constraintLayoutVersion = "2.0.0-beta4"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:$constraintLayoutVersion"

        const val coordinatorLayoutVersion = "1.1.0"
        const val coordinatorLayout = "androidx.coordinatorlayout:coordinatorlayout:$coordinatorLayoutVersion"

        const val drawerLayoutVersion = "1.0.0"
        const val drawerLayout = "androidx.drawerlayout:drawerlayout:$drawerLayoutVersion"

        const val recyclerViewVersion = "1.1.0"
        const val recyclerView = "androidx.recyclerview:recyclerview:$recyclerViewVersion"

        const val lifecycleVersion = "2.2.0"
        const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion"
        const val lifecycleLiveData = "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion"

        const val materialVersion = "1.2.0-alpha05"
        const val material = "com.google.android.material:material:$materialVersion"

        object Navigation {
            const val version = "2.2.1"

            const val runtimeKtx = "androidx.navigation:navigation-runtime-ktx:$version"
            const val fragmentKtx = "androidx.navigation:navigation-fragment-ktx:$version"
            const val uiKtx = "androidx.navigation:navigation-ui-ktx:$version"
        }
    }

    object Html {
        const val version = "0.7.1"

        const val js = "org.jetbrains.kotlinx:kotlinx-html-js:$version"
    }

    object JsWrappers {
        const val version = "pre.100-kotlin-${Kotlin.version}"

        const val reactVersion = "${Npm.reactVersion}-$version"
        const val react = "org.jetbrains:kotlin-react:$reactVersion"
        const val reactDom = "org.jetbrains:kotlin-react-dom:$reactVersion"

        const val reactRouterDomVersion = "${Npm.reactRouterDomVersion}-$version"
        const val reactRouterDom = "org.jetbrains:kotlin-react-router-dom:$reactRouterDomVersion"

        const val cssVersion = "1.0.0-$version"
        const val css = "org.jetbrains:kotlin-css-js:$cssVersion"

        const val styledVersion = "1.0.0-$version"
        const val styled = "org.jetbrains:kotlin-styled:$styledVersion"

        const val extensionsVersion = "1.0.1-$version"
        const val extensions = "org.jetbrains:kotlin-extensions:$extensionsVersion"

        object MaterialUi {
            const val version = "0.3.13"
            const val core = "subroh0508.net.kotlinmaterialui:core:$version"
            const val lab = "subroh0508.net.kotlinmaterialui:lab:$version"
        }
    }

    object Npm {
        const val reactVersion = "16.13.1"
        const val react = "react"
        const val reactDom = "react-dom"

        const val reactRouterDomVersion = "5.1.2"
        const val reactRouterDom = "react-router-dom"

        const val styledComponentVersion = "^4.3.2"
        const val styledComponent = "styled-components"

        const val inlineStylePrefixerVersion = "^5.1.0"
        const val inlineStylePrefixer = "inline-style-prefixer"

        const val abortControllerVersion = "3.0.0"
        const val abortController = "abort-controller"

        const val textEncodingVersion = "0.7.0"
        const val textEncoding = "text-encoding"
    }
}
