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
                implementation(project(":shared:infra:db"))
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
        val androidMain by getting
        val jsMain by getting
    }
}

firebaseDependencies {
    implementation(Libraries.Firebase.auth)
}
