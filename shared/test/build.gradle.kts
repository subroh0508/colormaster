plugins {
    kotlin("multiplatform")
    `android-multiplatform`
}

kotlinMpp {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libraries.Ktor.client)
                implementation(Libraries.Ktor.clientMock)
                implementation(kotestEngine)
                implementation(kotestAssertion)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libraries.Jetpack.annotation)
                implementation(Libraries.Coroutines.test)
            }
        }
        val jsMain by getting
    }
}
