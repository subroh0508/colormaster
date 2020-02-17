import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("js")
    id("kotlinx-serialization")
}

kotlin {
    target {
        useCommonJs()
        browser {
            runTask {
                sourceMaps = true
                devServer = KotlinWebpackConfig.DevServer(
                    port = 8088,
                    contentBase = listOf("${projectDir.path}/src/main/resources")
                )
                outputFileName = "bundle.js"
            }
            webpackTask {
                sourceMaps = false
                outputFileName = "bundle.js"
            }
        }
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation(Libraries.Kotlin.js)
                implementation(Libraries.Serialization.js)
            }
        }
    }
}
