package components

import androidx.compose.runtime.*
import externals.attachRippleTo
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.HTMLElement
import utilities.TagElementBuilder


@Composable
fun Ripple(
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    tag: String = "div",
    unbounded: Boolean = false,
    content: @Composable () -> Unit,
) {
    var element by remember { mutableStateOf<HTMLElement?>(null) }

    SideEffect {
        element?.let { attachRippleTo(it) { isUnbounded = unbounded } }
    }

    TagElement(
        TagElementBuilder(tag),
        {
            applyAttrs?.invoke(this)

            classes("mdc-ripple-surface")
            ref {
                element = it
                onDispose { element = null }
            }
        },
    ) { content() }
}
