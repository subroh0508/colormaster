@file:Suppress("HardcodedStringLiteral")

import org.gradle.api.Project

val Project.kotlinVersion get() = version("kotlin")
val Project.kotlinGradlePlugin get() = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
val Project.kotlinxSerializationGradlePlugin get() = "org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion"
val Project.kotlinWrappersBom get() = "org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:${version("kotlin-wrappers-bom")}"
