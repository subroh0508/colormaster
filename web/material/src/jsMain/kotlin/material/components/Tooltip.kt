package material.components

import androidx.compose.runtime.*
import material.externals.MDCTooltip
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

@Composable
fun Tooltip(
    id: String,
    hideDelayMs: Long = 600L,
    showDelayMs: Long = 500L,
    content: @Composable () -> Unit,
) {
    var tooltip by remember { mutableStateOf<HTMLElement?>(null) }

    SideEffect {
        tooltip?.let {
            MDCTooltip(it).apply {
                setHideDelay(hideDelayMs)
                setShowDelay(showDelayMs)
            }
        }
    }

    Div({
        id(id)
        classes("mdc-tooltip")
        attr("role", "tooltip")
        attr("aria-hidden", "true")

        ref {
            tooltip = it
            onDispose { tooltip = null }
        }
    }) {
        Div({ classes("mdc-tooltip__surface", "mdc-tooltip__surface-animation") }) {
            content()
        }
    }
}

@Composable
fun Tooltip(
    id: String,
    text: String,
    hideDelayMs: Long = 600L,
    showDelayMs: Long = 500L,
) = Tooltip(id, hideDelayMs, showDelayMs) { Text(text) }
