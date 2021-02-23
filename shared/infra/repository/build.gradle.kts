plugins {
    kotlin("multiplatform")
    `android-multiplatform`
}

kotlinMpp {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:api:imasparql"))
                implementation(project(":shared:infra:db"))
                implementation(project(":shared:infra:query"))
                implementation(project(":shared:model"))

                implementation(Libraries.Coroutines.core)
                implementation(Libraries.Serialization.core)

                implementation(Libraries.Koin.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Libraries.Ktor.client)
                implementation(Libraries.Ktor.clientMock)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libraries.Kotlin.reflect)
            }
        }
        val jsMain by getting
    }
}
