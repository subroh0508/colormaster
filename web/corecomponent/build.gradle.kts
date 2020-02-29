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
                implementation(project(":shared:domain:valueobject"))
                implementation(project(":shared:repository"))

                implementation(Libraries.Kotlin.js)

                implementation(Libraries.Coroutines.js)

                implementation(Libraries.Ktor.clientJs)
                implementation(Libraries.Ktor.jsonJs)
                implementation(Libraries.Ktor.serializationJs)

                implementation(Libraries.Serialization.js)

                implementation(Libraries.Html.js)

                implementation(Libraries.Kodein.js)

                implementation(npm(Libraries.Npm.abortController, Libraries.Npm.abortControllerVersion))
                implementation(npm(Libraries.Npm.textEncoding, Libraries.Npm.textEncodingVersion))
            }
        }
    }
}
