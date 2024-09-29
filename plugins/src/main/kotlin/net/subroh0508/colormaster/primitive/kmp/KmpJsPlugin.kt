package net.subroh0508.colormaster.primitive.kmp

import net.subroh0508.colormaster.library
import net.subroh0508.colormaster.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class KmpJsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with (target) {
            kotlin {
                js { browser() }

                with (sourceSets) {
                    jsTest {
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