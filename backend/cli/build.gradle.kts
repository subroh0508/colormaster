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

    // YAML
    implementation(libs.snakeyaml)

    // SQLDelight
    implementation(libs.sqldelight.jvm.driver)
    implementation(libs.sqldelight.coroutines.extensions)
    implementation(libs.sqldelight.primitive.adapters)
}

val fetchIdolColors by tasks.registering(JavaExec::class) {
    group = "colormaster"
    description = "Fetch idol member colors from im@sparql"
    mainClass.set("net.subroh0508.colormaster.backend.cli.commands.FetchIdolColorsCommand")
    classpath(sourceSets["main"].runtimeClasspath)
}

val importIdolsFromYaml by tasks.registering(JavaExec::class) {
    group = "colormaster"
    description = "Import idol records from result.yaml to color_master.db"
    mainClass.set("net.subroh0508.colormaster.backend.cli.commands.ImportIdolsFromYamlCommand")
    classpath(sourceSets["main"].runtimeClasspath)
}

val exportIdolsToYaml by tasks.registering(JavaExec::class) {
    group = "colormaster"
    description = "Export idol records from color_master.db to YAML file"
    mainClass.set("net.subroh0508.colormaster.backend.cli.commands.ExportIdolsToYamlCommand")
    classpath(sourceSets["main"].runtimeClasspath)
}
