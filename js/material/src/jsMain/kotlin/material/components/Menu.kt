package material.components

import androidx.compose.runtime.*
import androidx.compose.web.events.SyntheticMouseEvent
import material.externals.MDCMenu
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun Menu(
    anchor: @Composable (MDCMenu?) -> Unit,
    content: @Composable () -> Unit,
) {
    var menu by remember { mutableStateOf<MDCMenu?>(null) }

    Div({ classes("mdc-menu-surface--anchor") }) {
        anchor(menu)

        Div({
            classes("mdc-menu", "mdc-menu-surface")

            ref {
                menu = MDCMenu(it)
                onDispose { menu = null }
            }
        }) {
            List({
                attr("role", "menu")
                attr("aria-hidden", "true")
                attr("aria-orientation", "vertical")
                attr("tabindex", "-1")
            }) { content() }
        }
    }
}

@Composable
fun MenuItem(
    onClick: (SyntheticMouseEvent) -> Unit = {},
    activated: Boolean = false,
    content: @Composable () -> Unit,
) = ListItem(
    {
        attr("role", "menuitem")

        onClick(onClick)
    },
    activated,
) { content() }

@Composable
fun MenuItem(
    text: String,
    activated: Boolean = false,
    onClick: (SyntheticMouseEvent) -> Unit = {},
) = MenuItem(onClick, activated) { Text(text) }
