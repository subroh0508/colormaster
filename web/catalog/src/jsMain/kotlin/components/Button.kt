package components

import androidx.compose.runtime.*
import androidx.compose.web.events.SyntheticMouseEvent
import externals.attachRippleTo
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

@Composable
fun OutlinedButton(
    label: String,
    icon: String? = null,
    onClick: (SyntheticMouseEvent) -> Unit = {},
) {
    val element = rememberRippleElement()

    Button({
        classes(*buttonClasses(icon != null))
        ref {
            element.value = it
            onDispose { element.value = null }
        }
        onClick(onClick)
    }) {
        Span({ classes("mdc-button__ripple") })
        Span({ classes("mdc-button__focus-ring") })
        ButtonIcon(icon)
        Span({ classes("mdc-button__label") }) { Text(label) }
    }
}

private fun buttonClasses(hasIcon: Boolean) = listOfNotNull(
    "mdc-button",
    "mdc-button--outlined",
    if (hasIcon) "mdc-button--icon-leading" else null,
).toTypedArray()

@Composable
private fun ButtonIcon(icon: String?) {
    icon ?: return

    I({
        classes("material-icons", "mdc-button__icon")
        attr("aria-hidden", "true")
    }) { Text(icon) }
}
