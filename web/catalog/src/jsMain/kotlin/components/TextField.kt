package components

import androidx.compose.runtime.*
import externals.MDCTextField
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

private const val LABEL_ID = "text-field-label"

@Composable
fun OutlinedTextField(
    label: String,
    value: String? = null,
    id: String = LABEL_ID,
) {
    var element by remember { mutableStateOf<HTMLElement?>(null) }

    SideEffect {
        element?.let { MDCTextField(it) }
    }

    Label(attrs = {
        classes("mdc-text-field", "mdc-text-field--outlined")
        ref {
            element = it
            onDispose { element = null }
        }
    }) {
        Span({ classes("mdc-notched-outline") }) {
            Span({ classes("mdc-notched-outline__leading") })
            Span({ classes("mdc-notched-outline__notch") }) {
                Span({
                    id(id)
                    classes("mdc-floating-label")
                }) { Text(label) }
            }
            Span({ classes("mdc-notched-outline__trailing") })
        }
        Input(InputType.Text) {
            classes("mdc-text-field__input")
            //attr("aria-labelledby", labelId)
            value(value ?: "")
        }
    }
}
