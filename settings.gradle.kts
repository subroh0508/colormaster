include(
    ":android:app",
    ":web:app",
    ":shared:model",
    ":shared:components:core",
    ":shared:api:imasparql",
    ":shared:api:authentication",
    ":shared:api:jsfirebaseapp",
    ":shared:infra:repository",
    ":shared:infra:db",
    ":shared:presentation:common",
    ":shared:presentation:search",
    ":shared:presentation:preview",
    ":shared:test"
)

enableFeaturePreview("GRADLE_METADATA")
