package net.subroh0508.colormaster.primitive.spotless

import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class SpotlessPlugin : Plugin<Project> {
    companion object {
        private const val KTLINT_VERSION = "1.5.0"
        private const val SPOTLESS_PLUGIN_ID = "com.diffplug.spotless"
    }

    override fun apply(target: Project) {
        target.allprojects {
            pluginManager.apply(SPOTLESS_PLUGIN_ID)

            extensions.configure(SpotlessExtension::class.java) {
                kotlin {
                    target("**/*.kt")
                    targetExclude("**/build/**", "**/generated/**")
                    ktlint(KTLINT_VERSION)
                }
                kotlinGradle {
                    target("*.gradle.kts", "**/*.gradle.kts")
                    targetExclude("**/build/**")
                    ktlint(KTLINT_VERSION)
                }
            }
        }
    }
}
