plugins {
    kotlin("multiplatform")
    //id("com.android.library")
    id("kotlinx-serialization")
}

kotlin {
    jvm()
    js { nodejs {} }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(Libraries.Kotlin.stdlibCommon)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(Libraries.Kotlin.stdlibJvm)
            }
        }
        val jsMain by getting {
            dependencies {
                implementation(Libraries.Kotlin.stdlibJs)
            }
        }
    }
}
