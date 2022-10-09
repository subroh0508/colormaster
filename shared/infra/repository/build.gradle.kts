plugins {
    `shared-infra`
    id("io.kotest.multiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:api:imasparql"))
                implementation(project(":shared:api:authentication"))
                implementation(project(":shared:api:firestore"))
                implementation(project(":shared:model"))

                implementation(Libraries.Serialization.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(Libraries.Ktor.client)
                implementation(Libraries.Ktor.clientMock)
            }
        }
    }
}

firebaseDependencies {
    implementation(Libraries.Firebase.auth)
}
