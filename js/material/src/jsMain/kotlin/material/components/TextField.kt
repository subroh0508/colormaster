package material.components

import androidx.compose.runtime.*
import androidx.compose.web.events.SyntheticMouseEvent
import material.externals.MDCTextField
import material.externals.MDCTextFieldIcon
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.events.SyntheticChangeEvent
import org.jetbrains.compose.web.events.SyntheticInputEvent
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLLabelElement
import org.w3c.dom.HTMLTextAreaElement

private object TextFieldVariant {
    const val Filled = "filled"
    const val Outlined = "outlined"
}

private object TextFieldIconPlace {
    const val Leading = "leading"
    const val Trailing = "trailing"
}

private const val LABEL_ID = "text-field-label"

@Composable
fun OutlinedTextField(
    label: String,
    value: String? = null,
    id: String = LABEL_ID,
    attrs: (AttrsScope<HTMLLabelElement>.() -> Unit)? = null,
    onChange: (SyntheticInputEvent<String, HTMLInputElement>) -> Unit,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val textField = mutableStateOf<MDCTextField?>(null)

    TextFieldLabel(
        TextFieldVariant.Outlined,
        textField,
        attrs = attrs,
    ) {
        NotchedOutline(id, label)
        leading?.invoke()
        TextFieldInput(id, value, onChange)
        trailing?.invoke()
    }
}

@Composable
fun OutlinedTextArea(
    label: String,
    value: String? = null,
    id: String = LABEL_ID,
    attrs: (AttrsScope<HTMLLabelElement>.() -> Unit)? = null,
    disabled: Boolean = false,
    onChange: (SyntheticInputEvent<String, HTMLTextAreaElement>) -> Unit,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    val textField = mutableStateOf<MDCTextField?>(null)

    TextFieldLabel(
        TextFieldVariant.Outlined,
        textField,
        textarea = true,
        disabled = disabled,
        attrs = attrs,
    ) {
        NotchedOutline(id, label)
        ResizerTextArea(id, label, value, onChange, leading, trailing)
    }
}

@Composable
fun LeadingTextFieldIcon(
    icon: String,
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    onClick: (SyntheticMouseEvent) -> Unit = {},
) = TextFieldIcon(icon, TextFieldIconPlace.Leading, applyAttrs, onClick)

@Composable
fun TrailingTextFieldIcon(
    icon: String,
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    onClick: (SyntheticMouseEvent) -> Unit = {},
) = TextFieldIcon(icon, TextFieldIconPlace.Trailing, applyAttrs, onClick)

@Composable
private fun TextFieldLabel(
    variant: String,
    state: MutableState<MDCTextField?>,
    textarea: Boolean = false,
    disabled: Boolean = false,
    attrs: (AttrsScope<HTMLLabelElement>.() -> Unit)? = null,
    content: @Composable () -> Unit,
) = Label(attrs = {
    classes(*labelClasses(variant, textarea, disabled))
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
    leading: (@Composable () -> Unit)?,
    trailing: (@Composable () -> Unit)?,
) = Span({
    style {
        display(DisplayStyle.Flex)
        width(100.percent)
    }
}) {
    leading?.invoke()
    TextAreaAutoSize(
        label,
        value,
        hasLeading = leading != null,
        hasTrailing = trailing != null,
        onChange = onChange,
    ) {
        classes("mdc-text-field__input")
        attr("aria-labelledby", id)
        attr("rows", "1")
    }
    trailing?.invoke()
}

@Composable
private fun TextFieldIcon(
    icon: String,
    place: String,
    applyAttrs: (AttrsScope<HTMLElement>.() -> Unit)? = null,
    onClick: (SyntheticMouseEvent) -> Unit = {},
) {
    val state = mutableStateOf<MDCTextFieldIcon?>(null)

    I({
        classes("material-icons", "mdc-text-field__icon", "mdc-text-field__icon--$place")
        tabIndex(0)
        attr("role", "button")
        applyAttrs?.invoke(this)
        onClick(onClick)

        ref {
            state.value = MDCTextFieldIcon(it)
            onDispose { state.value = null }
        }
    }) { Text(icon) }
}

private fun labelClasses(
    variant: String,
    textarea: Boolean = false,
    disabled: Boolean = false,
) = listOfNotNull(
    "mdc-text-field",
    "mdc-text-field--$variant",
    if (textarea) "mdc-text-field--textarea" else null,
    if (disabled) "mdc-text-field--disabled" else null,
).toTypedArray()