package net.subroh0508.colormaster.primitive

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.TestReport
import org.gradle.kotlin.dsl.create
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest

@Suppress("unused")
class AllTestReportPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with (target) {
            tasks.create("testDebugUnitTestReport", TestReport::class) {
                destinationDirectory.set(
                    file("${layout.buildDirectory.asFile.get().absoluteFile}/reports/allTests/testDebugUnitTest"),
                )

                subprojects.forEach {
                    val test = it.tasks.findByName("testDebugUnitTest") as? Test
                    if (test != null) testResults.from(test.binaryResultsDirectory)
                }
            }

            tasks.create("jsBrowserTestReport", TestReport::class) {
                destinationDirectory.set(
                    file("${layout.buildDirectory.asFile.get().absoluteFile}/reports/allTests/jsBrowserTest"),
                )

                subprojects.forEach {
                    val test = it.tasks.findByName("jsBrowserTest") as? KotlinJsTest
                    if (test != null) testResults.from(test.binaryResultsDirectory)
                }
            }
        }
    }
}