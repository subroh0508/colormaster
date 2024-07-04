plugins {
    id("shared")
    alias(libs.plugins.jetbrains.compose)
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

                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.json)
                implementation(libs.ktor.serialization.core)

                implementation(libs.koin.core)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(project(":shared:data:api:jsfirebaseapp"))

                implementation(dependencies.platform(libs.kotlin.wrappers.bom))
                implementation(libs.kotlin.wrappers.extensions)

                implementation(npm("i18next", libs.versions.npm.i18next.core.get()))
                implementation(npm("i18next-http-backend", libs.versions.npm.i18next.http.backend.get()))
            }
        }
    }
}
