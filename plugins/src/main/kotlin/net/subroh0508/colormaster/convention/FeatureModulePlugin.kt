package net.subroh0508.colormaster.convention

import net.subroh0508.colormaster.koinCore
import net.subroh0508.colormaster.kotlinxCoroutinesCore
import net.subroh0508.colormaster.library
import net.subroh0508.colormaster.libs
import net.subroh0508.colormaster.primitive.compose.compose
import net.subroh0508.colormaster.primitive.kmp.applyKmpPlugins
import net.subroh0508.colormaster.primitive.kmp.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.project

@Suppress("unused")
class FeatureModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with (target) {
            with (plugins) {
                applyKmpPlugins()
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            kotlin {
                with (sourceSets) {
                    commonMain {
                        dependencies {
                            implementation(project(":core:common"))
                            implementation(project(":core:model"))
                            implementation(project(":core:data"))
                            implementation(compose.dependencies.runtime)
                            implementation(libs.kotlinxCoroutinesCore)
                            implementation(libs.koinCore)
                        }
                    }

                    androidMain {
                        dependencies {
                            implementation(compose.dependencies.ui)
                        }
                    }

                    jsMain {
                        dependencies {
                            implementation(compose.dependencies.html.core)
                        }
                    }
                }
            }
        }
    }
}
