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
    ":js:app",
    ":js:material",
    ":core:common",
    ":core:model",
    ":core:data",
    ":core:network:auth",
    ":core:network:firestore",
    ":core:network:imasparql",
    ":core:features:home",
    ":core:features:preview",
    ":core:features:search",
    ":core:features:myidols",
    ":core:test",
    ":backend:server",
    ":backend:cli"
)
