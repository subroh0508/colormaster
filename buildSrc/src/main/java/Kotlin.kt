@file:Suppress("HardcodedStringLiteral")

import org.gradle.api.Project

val Project.kotlinVersion get() = version("kotlin")
val Project.kotlinStdlib get() = "org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion"
val Project.kotlinGradlePlugin get() = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
