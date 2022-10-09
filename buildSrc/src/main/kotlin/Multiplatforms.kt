import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun Project.kotlinMpp(configure: KotlinMultiplatformExtension.() -> Unit) =
    (this as ExtensionAware).extensions.configure<KotlinMultiplatformExtension>("kotlin") {
        android()
        js(IR) { nodejs {} }

        sourceSets {
            val commonMain by getting
            val commonTest by getting {
                dependencies {
                    implementation(project(":shared:test"))

                    implementation(kotestEngine)
                    implementation(kotestAssertion)
                }
            }
            val androidMain by getting
            val androidTest by getting {
                dependencies {
                    implementation(Libraries.Coroutines.test)
                    implementation(kotestRunnerJunit5)
                    implementation(Libraries.MockK.android)
                }
            }
            val jsMain by getting
            val jsTest by getting {
                dependencies {
                    implementation(enforcedPlatform(kotlinWrappersBom))
                    implementation(Libraries.JsWrappers.extensions)
                }
            }
        }

        configure()
    }
