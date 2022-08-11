package material.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

@Composable
fun Card(
    attrsScope: ((AttrsScope<HTMLDivElement>).() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Div({
        classes("mdc-card")
        attrsScope?.invoke(this)
    }) {
        content()
    }
}

@Composable
fun OutlinedCard(
    attrsScope: ((AttrsScope<HTMLDivElement>).() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Div({
        classes("mdc-card", "mdc-card--outlined")
        attrsScope?.invoke(this)
    }) {
        content()
    }
}

@Composable
fun CardHeader(
    attrsScope: ((AttrsScope<HTMLDivElement>).() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Div({
        classes("mdc-card__header")
        style { padding(16.px) }
        attrsScope?.invoke(this)
    }) {
        content()
    }
}
@Composable
fun CardContent(
    attrsScope: ((AttrsScope<HTMLDivElement>).() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Div({
        classes("mdc-card__content")
        style { padding(16.px) }
        attrsScope?.invoke(this)
    }) {
        content()
    }
}
