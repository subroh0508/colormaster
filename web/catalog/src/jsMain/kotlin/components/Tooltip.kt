package components

import androidx.compose.runtime.*
import externals.MDCTooltip
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun Tooltip(
    id: String,
    content: @Composable () -> Unit,
) {
    var tooltip by remember { mutableStateOf<MDCTooltip?>(null) }

    Div({
        id(id)
        classes("mdc-tooltip")
        attr("role", "tooltip")
        attr("aria-hidden", "true")

        ref {
            tooltip = MDCTooltip(it)
            onDispose { tooltip = null }
        }
    }) {
        Div({ classes("mdc-tooltip__surface", "mdc-tooltip__surface-animation") }) {
            content()
        }
    }
}

@Composable
fun Tooltip(id: String, text: String) = Tooltip(id) { Text(text) }
