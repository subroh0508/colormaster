package net.subroh0508.colormaster.convention

import net.subroh0508.colormaster.library
import net.subroh0508.colormaster.libs
import net.subroh0508.colormaster.primitive.kmp.applyKmpPlugins
import net.subroh0508.colormaster.primitive.kmp.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class ApiModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with (target) {
            with (pluginManager) {
                applyKmpPlugins()
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            kotlin {
                with (sourceSets) {
                    commonMain {
                        dependencies {
                            implementation(libs.library("kotlinx-coroutines-core"))
                            implementation(libs.library("kotlinx-serialization"))
                            implementation(libs.library("koin-core"))
                        }
                    }
                }
            }
        }
    }
}