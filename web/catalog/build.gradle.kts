import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
                outputFileName = "catalog.bundle.js"
            }
            runTask {
                sourceMaps = true
                devServer = KotlinWebpackConfig.DevServer(
                    port = 8088,
                    contentBase = mutableListOf("${projectDir.path}/src/jsMain/resources"),
                )
            }
            webpackTask {
                sourceMaps = false
            }
        }

        useCommonJs()
        binaries.executable()
    }

    sourceSets {
        named("jsMain") {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.web.svg)
                implementation(compose.runtime)

                implementation(npm("@material/button", Libraries.Npm.materialComponentWeb))
                implementation(npm("@material/card", Libraries.Npm.materialComponentWeb))
                implementation(npm("@material/chips", Libraries.Npm.materialComponentWeb))
                implementation(npm("@material/checkbox", Libraries.Npm.materialComponentWeb))
                implementation(npm("@material/form-field", Libraries.Npm.materialComponentWeb))
                implementation(npm("@material/list", Libraries.Npm.materialComponentWeb))
                implementation(npm("@material/ripple", Libraries.Npm.materialComponentWeb))
                implementation(npm("@material/tab-bar", Libraries.Npm.materialComponentWeb))
                implementation(npm("@material/textfield", Libraries.Npm.materialComponentWeb))
                implementation(npm("@material/typography", Libraries.Npm.materialComponentWeb))
                implementation(npm("@material/theme", Libraries.Npm.materialComponentWeb))

                implementation(devNpm("sass", "^1.51.0"))
                implementation(devNpm("sass-loader", "^12.6.0"))
                implementation(devNpm("extract-loader", "^5.1.0"))
                implementation(devNpm("file-loader", "^6.2.0"))
                implementation(devNpm("autoprefixer", "^10.4.7"))
                implementation(devNpm("postcss-loader", "^6.2.1"))
            }
        }
    }
}
