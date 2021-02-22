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
                implementation(Libraries.Kotest.engine)
                implementation(Libraries.Kotest.assertion)
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
