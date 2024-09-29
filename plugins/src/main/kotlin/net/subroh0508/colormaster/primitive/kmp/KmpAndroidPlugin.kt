package net.subroh0508.colormaster.primitive.kmp

import libs
import net.subroh0508.colormaster.library
import net.subroh0508.colormaster.primitive.android.android
import net.subroh0508.colormaster.primitive.android.setupAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class KmpAndroidPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with (target) {
            with (pluginManager) {
                apply("com.android.library")
            }

            kotlin {
                androidTarget()

                with (sourceSets) {
                    getByName("androidUnitTest") {
                        dependencies {
                            implementation(libs.library("kotest-runner-junit5"))
                            implementation(libs.library("mockk-android"))
                        }
                    }
                }
            }

            setupAndroid()
        }
    }
}