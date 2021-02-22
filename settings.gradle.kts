include(
    ":android:app",
    ":web:app",
    ":shared:model",
    ":shared:components:core",
    ":shared:infra:repository",
    ":shared:infra:query",
    ":shared:infra:api",
    ":shared:infra:db",
    ":shared:presentation:common",
    ":shared:presentation:search",
    ":shared:presentation:preview",
    ":shared:test"
)

enableFeaturePreview("GRADLE_METADATA")
