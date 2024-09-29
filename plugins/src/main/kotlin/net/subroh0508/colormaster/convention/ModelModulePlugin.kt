package net.subroh0508.colormaster.convention

import net.subroh0508.colormaster.primitive.kmp.applyKmpPlugins
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class ModelModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with (target) {
            with (plugins) {
                applyKmpPlugins()
            }
        }
    }
}
