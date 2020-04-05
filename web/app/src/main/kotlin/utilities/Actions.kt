package utilities

import kotlinext.js.jsObject

external interface Actions<T: Enum<*>, V> {
    var type: T
    var payload: V
}

fun <T: Enum<*>, V> actions(block: Actions<T, V>.() -> Unit) = jsObject(block)
