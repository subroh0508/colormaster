package material.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLSpanElement

@Composable
fun Chip(
    label: String,
    selected: Boolean = false,
    attrsScope: (AttrsScope<HTMLSpanElement>.() -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    Span({
        classes(*chipClasses(selected))
        attrsScope?.invoke(this)
    }) {
        Span({
            classes("mdc-evolution-chip__cell", "mdc-evolution-chip__cell--primary")
        }) {
            Button({
                classes("mdc-evolution-chip__action", "mdc-evolution-chip__action--primary")
                onClick { onClick() }
            }) {
                Span({
                    classes("mdc-evolution-chip__ripple", "mdc-evolution-chip__ripple--primary")
                })
                Span({
                    classes("mdc-evolution-chip__text-label")
                }) { Text(label) }
            }
        }
    }
}

private fun chipClasses(selected: Boolean) = listOfNotNull(
    "mdc-evolution-chip",
    if (selected) "mdc-evolution-chip--activated" else null,
).toTypedArray()
