@Suppress("HardcodedStringLiteral", "unused")

object Libraries {
    object GradlePlugin {
        const val kotlinSerialization = "org.jetbrains.kotlin:kotlin-serialization:${Kotlin.version}"
        const val koin = "org.koin:koin-gradle-plugin:${Koin.version}"
    }

    object Kotlin {
        const val version = "1.4.21"

        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
        const val test = "org.jetbrains.kotlin:kotlin-test:$version"
    }

    object Serialization {
        const val version = "1.0.1"

        const val core = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"
    }

    object Coroutines {
        const val version = "1.4.2"

        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val js = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$version"

        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Ktor {
        const val version = "1.4.3"

        const val client = "io.ktor:ktor-client-core:$version"
        const val clientOkHttp= "io.ktor:ktor-client-okhttp:$version"
        const val clientIos = "io.ktor:ktor-client-ios:$version"
        const val clientJs = "io.ktor:ktor-client-js:$version"
        const val json = "io.ktor:ktor-client-json:$version"
        const val jsonAndroid = "io.ktor:ktor-client-json-jvm:$version"
        const val jsonNative = "io.ktor:ktor-client-json-native:$version"
        const val jsonJs = "io.ktor:ktor-client-json-js:$version"
        const val serialization = "io.ktor:ktor-client-serialization:$version"
        const val serializationAndroid = "io.ktor:ktor-client-serialization-jvm:$version"
        const val serializationIosArm64 = "io.ktor:ktor-client-serialization-iosarm64:$version"
        const val serializationIosX64 = "io.ktor:ktor-client-serialization-iosx64:$version"
        const val serializationJs = "io.ktor:ktor-client-serialization-js:$version"

        const val clientMock = "io.ktor:ktor-client-mock:$version"
    }

    object Koin {
        const val version = "3.0.0-alpha-4"

        const val core = "org.koin:koin-core:$version"
        const val android = "org.koin:koin-android:$version"
        const val androidXScope = "org.koin:koin-androidx-scope:$version"
        const val androidXViewModel = "org.koin:koin-androidx-viewmodel:$version"
        const val js = "org.koin:koin-core-js:$version"
    }

    object Okhttp3 {
        const val version = "4.6.0"

        const val client = "com.squareup.okhttp3:okhttp:$version"
        const val loggingIntercerptor = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    object Jetpack {
        private const val coreVersion = "1.3.1"
        const val core = "androidx.core:core-ktx:$coreVersion"

        private const val appCompatVersion = "1.2.0"
        const val appCompat = "androidx.appcompat:appcompat:$appCompatVersion"

        private const val activityVersion = "1.2.0-alpha08"
        const val activity = "androidx.activity:activity-ktx:$activityVersion"

        private const val materialVersion = "1.2.0-alpha05"
        const val material = "com.google.android.material:material:$materialVersion"

        object Compose {
            const val version = "1.0.0-alpha09"

            const val ui = "androidx.compose.ui:ui:$version"
            const val material = "androidx.compose.material:material:$version"
            const val uiTooling = "androidx.compose.ui:ui-tooling:$version"
        }

        object Lifecycle {
            private const val version = "2.3.0-alpha07"

            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }
    }

    object Html {
        const val version = "0.7.2"

        const val js = "org.jetbrains.kotlinx:kotlinx-html-js:$version"
    }

    object Css {
        const val version = "1.0.0-${JsWrappers.version}"

        const val js = "org.jetbrains:kotlin-css-js:$version"
    }

    object JsWrappers {
        const val version = "pre.132-kotlin-${Kotlin.version}"

        private const val reactVersion = "${Npm.reactVersion}-$version"
        const val react = "org.jetbrains:kotlin-react:$reactVersion"
        const val reactDom = "org.jetbrains:kotlin-react-dom:$reactVersion"

        private const val reactRouterDomVersion = "${Npm.reactRouterDomVersion}-$version"
        const val reactRouterDom = "org.jetbrains:kotlin-react-router-dom:$reactRouterDomVersion"

        private const val styledVersion = "${Npm.styledComponentVersion}-$version"
        const val styled = "org.jetbrains:kotlin-styled:$styledVersion"

        private const val extensionsVersion = "1.0.1-$version"
        const val extensions = "org.jetbrains:kotlin-extensions:$extensionsVersion"

        object MaterialUi {
            const val version = "0.5.4"
            const val core = "net.subroh0508.kotlinmaterialui:core:$version"
        }
    }

    object Npm {
        const val reactVersion = "17.0.0"
        const val react = "react"
        const val reactDom = "react-dom"

        const val reactRouterDomVersion = "5.2.0"
        const val reactRouterDom = "react-router-dom"

        const val styledComponentVersion = "5.2.0"
        const val styledComponent = "styled-components"

        const val inlineStylePrefixerVersion = "^6.0.0"
        const val inlineStylePrefixer = "inline-style-prefixer"

        const val abortControllerVersion = "3.0.0"
        const val abortController = "abort-controller"

        const val textEncodingVersion = "0.7.0"
        const val textEncoding = "text-encoding"

        object I18next {
            const val version = "^19.4.5"
            const val core = "i18next"

            const val httpBackendVersion = "^1.0.15"
            const val httpBackend = "i18next-http-backend"

            const val reactVersion = "^11.8.0"
            const val react = "react-i18next"
        }
    }

    object MockK {
        private const val version = "1.10.3"

        const val core = "io.mockk:mockk-common:$version"
        const val android = "io.mockk:mockk:$version"
    }

    object Kotest {
        private const val version = "4.3.2"

        const val engine = "io.kotest:kotest-framework-engine:$version"
        const val runnerJunit5 = "io.kotest:kotest-runner-junit5:$version"
        const val assertion = "io.kotest:kotest-assertions-core:$version"
    }
}
