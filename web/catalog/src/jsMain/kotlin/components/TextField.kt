package components

import androidx.compose.runtime.*
import externals.MDCTextField
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.*

private object TextFieldVariant {
    const val Filled = "filled"
    const val Outlined = "outlined"
}

private const val LABEL_ID = "text-field-label"

@Composable
fun OutlinedTextField(
    label: String,
    value: String? = null,
    id: String = LABEL_ID,
) {
    val textField = mutableStateOf<MDCTextField?>(null)

    TextFieldLabel(
        TextFieldVariant.Outlined,
        textField,
    ) {
        NotchedOutline(id, label)
        TextFieldInput(id, value)
    }
}

@Composable
fun OutlinedTextArea(
    label: String,
    value: String? = null,
    id: String = LABEL_ID,
) {
    val textField = mutableStateOf<MDCTextField?>(null)

    TextFieldLabel(
        TextFieldVariant.Outlined,
        textField,
        textarea = true,
    ) {
        NotchedOutline(id, label)
        ResizerTextArea(id, label, value)
    }
}

@Composable
private fun TextFieldLabel(
    variant: String,
    state: MutableState<MDCTextField?>,
    textarea: Boolean = false,
    content: @Composable () -> Unit,
) = Label(attrs = {
    classes(*labelClasses(variant, textarea))
    ref {
        state.value = MDCTextField(it)
        onDispose { state.value = null }
    }
}) { content() }

@Composable
private fun NotchedOutline(id: String, label: String) {
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
}

@Composable
private fun TextFieldInput(
    id: String,
    value: String?,
) = Input(InputType.Text) {
    classes("mdc-text-field__input")
    attr("aria-labelledby", id)
    value?.let { value(it) }
}

@Composable
private fun ResizerTextArea(
    id: String,
    label: String,
    value: String?,
) = Span {
    TextAreaAutoSize(label, value) {
        classes("mdc-text-field__input")
        attr("aria-labelledby", id)
        attr("rows", "1")
    }
}

private fun labelClasses(variant: String, textarea: Boolean = false) = listOfNotNull(
    "mdc-text-field",
    "mdc-text-field--$variant",
    if (textarea) "mdc-text-field--textarea" else null,
).toTypedArray()