package net.subroh0508.colormaster.common.external

import kotlin.js.RegExp

// @see: https://github.com/JetBrains/kotlin-wrappers/blob/master/kotlin-extensions/src/jsMain/kotlin/kotlinext/js/CommonJS.kt

external interface Context<T> : JsFunction1<String, T> {
    fun resolve(module: String): String
    fun keys(): Array<String>
    val id: Int
}

fun <T> requireAll(context: Context<T>) = context.keys().forEach(context::invoke)

external object require {
    fun resolve(module: String): String

    // Note: require.context is a webpack-specific function
    fun <T> context(directory: String, useSubdirectories: Boolean = definedExternally, regExp: RegExp = definedExternally, mode: String = definedExternally): Context<T>
}

external fun <T> require(module: String): T
