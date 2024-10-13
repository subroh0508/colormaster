package net.subroh0508.colormaster.convention

import net.subroh0508.colormaster.kotlinxCoroutinesCore
import net.subroh0508.colormaster.libs
import net.subroh0508.colormaster.primitive.kmp.applyKmpPlugins
import net.subroh0508.colormaster.primitive.kmp.kotlin
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class ModelModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with (target) {
            with (plugins) {
                applyKmpPlugins()
            }

            kotlin {
                with (sourceSets) {
                    commonMain {
                        dependencies {
                            implementation(libs.kotlinxCoroutinesCore)
                        }
                    }
                }
            }
        }
    }
}
