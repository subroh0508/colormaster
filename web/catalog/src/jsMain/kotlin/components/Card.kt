package components

import androidx.compose.runtime.Composable
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
