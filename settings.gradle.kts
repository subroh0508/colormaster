include(
    ":android:app",
    ":web:app",
    ":shared:utilities",
    ":shared:model",
    ":shared:components:core",
    ":shared:infra:repository",
    ":shared:infra:query",
    ":shared:infra:api",
    ":shared:presentation:search",
    ":shared:presentation:preview"
)

enableFeaturePreview("GRADLE_METADATA")
