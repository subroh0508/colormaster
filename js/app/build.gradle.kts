import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlin.mpp)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    js(IR) {
        useCommonJs()
        binaries.executable()

        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
                outputFileName = "bundle.js"
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
                implementation(project(":core:common"))
                implementation(project(":core:model"))
                implementation(project(":core:data"))
                implementation(project(":core:features:home"))
                implementation(project(":core:features:preview"))
                implementation(project(":core:features:search"))
                implementation(project(":core:features:myidols"))

                implementation(project(":js:material"))

                implementation(compose.html.core)
                implementation(compose.html.svg)
                implementation(compose.runtime)

                implementation(libs.kotlinx.coroutines.js)

                implementation(libs.ktor.client.js)
                implementation(libs.ktor.client.json.js)
                implementation(libs.ktor.serialization.js)

                implementation(libs.kotlinx.coroutines.core)

                implementation(dependencies.platform(libs.kotlin.wrappers.bom))
                implementation(libs.kotlin.wrappers.js)

                implementation(libs.koin.core)
                implementation(libs.decompose.core)

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

val jsBrowserDistribution = tasks.getByName("jsBrowserDistribution")

val copyDistributions by tasks.registering {
    doLast {
        copy {
            val destinationDir = File("$rootDir/public")
            if (!destinationDir.exists()) {
                destinationDir.mkdir()
            }
            val distributions =
                File("${layout.buildDirectory.asFile.get().absoluteFile}/dist/js/productionExecutable/")
            from(distributions)
            into(destinationDir)
        }
    }
}

jsBrowserDistribution.finalizedBy(copyDistributions)
