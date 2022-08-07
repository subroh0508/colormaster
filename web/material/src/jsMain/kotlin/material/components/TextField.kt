package material.components

import androidx.compose.runtime.*
import material.externals.MDCTextField
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.events.SyntheticChangeEvent
import org.jetbrains.compose.web.events.SyntheticInputEvent
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLLabelElement
import org.w3c.dom.HTMLTextAreaElement

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
    attrs: (AttrsScope<HTMLLabelElement>.() -> Unit)? = null,
    onChange: (SyntheticInputEvent<String, HTMLInputElement>) -> Unit,
) {
    val textField = mutableStateOf<MDCTextField?>(null)

    TextFieldLabel(
        TextFieldVariant.Outlined,
        textField,
        attrs = attrs,
    ) {
        NotchedOutline(id, label)
        TextFieldInput(id, value, onChange)
    }
}

@Composable
fun OutlinedTextArea(
    label: String,
    value: String? = null,
    id: String = LABEL_ID,
    attrs: (AttrsScope<HTMLLabelElement>.() -> Unit)? = null,
    onChange: (SyntheticInputEvent<String, HTMLTextAreaElement>) -> Unit,
) {
    val textField = mutableStateOf<MDCTextField?>(null)

    TextFieldLabel(
        TextFieldVariant.Outlined,
        textField,
        textarea = true,
        attrs = attrs,
    ) {
        NotchedOutline(id, label)
        ResizerTextArea(id, label, value, onChange)
    }
}

@Composable
private fun TextFieldLabel(
    variant: String,
    state: MutableState<MDCTextField?>,
    textarea: Boolean = false,
    attrs: (AttrsScope<HTMLLabelElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Label(attrs = {
    classes(*labelClasses(variant, textarea))
    attrs?.invoke(this)
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
    onChange: (SyntheticInputEvent<String, HTMLInputElement>) -> Unit,
) = Input(InputType.Text) {
    classes("mdc-text-field__input")
    attr("aria-labelledby", id)
    value?.let { value(it) }
    onInput(onChange)
}

@Composable
private fun ResizerTextArea(
    id: String,
    label: String,
    value: String?,
    onChange: (SyntheticInputEvent<String, HTMLTextAreaElement>) -> Unit,
) = Span({ style { width(100.percent) } }) {
    TextAreaAutoSize(label, value, onChange) {
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