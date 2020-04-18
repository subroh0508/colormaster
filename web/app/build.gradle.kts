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
                implementation(project(":shared:utilities"))
                implementation(project(":shared:components:core"))
                implementation(project(":shared:model"))
                implementation(project(":shared:infra:repository"))

                implementation(Libraries.Kotlin.js)

                implementation(Libraries.Coroutines.js)

                implementation(Libraries.Ktor.clientJs)
                implementation(Libraries.Ktor.jsonJs)
                implementation(Libraries.Ktor.serializationJs)

                implementation(Libraries.Serialization.js)

                implementation(Libraries.Html.js)

                implementation(Libraries.JsWrappers.react)
                implementation(Libraries.JsWrappers.reactDom)
                implementation(Libraries.JsWrappers.reactRouterDom)
                implementation(Libraries.JsWrappers.css)
                implementation(Libraries.JsWrappers.styled)
                implementation(Libraries.JsWrappers.MaterialUi.core)
                implementation(Libraries.JsWrappers.MaterialUi.lab)

                implementation(Libraries.Kodein.js)

                implementation(npm(Libraries.Npm.react, Libraries.Npm.reactVersion))
                implementation(npm(Libraries.Npm.reactDom, Libraries.Npm.reactVersion))
                implementation(npm(Libraries.Npm.reactRouterDom, "5.1.2" /* Libraries.Npm.reactRouterDomVersion */))
                implementation(npm(Libraries.Npm.styledComponent, Libraries.Npm.styledComponentVersion))
                implementation(npm(Libraries.Npm.inlineStylePrefixer, Libraries.Npm.inlineStylePrefixerVersion))
                implementation(npm(Libraries.Npm.abortController, Libraries.Npm.abortControllerVersion))
                implementation(npm(Libraries.Npm.textEncoding, Libraries.Npm.textEncodingVersion))
            }
        }
    }
}
