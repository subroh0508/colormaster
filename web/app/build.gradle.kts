import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    js(IR) {
        useCommonJs()
        binaries.executable()

        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
                outputFileName = "bundle.js"
                bundleAnalyzerReportDir = rootProject.buildDir.resolve("analyze")
            }
            runTask {
                sourceMaps = true
                devServer = KotlinWebpackConfig.DevServer(
                    port = 8088,
                    contentBase = mutableListOf("${projectDir.path}/src/jsMain/resources")
                )
            }
            webpackTask {
                sourceMaps = false
            }
        }
    }

    sourceSets {
        named("jsMain") {
            dependencies {
                implementation(project(":shared:components:core"))
                implementation(project(":shared:model"))
                implementation(project(":shared:infra:repository"))
                implementation(project(":shared:presentation:common"))
                implementation(project(":shared:presentation:home"))
                implementation(project(":shared:presentation:myidols"))
                implementation(project(":shared:presentation:search"))
                implementation(project(":shared:presentation:preview"))

                implementation(project(":web:material"))

                implementation(compose.web.core)
                implementation(compose.web.svg)
                implementation(compose.runtime)

                implementation(Libraries.Coroutines.js)

                implementation(Libraries.Ktor.clientJs)
                implementation(Libraries.Ktor.jsonJs)
                implementation(Libraries.Ktor.serializationJs)

                implementation(Libraries.Serialization.core)

                implementation(enforcedPlatform(kotlinWrappersBom))
                implementation(Libraries.JsWrappers.extensions)

                implementation(Libraries.Koin.core)
                implementation(Libraries.Decompose.core)

                implementation(devNpm("html-webpack-plugin", "^5.3.1"))
                implementation(devNpm("webpack-cdn-plugin", "^3.3.1"))

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

val jsBrowserWebpack = tasks.getByName("jsBrowserProductionWebpack")

val copyDistributions by tasks.registering {
    doLast {
        copy {
            val destinationDir = File("$rootDir/public")
            if (!destinationDir.exists()) {
                destinationDir.mkdir()
            }
            val distributions = File("$buildDir/distributions/")
            from(distributions)
            into(destinationDir)
        }
    }
}

jsBrowserWebpack.finalizedBy(copyDistributions)
