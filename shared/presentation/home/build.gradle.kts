plugins {
    `shared-presentation`
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":shared:data:model"))
                implementation(project(":shared:data:repository"))
            }
        }
    }
}
