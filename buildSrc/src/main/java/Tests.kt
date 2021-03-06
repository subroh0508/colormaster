import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestReport
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest

fun Project.testDebugUnitTestReport() {
    tasks.create("testDebugUnitTestReport", TestReport::class) {
        destinationDir = file("$buildDir/reports/allTests/testDebugUnitTest")

        gradle.afterProject {
            afterEvaluate {
                val test = tasks.findByName("testDebugUnitTest") as? Test
                if (test != null) reportOn(test)
            }
        }
    }
}

fun Project.jsNodeTestReport() {
    tasks.create("jsNodeTestReport", TestReport::class) {
        destinationDir = file("$buildDir/reports/allTests/jsNodeTest")

        gradle.afterProject {
            afterEvaluate {
                val test = tasks.findByName("jsNodeTest") as? KotlinJsTest
                if (test != null) reportOn(test.binaryResultsDirectory)
            }
        }
    }
}
