package components

import androidx.compose.runtime.*
import externals.MDCDrawer
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.Aside
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLElement

@Composable
fun ModalDrawer(
    drawerContent: @Composable (MDCDrawer?) -> Unit,
    mainContent: @Composable (MDCDrawer?) -> Unit,
) {
    var element by remember { mutableStateOf<HTMLElement?>(null) }
    var drawer by remember { mutableStateOf<MDCDrawer?>(null) }

    SideEffect {
        element?.let { drawer = MDCDrawer(it) }
    }

    Aside({
        classes("mdc-drawer", "mdc-drawer--modal")

        ref {
            element = it
            onDispose { element = null }
        }
    }) {
        Div({ classes("mdc-drawer__content") }) { drawerContent(drawer) }
    }

    Div({ classes("mdc-drawer-scrim") })
    Div { mainContent(drawer) }
}

@Composable
fun DrawerContent(content: @Composable () -> Unit) { List(tag = "nav") { content() } }

@Composable
fun DrawerListItem(
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    activated: Boolean = false,
    content: @Composable () -> Unit,
) { ListItem(applyAttrs, activated = activated, tag = "a") { content() } }

@Composable
fun DrawerListItem(
    text: String,
    activated: Boolean = false,
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
) { ListItem(text, applyAttrs, activated = activated, tag = "a")}
