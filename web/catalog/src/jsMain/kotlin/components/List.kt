package components

import androidx.compose.runtime.*
import externals.MDCList
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement
import utilities.TagElementBuilder

@Composable
fun List(
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    singleLine: Boolean = true,
    tag: String = "ul",
    content: @Composable () -> Unit,
) {
    var element by remember { mutableStateOf<HTMLElement?>(null) }

    SideEffect {
        element?.let { MDCList(it) }
    }

    TagElement<HTMLElement>(
        tag,
        {
            applyAttrs?.invoke(this)

            classes(*listClasses(singleLine))
            ref {
                element = it
                onDispose { element = it }
            }
        },
    ) { content() }
}

@Composable
fun ListItem(
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    activated: Boolean = false,
    tag: String = "li",
    content: @Composable () -> Unit,
) {
    val element = rememberRippleElement()

    TagElement<HTMLElement>(
        tag,
        {
            applyAttrs?.invoke(this)

            classes(*listItemClasses(activated))
            ref {
                element.value = it
                onDispose { element.value = null }
            }
        }
    ) {
        Span({ classes("mdc-deprecated-list-item__ripple") })
        content()
    }
}

@Composable
fun ListItem(
    text: String,
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    activated: Boolean = false,
    tag: String = "li",
) = ListItem(applyAttrs, activated = activated, tag = tag) { ListItemText { Text(text) } }

@Composable
fun ListItemText(
    content: @Composable () -> Unit,
) {
    Span({ classes("mdc-deprecated-list-item__text") }) {
        content()
    }
}

@Composable
fun ListGroup(content: @Composable () -> Unit) {
    Div({ classes("mdc-deprecated-list-group") }) {
        content()
    }
}

@Composable
fun ListGroupSubHeader(
    tag: String = "h6",
    content: @Composable () -> Unit,
) {
    TagElement<HTMLElement>(
        tag,
        { classes("mdc-deprecated-list-group__subheader") },
    ) { content() }
}

@Composable
fun ListGroupSubHeader(
    text: String,
    tag: String = "h6",
) = ListGroupSubHeader(tag = tag) { Text(text) }

@Composable
fun Divider() = Hr { classes("mdc-deprecated-list-divider") }

private fun listClasses(singleLine: Boolean) = listOfNotNull(
    "mdc-deprecated-list",
    if (singleLine) null else "mdc-deprecated-list--two-line",
).toTypedArray()

private fun listItemClasses(activated: Boolean) = listOfNotNull(
    "mdc-deprecated-list-item",
    if (activated) "mdc-deprecated-list-item--activated" else null,
).toTypedArray()
