plugins {
    `shared-repository`
    id("io.kotest.multiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:data:api:imasparql"))
                implementation(project(":shared:data:api:authentication"))
                implementation(project(":shared:data:api:firestore"))
                implementation(project(":shared:data:model"))

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
