package components

import androidx.compose.runtime.Composable
import androidx.compose.web.events.SyntheticMouseEvent
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun Icon(
    icon: String,
    onClick: ((SyntheticMouseEvent) -> Unit)? = null,
) {
    if (onClick == null) {
        ComposableIcon(icon)
        return
    }

    val element = rememberRippleElement(unbounded = true)

    Button({
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
