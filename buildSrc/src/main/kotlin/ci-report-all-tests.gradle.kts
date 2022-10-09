import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestReport
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest

val testDebugUnitTestReport by tasks.registering(TestReport::class) {
    destinationDir = file("$buildDir/reports/allTests/testDebugUnitTest")

    subprojects.forEach {
        val test = it.tasks.findByName("testDebugUnitTest") as? Test
        if (test != null) reportOn(test)
    }
}

val jsNodeTestReport by tasks.registering(TestReport::class) {
    destinationDir = file("$buildDir/reports/allTests/jsNodeTest")

    subprojects.forEach {
        val test = it.tasks.findByName("jsNodeTest") as? KotlinJsTest
        if (test != null) reportOn(test.binaryResultsDirectory)
    }
}
