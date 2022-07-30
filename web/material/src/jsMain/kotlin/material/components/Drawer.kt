package material.components

import androidx.compose.runtime.*
import material.externals.MDCDrawer
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement

@Composable
fun ModalDrawer(
    headerContent: (@Composable () -> Unit)? = null,
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
        headerContent?.invoke()
        Div({ classes("mdc-drawer__content") }) { drawerContent(drawer) }
    }

    Div({ classes("mdc-drawer-scrim") })
    Div { mainContent(drawer) }
}

@Composable
fun DrawerHeader(
    titleTag: String = "h3",
    subtitleTag: String = "h6",
    title: @Composable () -> Unit = {},
    subtitle: @Composable () -> Unit = {},
) {
    Div({ classes("mdc-drawer__header") }) {
        TagElement<HTMLElement>(
            titleTag,
            { classes("mdc-theme-text-primary", "mdc-drawer__title") },
        ) { title() }
        TagElement<HTMLElement>(
            subtitleTag,
            { classes("mdc-theme-text-secondary", "mdc-drawer__subtitle") },
        ) { subtitle() }
    }
}

@Composable
fun DrawerHeader(
    titleTag: String = "h3",
    subtitleTag: String = "h6",
    title: String? = null,
    subtitle: String? = null,
) = DrawerHeader(titleTag, subtitleTag, { title?.let { Text(it) } }, { subtitle?.let { Text(it) } })

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
) { ListItem(text, applyAttrs, activated = activated, tag = "a") }
