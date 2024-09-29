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
    ":core:data:api:imasparql",
    ":core:data:api:authentication",
    ":core:data:api:firestore",
    ":core:features:home",
    ":core:features:preview",
    ":core:features:search",
    ":core:features:myidols",
    ":core:test",
)
