package components

import androidx.compose.runtime.*
import externals.MDCList
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLElement

@Composable
fun List(
    singleLine: Boolean = true,
    content: @Composable () -> Unit,
) {
    var element by remember { mutableStateOf<HTMLElement?>(null) }

    SideEffect {
        element?.let { MDCList(it) }
    }

    Ul({
        classes(*listClasses(singleLine))
        ref {
            element = it
            onDispose { element = it }
        }
    }) {
        content()
    }
}

@Composable
fun ListItem(
    content: @Composable () -> Unit,
) {
    val element = rememberRippleElement()

    Li({
        classes("mdc-deprecated-list-item")
        ref {
            element.value = it
            onDispose { element.value = null }
        }
    }) {
        Span({ classes("mdc-deprecated-list-item__ripple") })
        Span({ classes("mdc-deprecated-list-item__text") }) {
            content()
        }
    }
}

@Composable
fun ListGroup(content: @Composable () -> Unit) {
    Div({ classes("mdc-deprecated-list-group") }) {
        content()
    }
}

@Composable
fun ListGroupSubHeader(content: @Composable () -> Unit) {
    H3({ classes("mdc-deprecated-list-group__subheader") }) {
        content()
    }
}

private fun listClasses(singleLine: Boolean) = listOfNotNull(
    "mdc-deprecated-list",
    if (singleLine) null else "mdc-deprecated-list--two-line",
).toTypedArray()
