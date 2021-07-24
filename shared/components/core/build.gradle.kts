plugins {
    kotlin("multiplatform")
    `android-multiplatform`
}

kotlinMpp {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:api:imasparql"))
                implementation(project(":shared:api:authentication"))
                implementation(project(":shared:api:firestore"))
                implementation(project(":shared:infra:repository"))
                implementation(project(":shared:infra:db"))
                implementation(project(":shared:model"))

                implementation(Libraries.Coroutines.core)

                implementation(Libraries.Ktor.client)
                implementation(Libraries.Ktor.json)
                implementation(Libraries.Ktor.serialization)

                implementation(Libraries.Koin.core)
            }
        }
        val androidMain by getting
        val jsMain by getting {
            dependencies {
                implementation(project(":shared:api:jsfirebaseapp"))

                implementation(Libraries.JsWrappers(kotlinVersion).extensions)
            }
        }
    }
}
