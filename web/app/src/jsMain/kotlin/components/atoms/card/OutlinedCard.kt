package components.atoms.card

import MaterialTheme
import androidx.compose.runtime.*
import material.components.CardContent
import material.components.CardHeader
import material.components.TypographyHeadline5
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import material.components.OutlinedCard as MaterialOutlinedCard

@Composable
fun OutlinedCard(
    header: @Composable () -> Unit,
    contentAttrsScope: ((AttrsScope<HTMLDivElement>).() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) = MaterialOutlinedCard({
    style {
        property("border-color", MaterialTheme.Var.divider)
    }
}) {
    CardHeader(attrsScope = {
        style { color(MaterialTheme.Var.textPrimary) }
    }) {
        TypographyHeadline5 { header() }
    }

    CardContent(attrsScope = {
        classes("mdc-typography--body2")
        style { color(MaterialTheme.Var.textPrimary) }
        contentAttrsScope?.invoke(this)
    }) {
        content?.invoke()
    }
}

@Composable
fun OutlinedCard(
    header: String,
    rawHtml: String,
) = MaterialOutlinedCard({
    style {
        property("border-color", MaterialTheme.Var.divider)
    }
}) {
    CardHeader(attrsScope = {
        style { color(MaterialTheme.Var.textPrimary) }
    }) {
        TypographyHeadline5 { Text(header) }
    }

    RawHtmlCardContent(rawHtml)
}

@Composable
fun RawHtml(
    rawHtml: String,
    tag: String = "div",
) {
    val element = remember { mutableStateOf<HTMLElement?>(null) }

    LaunchedEffect(rawHtml) {
        element.value?.innerHTML = rawHtml
    }

    TagElement(
        tag,
        applyAttrs = {
            ref {
                element.value = it
                onDispose { element.value = null }
            }
        },
        content = null,
    )
}

@Composable
private fun RawHtmlCardContent(
    rawHtml: String,
) {
    val element = remember { mutableStateOf<HTMLElement?>(null) }

    LaunchedEffect(rawHtml) {
        element.value?.innerHTML = rawHtml
    }

    CardContent(attrsScope = {
        classes("mdc-typography--body2")
        style { color(MaterialTheme.Var.textPrimary) }
        ref {
            element.value = it
            onDispose { element.value = null }
        }
    }) { }
}
