import org.gradle.api.Project
import org.gradle.api.Task
import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

internal fun Project.prop(propertyName: String) = property(propertyName) as String
internal fun Task.prop(propertyName: String) = project.prop(propertyName)

fun Project.enforcedPlatform(dependencyNotation: Any) = dependencies.enforcedPlatform(dependencyNotation)
