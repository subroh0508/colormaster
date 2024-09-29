package net.subroh0508.colormaster.primitive.kmp

import net.subroh0508.colormaster.library
import net.subroh0508.colormaster.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class KmpPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with (target) {
            with (pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
            }

            kotlin {
                with (sourceSets) {
                    commonTest {
                        dependencies {
                            implementation(libs.library("kotlinx-coroutines-test"))
                            implementation(libs.library("kotest-framework-engine"))
                            implementation(libs.library("kotest-assertions-core"))
                        }
                    }
                }
            }
        }
    }
}
