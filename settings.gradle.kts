pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        includeBuild("plugins")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

include(
    ":android:app",
    ":web:app",
    ":web:material",
    ":web:catalog",
    ":core:common",
    ":core:model",
    ":core:data:repository",
    ":core:network:imasparql",
    ":core:network:authentication",
    ":core:network:firestore",
    ":core:features:home",
    ":core:features:preview",
    ":core:features:search",
    ":core:features:myidols",
    ":core:test",
)
