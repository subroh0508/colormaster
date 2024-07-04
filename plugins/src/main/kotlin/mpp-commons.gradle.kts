import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("multiplatform")
}

kotlin {
    android()
    js(IR) { nodejs {} }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(project(":shared:test"))

                implementation(kotestEngine)
                implementation(kotestAssertion)
            }
        }
        val androidMain by getting
        val androidTest by getting {
            dependencies {
                implementation(Libraries.Coroutines.test)
                implementation(kotestRunnerJunit5)
                implementation(Libraries.MockK.android)
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(project.dependencies.platform(kotlinWrappersBom))
                implementation(Libraries.JsWrappers.extensions)
            }
        }
    }
}
