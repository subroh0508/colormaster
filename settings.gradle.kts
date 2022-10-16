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
    ":shared:components:core",
    ":shared:data:model",
    ":shared:data:repository",
    ":shared:data:api:imasparql",
    ":shared:data:api:authentication",
    ":shared:data:api:firestore",
    ":shared:data:api:jsfirebaseapp",
    ":shared:features:home",
    ":shared:features:preview",
    ":shared:presentation:common",
    ":shared:presentation:search",
    ":shared:presentation:myidols",
    ":shared:test"
)
