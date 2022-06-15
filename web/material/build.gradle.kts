import org.jetbrains.compose.compose
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    js(IR) { nodejs() }

    sourceSets {
        named("jsMain") {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.web.svg)
                implementation(compose.runtime)

                api(npm("@material/button", Libraries.Npm.materialComponentWeb))
                api(npm("@material/card", Libraries.Npm.materialComponentWeb))
                api(npm("@material/chips", Libraries.Npm.materialComponentWeb))
                api(npm("@material/checkbox", Libraries.Npm.materialComponentWeb))
                api(npm("@material/drawer", Libraries.Npm.materialComponentWeb))
                api(npm("@material/form-field", Libraries.Npm.materialComponentWeb))
                api(npm("@material/icon-button", Libraries.Npm.materialComponentWeb))
                api(npm("@material/list", Libraries.Npm.materialComponentWeb))
                api(npm("@material/menu", Libraries.Npm.materialComponentWeb))
                api(npm("@material/ripple", Libraries.Npm.materialComponentWeb))
                api(npm("@material/tab-bar", Libraries.Npm.materialComponentWeb))
                api(npm("@material/textfield", Libraries.Npm.materialComponentWeb))
                api(npm("@material/tooltip", Libraries.Npm.materialComponentWeb))
                api(npm("@material/top-app-bar", Libraries.Npm.materialComponentWeb))
                api(npm("@material/typography", Libraries.Npm.materialComponentWeb))
                api(npm("@material/theme", Libraries.Npm.materialComponentWeb))
            }
        }
    }
}
