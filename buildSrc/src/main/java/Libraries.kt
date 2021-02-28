@Suppress("HardcodedStringLiteral", "unused")

object Libraries {
    object Serialization {
        const val version = "1.2.1"

        const val core = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"
    }

    object Coroutines {
        const val version = "1.5.0"

        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val js = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$version"
        const val playServices = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$version"

        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Ktor {
        const val version = "1.6.0"

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
        const val version = "3.0.2"

        const val core = "io.insert-koin:koin-core:$version"
        const val android = "io.insert-koin:koin-android:$version"
    }

    object Okhttp3 {
        const val version = "4.6.0"

        const val client = "com.squareup.okhttp3:okhttp:$version"
        const val loggingIntercerptor = "com.squareup.okhttp3:logging-interceptor:$version"
    }

    object Jetpack {
        private const val coreVersion = "1.3.2"
        const val core = "androidx.core:core-ktx:$coreVersion"

        private const val annotationVersion = "1.1.0"
        const val annotation = "androidx.annotation:annotation:$annotationVersion"

        private const val activityVersion = "1.3.0-rc02"
        const val activity = "androidx.activity:activity-ktx:$activityVersion"
        const val activityCompose = "androidx.activity:activity-compose:$activityVersion"

        private const val materialVersion = "1.3.0"
        const val material = "com.google.android.material:material:$materialVersion"

        object Compose {
            const val version = "1.0.0-rc02"

            const val ui = "androidx.compose.ui:ui:$version"
            const val material = "androidx.compose.material:material:$version"
            const val uiTooling = "androidx.compose.ui:ui-tooling:$version"
            const val util = "androidx.compose.ui:ui-util:$version"
        }

        object Lifecycle {
            private const val version = "2.3.0"

            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }
    }

    object Firebase {
        private const val bomVersion = "26.4.0"

        const val bom = "com.google.firebase:firebase-bom:$bomVersion"
        const val auth = "com.google.firebase:firebase-auth-ktx"
    }

    object GoogleServices {
        private const val authVersion = "19.0.0"

        const val auth = "com.google.android.gms:play-services-auth:$authVersion"
    }

    object Html {
        const val version = "0.7.3"

        const val js = "org.jetbrains.kotlinx:kotlinx-html-js:$version"
    }

    class Css(kotlinVersion: String) {
        val version = "1.0.0-${JsWrappers(kotlinVersion).version}"

        val js = "org.jetbrains.kotlin-wrappers:kotlin-css-js:$version"
    }

    class JsWrappers(kotlinVersion: String) {
        val version = "pre.209-kotlin-$kotlinVersion"

        private val reactVersion = "${Npm.reactVersion}-$version"
        val react = "org.jetbrains.kotlin-wrappers:kotlin-react:$reactVersion"
        val reactDom = "org.jetbrains.kotlin-wrappers:kotlin-react-dom:$reactVersion"

        private val reactRouterDomVersion = "${Npm.reactRouterDomVersion}-$version"
        val reactRouterDom = "org.jetbrains.kotlin-wrappers:kotlin-react-router-dom:$reactRouterDomVersion"

        private val styledVersion = "${Npm.styledComponentVersion}-$version"
        val styled = "org.jetbrains.kotlin-wrappers:kotlin-styled:$styledVersion"

        private val extensionsVersion = "1.0.1-$version"
        val extensions = "org.jetbrains.kotlin-wrappers:kotlin-extensions:$extensionsVersion"

        object MaterialUi {
            const val version = "0.5.8"
            const val core = "net.subroh0508.kotlinmaterialui:core:$version"
        }
    }

    object Npm {
        internal const val reactVersion = "17.0.2"
        internal const val reactRouterDomVersion = "5.2.0"
        internal const val styledComponentVersion = "5.3.0"

        const val reactAutoSuggestVersion = "10.0.2"
        const val reactAutoSuggest = "react-autosuggest"

        object I18next {
            const val version = "^19.4.5"
            const val core = "i18next"

            const val httpBackendVersion = "^1.0.15"
            const val httpBackend = "i18next-http-backend"

            const val reactVersion = "^11.8.0"
            const val react = "react-i18next"
        }

        object Firebase {
            const val authVersion = "0.16.4"
            const val auth = "@firebase/auth"
        }
    }

    object MockK {
        private const val version = "1.11.0"

        const val core = "io.mockk:mockk-common:$version"
        const val android = "io.mockk:mockk:$version"
    }

    object Kotest {
        private const val version = "4.6.0"

        const val engine = "io.kotest:kotest-framework-engine:$version"
        const val runnerJunit5 = "io.kotest:kotest-runner-junit5:$version"
        const val assertion = "io.kotest:kotest-assertions-core:$version"
    }
}

