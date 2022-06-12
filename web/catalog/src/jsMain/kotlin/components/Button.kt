package components

import androidx.compose.runtime.*
import androidx.compose.web.events.SyntheticMouseEvent
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

private object ButtonVariant {
    const val Outlined = "outlined"
    const val Contained = "raised"
}

@Composable
fun TextButton(
    label: String,
    onClick: (SyntheticMouseEvent) -> Unit = {},
    leadingIcon: String? = null,
    trailingIcon: String? = null,
) = MaterialButton(
    null,
    onClick,
    leadingIcon,
    trailingIcon,
) { Text(label) }

@Composable
fun OutlinedButton(
    label: String,
    onClick: (SyntheticMouseEvent) -> Unit = {},
    leadingIcon: String? = null,
    trailingIcon: String? = null,
) = MaterialButton(
    ButtonVariant.Outlined,
    onClick,
    leadingIcon,
    trailingIcon
) { Text(label) }

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

@Composable
private fun MaterialButton(
    variant: String? = null,
    onClick: (SyntheticMouseEvent) -> Unit = {},
    leadingIcon: String? = null,
    trailingIcon: String? = null,
    label: @Composable () -> Unit,
) {
    val element = rememberRippleElement()

    Button({
        classes(*buttonClasses(variant, leadingIcon != null, trailingIcon != null))
        ref {
            element.value = it
            onDispose { element.value = null }
        }
        onClick(onClick)
    }) {
        Span({ classes("mdc-button__ripple") })
        Span({ classes("mdc-button__focus-ring") })
        leadingIcon?.let { ButtonIcon(it) }
        Span({ classes("mdc-button__label") }) { label() }
        trailingIcon?.let { ButtonIcon(it) }
    }
}

private fun buttonClasses(
    variant: String?,
    hasLeadingIcon: Boolean,
    hasTrailingIcon: Boolean,
) = listOfNotNull(
    "mdc-button",
    variant?.let { "mdc-button--$it" },
    if (hasLeadingIcon) "mdc-button--icon-leading" else null,
    if (hasTrailingIcon) "mdc-button--icon-trailing" else null,
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
