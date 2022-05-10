@Suppress("HardcodedStringLiteral", "unused")

object Libraries {
    object Serialization {
        const val version = "1.2.2"

        const val core = "org.jetbrains.kotlinx:kotlinx-serialization-json:$version"
    }

    object Coroutines {
        const val version = "1.5.2"

        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val js = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$version"
        const val playServices = "org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$version"

        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Ktor {
        const val version = "1.6.3"

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
        const val version = "3.1.2"

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

        private const val activityVersion = "1.4.0-alpha02"
        const val activity = "androidx.activity:activity-ktx:$activityVersion"
        const val activityCompose = "androidx.activity:activity-compose:$activityVersion"

        private const val materialVersion = "1.3.0"
        const val material = "com.google.android.material:material:$materialVersion"

        object Lifecycle {
            private const val version = "2.3.0"

            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }
    }

    object Compose {
        const val util = "org.jetbrains.compose.ui:ui-util"
    }

    object Firebase {
        private const val bomVersion = "28.4.1"

        const val bom = "com.google.firebase:firebase-bom:$bomVersion"
        const val auth = "com.google.firebase:firebase-auth-ktx"
        const val firestore = "com.google.firebase:firebase-firestore-ktx"
    }

    object GoogleServices {
        private const val authVersion = "19.2.0"

        const val auth = "com.google.android.gms:play-services-auth:$authVersion"
    }

    object JsWrappers {
        val react = "org.jetbrains.kotlin-wrappers:kotlin-react"
        val reactDom = "org.jetbrains.kotlin-wrappers:kotlin-react-dom"
        val reactRouterDom = "org.jetbrains.kotlin-wrappers:kotlin-react-router-dom"
        val styled = "org.jetbrains.kotlin-wrappers:kotlin-styled"
        val extensions = "org.jetbrains.kotlin-wrappers:kotlin-extensions"

        object MaterialUi {
            const val version = "0.7.0"
            const val core = "net.subroh0508.kotlinmaterialui:core:$version"
        }
    }

    object Npm {
        const val reactAutoSuggestVersion = "10.0.2"
        const val reactAutoSuggest = "react-autosuggest"

        const val firebaseVersion = "8.7.1"
        const val firebase = "firebase"

        const val materialComponentWeb = "^14.0.0"

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
        private const val version = "1.12.0"

        const val core = "io.mockk:mockk-common:$version"
        const val android = "io.mockk:mockk:$version"
    }

    object Kotest {
        private const val version = "4.6.3"

        const val engine = "io.kotest:kotest-framework-engine:$version"
        const val runnerJunit5 = "io.kotest:kotest-runner-junit5:$version"
        const val assertion = "io.kotest:kotest-assertions-core:$version"
    }
}

