package net.subroh0508.colormaster.primitive

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestReport
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest

@Suppress("unused")
class AllTestReportPlugin : Plugin<Project> {
    companion object {
        private const val TASK_TEST_DEBUG_UNIT_TEST = "testDebugUnitTest"
        private const val TASK_JS_BROWSER_TEST = "jsBrowserTest"

        private const val TASK_TEST_DEBUG_UNIT_TEST_REPORT = "${TASK_TEST_DEBUG_UNIT_TEST}Report"
        private const val TASK_JS_BROWSER_TEST_REPORT = "${TASK_JS_BROWSER_TEST}Report"
    }

    override fun apply(target: Project) {
        applyAndroidUnitTestReport(target)
        applyJsBrowserUnitTestReport(target)
    }

    private fun applyAndroidUnitTestReport(target: Project) = with (target) {
        tasks.register(TASK_TEST_DEBUG_UNIT_TEST_REPORT, TestReport::class) {
            destinationDirectory.set(
                layout.buildDirectory.dir("reports/allTests/${TASK_TEST_DEBUG_UNIT_TEST}").get().asFile,
            )

            subprojects.forEach {
                val test = it.tasks.findByName(TASK_TEST_DEBUG_UNIT_TEST) as? Test
                if (test != null) testResults.from(test.binaryResultsDirectory)
            }
        }
    }

    private fun applyJsBrowserUnitTestReport(target: Project) = with (target) {
        tasks.register(TASK_JS_BROWSER_TEST_REPORT, TestReport::class) {
            destinationDirectory.set(
                layout.buildDirectory.dir("reports/allTests/${TASK_JS_BROWSER_TEST}").get().asFile,
            )

            subprojects.forEach {
                val test = it.tasks.findByName(TASK_JS_BROWSER_TEST) as? KotlinJsTest
                if (test != null) testResults.from(test.binaryResultsDirectory)
            }
        }
    }
}
