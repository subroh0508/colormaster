package components.atoms.textfield

import androidx.compose.runtime.Composable
import material.components.OutlinedTextArea
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.events.SyntheticInputEvent
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLTextAreaElement

@Composable
fun OutlinedTextField(
    id: String,
    label: String,
    value: String?,
    attrs: (AttrsScope<HTMLDivElement>.() -> Unit)? = null,
    onChange: (SyntheticInputEvent<String, HTMLTextAreaElement>) -> Unit,
) = Div(attrs) {
    OutlinedTextArea(
        label,
        value,
        id,
        { style { width(100.percent) } },
        onChange,
    )
}
