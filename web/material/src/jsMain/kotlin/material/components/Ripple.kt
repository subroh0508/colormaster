package material.components

import androidx.compose.runtime.*
import material.externals.attachRippleTo
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.HTMLElement
import material.utilities.TagElementBuilder


@Composable
fun Ripple(
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    tag: String = "div",
    unbounded: Boolean = false,
    content: @Composable () -> Unit,
) {
    val element = rememberRippleElement(unbounded)

    TagElement(
        TagElementBuilder(tag),
        {
            applyAttrs?.invoke(this)

            classes("mdc-ripple-surface")
            ref {
                element.value = it
                onDispose { element.value = null }
            }
        },
    ) { content() }
}

@Composable
fun rememberRippleElement(unbounded: Boolean = false): MutableState<HTMLElement?> {
    val element = remember { mutableStateOf<HTMLElement?>(null) }

    SideEffect {
        element.value?.let {
            val ripple = attachRippleTo(it) { isUnbounded = unbounded }
            console.log(ripple)
        }
    }

    return element
}
