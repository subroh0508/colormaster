import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

val node = version("node")
val yarn = version("yarn")

plugins.withType<YarnPlugin> {
    the<YarnRootExtension>().apply {
        version = yarn
        //lockFileDirectory = projectDir
        //ignoreScripts = false
    }
}

plugins.withType<NodeJsRootPlugin> {
    the<NodeJsRootExtension>().apply {
        nodeVersion = node
        // @see: https://youtrack.jetbrains.com/issue/KT-49109
        versions.webpackCli.version = "4.9.0"
    }
}
