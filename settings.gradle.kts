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
    ":shared:components:core",
    ":shared:data:model",
    ":shared:data:repository",
    ":shared:data:api:imasparql",
    ":shared:data:api:authentication",
    ":shared:data:api:firestore",
    ":shared:data:api:jsfirebaseapp",
    ":shared:features:home",
    ":shared:features:preview",
    ":shared:features:search",
    ":shared:features:myidols",
    ":shared:test"
)
