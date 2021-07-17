import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("js")
    id("kotlinx-serialization")
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
                    contentBase = mutableListOf("${projectDir.path}/src/main/resources")
                )
            }
            webpackTask {
                sourceMaps = false
            }
        }
    }

    sourceSets {
        val main by getting {
            dependencies {
                implementation(project(":shared:components:core"))
                implementation(project(":shared:model"))
                implementation(project(":shared:infra:repository"))
                implementation(project(":shared:presentation:common"))
                implementation(project(":shared:presentation:search"))
                implementation(project(":shared:presentation:preview"))

                implementation(Libraries.Coroutines.js)

                implementation(Libraries.Ktor.clientJs)
                implementation(Libraries.Ktor.jsonJs)
                implementation(Libraries.Ktor.serializationJs)

                implementation(Libraries.Serialization.core)

                implementation(Libraries.Html.js)
                implementation(Libraries.Css(kotlinVersion).js)

                implementation(Libraries.JsWrappers(kotlinVersion).react)
                implementation(Libraries.JsWrappers(kotlinVersion).reactDom)
                implementation(Libraries.JsWrappers(kotlinVersion).reactRouterDom)
                implementation(Libraries.JsWrappers(kotlinVersion).styled)
                implementation(Libraries.JsWrappers(kotlinVersion).extensions)
                implementation(Libraries.JsWrappers.MaterialUi.core)

                implementation(Libraries.Koin.core)

                implementation(npm(Libraries.Npm.reactAutoSuggest, Libraries.Npm.reactAutoSuggestVersion))
                implementation(npm(Libraries.Npm.I18next.core, Libraries.Npm.I18next.version))
                implementation(npm(Libraries.Npm.I18next.httpBackend, Libraries.Npm.I18next.httpBackendVersion))
                implementation(npm(Libraries.Npm.I18next.react, Libraries.Npm.I18next.reactVersion))

                implementation(devNpm("html-webpack-plugin", "^5.3.1"))
                implementation(devNpm("webpack-cdn-plugin", "^3.3.1"))
            }
        }
    }
}


val browserWebpack = tasks.getByName("browserProductionWebpack")

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

browserWebpack.finalizedBy(copyDistributions)
