package components

import androidx.compose.runtime.*
import androidx.compose.web.events.SyntheticMouseEvent
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun OutlinedButton(
    label: String,
    icon: String? = null,
    onClick: (SyntheticMouseEvent) -> Unit = {},
) {
    val element = rememberRippleElement()

    Button({
        classes(*outlinedButtonClasses(icon != null))
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

@Composable
fun ButtonGroup(
    content: @Composable () -> Unit,
) {
    Style(ButtonGroupStyle)

    Div({
        classes(ButtonGroupStyle.group)
        attr("role", "group")
    }) { content() }
}

private fun outlinedButtonClasses(hasIcon: Boolean) = listOfNotNull(
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

private object ButtonGroupStyle : StyleSheet() {
    val group by style {
        display(DisplayStyle.LegacyInlineFlex)

        (type("button") + not(firstChild)) style {
            property("border-top-left-radius", "0")
            property("border-bottom-left-radius", "0")

            marginLeft((-1).px)
        }

        (type("button") + not(lastChild)) style {
            property("border-right-color", "transparent")
            property("border-top-right-radius", "0")
            property("border-bottom-right-radius", "0")
        }
    }
}
