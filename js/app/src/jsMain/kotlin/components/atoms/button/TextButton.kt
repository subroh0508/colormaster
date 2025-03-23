package components.atoms.button

import MaterialTheme
import androidx.compose.runtime.Composable
import material.components.rememberRippleElement
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Span
import org.w3c.dom.HTMLButtonElement

@Composable
fun TextButton(
    applyAttrs: (AttrsScope<HTMLButtonElement>.() -> Unit)? = null,
    styleSheet: TextButtonStyle = DefaultTextButtonStyle,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    label: (@Composable () -> Unit)? = null,
) {
    val element = rememberRippleElement()

    Style(styleSheet)

    Button({
        classes("mdc-button", "mdc-button--icon-leading", styleSheet.button)
        applyAttrs?.invoke(this)

        ref {
            element.value = it
            onDispose { element.value = null }
        }
    }) {
        Span({ classes("mdc-button__ripple") })
        Span({ classes("mdc-button__focus-ring") })
        leadingIcon?.let {
            I({ classes("mdc-button__icon", "material-icons") }) { it() }
        }
        label?.let {
            Span({ classes("mdc-button__label") }) { it.invoke() }
        }
        trailingIcon?.let {
            I({ classes("mdc-button__icon", "material-icons") }) { it() }
        }
    }
}

abstract class TextButtonStyle : StyleSheet() {
    abstract val button: String
}

private object DefaultTextButtonStyle : TextButtonStyle() {
    override val button by style {
        className("mdc-button__label") style {
            color(MaterialTheme.Var.onSurface)
        }

        (className("mdc-button__ripple") + before) style {
            backgroundColor(MaterialTheme.Var.ripple)
        }
        (className("mdc-button__ripple") + after) style {
            backgroundColor(MaterialTheme.Var.ripple)
        }
    }
}
