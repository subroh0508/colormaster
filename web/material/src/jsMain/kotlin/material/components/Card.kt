package material.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div

@Composable
fun Card(content: @Composable () -> Unit) {
    Div({ classes("mdc-card") }) {
        content()
    }
}

@Composable
fun OutlinedCard(content: @Composable () -> Unit) {
    Div({ classes("mdc-card", "mdc-card--outlined") }) {
        content()
    }
}

@Composable
fun CardHeader(content: @Composable () -> Unit) {
    Div({
        classes("mdc-card__header")
        style { padding(16.px) }
    }) {
        content()
    }
}
@Composable
fun CardContent(content: @Composable () -> Unit) {
    Div({
        classes("mdc-card__content")
        style { padding(16.px) }
    }) {
        content()
    }
}
