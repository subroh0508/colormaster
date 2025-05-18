plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlinx.serialization)
}

dependencies {
    implementation(project(":backend:server"))

    // Add Ktor client dependencies
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)  // For JVM platform
    implementation(libs.ktor.client.json)
    implementation(libs.ktor.serialization.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.kotlinx.coroutines.core)

    // Logging
    implementation(libs.okhttp3.logging.interceptor)

    // SQLDelight
    implementation(libs.sqldelight.jvm.driver)
    implementation(libs.sqldelight.coroutines.extensions)
    implementation(libs.sqldelight.primitive.adapters)
}

val fetchIdolColorsFromImasparql by tasks.registering(JavaExec::class) {
    group = "colormaster"
    description = "Fetch idol member colors from im@sparql"
    mainClass.set("net.subroh0508.colormaster.backend.cli.commands.FetchIdolColorsFromImasparqlCommand")
    classpath(sourceSets["main"].runtimeClasspath)
}
