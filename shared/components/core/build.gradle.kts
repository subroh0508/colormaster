plugins {
    shared
    id("org.jetbrains.compose")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:data:api:imasparql"))
                implementation(project(":shared:data:api:authentication"))
                implementation(project(":shared:data:api:firestore"))
                implementation(project(":shared:data:repository"))
                implementation(project(":shared:data:model"))

                implementation(compose.runtime)
                implementation(compose.ui)

                implementation(Libraries.Coroutines.core)

                implementation(Libraries.Ktor.client)
                implementation(Libraries.Ktor.json)
                implementation(Libraries.Ktor.serialization)

                implementation(Libraries.Koin.core)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(project(":shared:data:api:jsfirebaseapp"))

                implementation(enforcedPlatform(kotlinWrappersBom))
                implementation(Libraries.JsWrappers.extensions)

                implementation(npm(Libraries.Npm.I18next.core, Libraries.Npm.I18next.version))
                implementation(npm(Libraries.Npm.I18next.httpBackend, Libraries.Npm.I18next.httpBackendVersion))
            }
        }
    }
}
