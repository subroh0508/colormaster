plugins {
    kotlin("multiplatform")
}

kotlin {
    android()
    js(LEGACY) { nodejs {} }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(project(":shared:test"))

                implementation(Libraries.MockK.core)
                implementation(Libraries.Kotest.engine)
                implementation(Libraries.Kotest.assertion)
            }
        }
        val androidMain by getting
        val androidTest by getting {
            dependencies {
                implementation(Libraries.Coroutines.test)
                implementation(Libraries.MockK.android)
                implementation(Libraries.Kotest.runnerJunit5)
            }
        }
        val jsMain by getting
        val jsTest by getting
    }
}
