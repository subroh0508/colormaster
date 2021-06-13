import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun Project.kotlinMpp(configure: KotlinMultiplatformExtension.() -> Unit) =
    (this as ExtensionAware).extensions.configure<KotlinMultiplatformExtension>("kotlin") {
        android()
        js(LEGACY) { nodejs {} }

        sourceSets {
            val commonMain by getting
            val commonTest by getting {
                dependencies {
                    implementation(project(":shared:test"))

                    implementation(Libraries.MockK.core)
                    implementation(Libraries.Kotest.engine)
                    implementation(Libraries.Kotest.assertion)
                }
            }
            val androidMain by getting
            val androidTest by getting {
                dependencies {
                    implementation(Libraries.Coroutines.test)
                    implementation(Libraries.MockK.android)
                    implementation(Libraries.Kotest.runnerJunit5)
                }
            }
            val jsMain by getting
            val jsTest by getting
        }

        configure()
    }
