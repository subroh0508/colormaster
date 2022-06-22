package material.components

import androidx.compose.runtime.Composable
import androidx.compose.web.events.SyntheticMouseEvent
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement

@Composable
fun Icon(
    icon: String,
    applyAttrs: (AttrsScope<HTMLButtonElement>.() -> Unit)? = null,
    onClick: ((SyntheticMouseEvent) -> Unit)? = null,
) {
    if (onClick == null) {
        ComposableIcon(icon)
        return
    }

    val element = rememberRippleElement(unbounded = true)

    Button({
        applyAttrs?.invoke(this)
        classes("mdc-icon-button")
        ref {
            element.value = it
            onDispose { element.value = null }
        }

        onClick(onClick)
    }) {
        Span({ classes("mdc-icon-button__ripple") })
        Span({ classes("mdc-icon-button__focus-ring") })
        ComposableIcon(icon)
    }
}

@Composable
private fun ComposableIcon(icon: String) {
    I({ classes("material-icons") }) {
        Text(icon)
    }
}
