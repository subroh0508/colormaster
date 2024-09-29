package net.subroh0508.colormaster.primitive.kmp

import org.gradle.api.Project
import org.gradle.api.plugins.PluginContainer
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun Project.kotlin(action: KotlinMultiplatformExtension.() -> Unit) = extensions.configure(action)

fun PluginContainer.applyKmpPlugins() {
    apply("colormaster.primitive.kmp")
    apply("colormaster.primitive.kmp.android")
    apply("colormaster.primitive.kmp.js")
}
