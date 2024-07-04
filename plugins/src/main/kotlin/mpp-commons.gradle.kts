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

                implementation(libs.kotestFrameworkEngine)
                implementation(libs.kotestAssertionsCore)
            }
        }
        val androidMain by getting
        val androidTest by getting {
            dependencies {
                implementation(libs.kotlinxCoroutinesTest)
                implementation(libs.kotestRunnerJunit5)
                implementation(libs.mockkAndroid)
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(dependencies.platform(libs.kotlinWrappersBom))
                implementation(libs.kotlinWrappersExtensions)
            }
        }
    }
}
