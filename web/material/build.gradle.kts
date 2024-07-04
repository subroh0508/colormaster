plugins {
    alias(libs.plugins.kotlin.mpp)
    alias(libs.plugins.jetbrains.compose)
}

kotlin {
    js(IR) { nodejs() }

    sourceSets {
        named("jsMain") {
            dependencies {
                implementation(compose.web.core)
                implementation(compose.web.svg)
                implementation(compose.runtime)

                api(npm("@material/button", libs.versions.npm.material.component.web.get()))
                api(npm("@material/card", libs.versions.npm.material.component.web.get()))
                api(npm("@material/chips", libs.versions.npm.material.component.web.get()))
                api(npm("@material/checkbox", libs.versions.npm.material.component.web.get()))
                api(npm("@material/drawer", libs.versions.npm.material.component.web.get()))
                api(npm("@material/form-field", libs.versions.npm.material.component.web.get()))
                api(npm("@material/icon-button", libs.versions.npm.material.component.web.get()))
                api(npm("@material/list", libs.versions.npm.material.component.web.get()))
                api(npm("@material/menu", libs.versions.npm.material.component.web.get()))
                api(npm("@material/ripple", libs.versions.npm.material.component.web.get()))
                api(npm("@material/tab-bar", libs.versions.npm.material.component.web.get()))
                api(npm("@material/textfield", libs.versions.npm.material.component.web.get()))
                api(npm("@material/tooltip", libs.versions.npm.material.component.web.get()))
                api(npm("@material/top-app-bar", libs.versions.npm.material.component.web.get()))
                api(npm("@material/typography", libs.versions.npm.material.component.web.get()))
                api(npm("@material/theme", libs.versions.npm.material.component.web.get()))
            }
        }
    }
}
