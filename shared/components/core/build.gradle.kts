plugins {
    shared
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
            }
        }
    }
}
