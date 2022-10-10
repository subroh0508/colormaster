@file:Suppress("HardcodedStringLiteral")

import org.gradle.api.Project

val Project.kotlinVersion get() = version("kotlin")
val Project.kotlinGradlePlugin get() = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
val Project.kotlinxSerializationGradlePlugin get() = "org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion"
val Project.kotlinWrappersBom get() = "org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:${version("kotlin-wrappers-bom")}"

val Project.kotestVersion get() = version("kotest")
val Project.kotestEngine get() = "io.kotest:kotest-framework-engine:$kotestVersion"
val Project.kotestRunnerJunit5 get() = "io.kotest:kotest-runner-junit5:$kotestVersion"
val Project.kotestAssertion get() = "io.kotest:kotest-assertions-core:$kotestVersion"
