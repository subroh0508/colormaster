package net.subroh0508.colormaster.primitive.kmp

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class KmpJsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with (target) {
            kotlin {
                js { browser() }
            }
        }
    }
}