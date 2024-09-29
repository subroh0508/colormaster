package net.subroh0508.colormaster.convention

import net.subroh0508.colormaster.koinCore
import net.subroh0508.colormaster.library
import net.subroh0508.colormaster.libs
import net.subroh0508.colormaster.primitive.compose.compose
import net.subroh0508.colormaster.primitive.kmp.applyKmpPlugins
import net.subroh0508.colormaster.primitive.kmp.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class CommonModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with (target) {
            with (pluginManager) {
                applyKmpPlugins()
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            kotlin {
                with (sourceSets) {
                    commonMain {
                        dependencies {
                            implementation(compose.dependencies.runtime)
                            implementation(compose.dependencies.ui)
                            implementation(libs.koinCore)
                        }
                    }
                    jsMain {
                        dependencies {
                            implementation(dependencies.platform(libs.library("kotlin-wrappers-bom")))
                            implementation(libs.library("kotlin-wrappers-js"))
                        }
                    }
                }
            }
        }
    }
}