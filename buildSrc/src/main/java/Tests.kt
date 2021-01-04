import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestReport
import org.gradle.kotlin.dsl.create

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
