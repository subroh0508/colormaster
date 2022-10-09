pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        id("org.jetbrains.compose") version (extra["compose.version"] as String)
        kotlin("plugin.serialization") version (extra["kotlin.version"] as String)
        id("io.kotest.multiplatform") version (extra["kotest.version"] as String)
    }
}

include(
    ":android:app",
    ":web:app",
    ":web:material",
    ":web:catalog",
    ":shared:base",
    ":shared:model",
    ":shared:components:core",
    ":shared:api:imasparql",
    ":shared:api:authentication",
    ":shared:api:firestore",
    ":shared:api:jsfirebaseapp",
    ":shared:infra:repository",
    ":shared:presentation:common",
    ":shared:presentation:home",
    ":shared:presentation:search",
    ":shared:presentation:preview",
    ":shared:presentation:myidols",
    ":shared:test"
)
