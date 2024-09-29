package net.subroh0508.colormaster.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class ModelModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with (target) {
            with (pluginManager) {
                apply("colormaster.primitive.kmp")
                apply("colormaster.primitive.kmp.android")
                apply("colormaster.primitive.kmp.js")
            }
        }
    }
}
