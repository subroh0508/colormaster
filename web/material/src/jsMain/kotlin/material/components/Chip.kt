package material.components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.css.marginRight
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLSpanElement

@Composable
fun Chip(
    label: String,
    selected: Boolean = false,
    disabled: Boolean = false,
    leadingIcon: String? = null,
    attrsScope: (AttrsScope<HTMLSpanElement>.() -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    Span({
        classes(*chipClasses(selected, disabled))
        attrsScope?.invoke(this)
    }) {
        Span({
            classes("mdc-evolution-chip__cell", "mdc-evolution-chip__cell--primary")
        }) {
            Button({
                classes("mdc-evolution-chip__action", "mdc-evolution-chip__action--primary")
                if (disabled) disabled()
                onClick { onClick() }
            }) {
                Span({ classes("mdc-evolution-chip__ripple", "mdc-evolution-chip__ripple--primary") })

                if (leadingIcon != null) {
                    Span({
                        classes("mdc-evolution-chip__graphic")
                        style { marginRight(4.px) }
                    }) {
                        Span({ classes("mdc-evolution-chip__icon", "mdc-evolution-chip__icon--primary", "material-icons") }) {
                            Text(leadingIcon)
                        }
                    }
                }

                Span({
                    classes("mdc-evolution-chip__text-label")
                }) { Text(label) }
            }
        }
    }
}

private fun chipClasses(
    selected: Boolean,
    disabled: Boolean,
) = listOfNotNull(
    "mdc-evolution-chip",
    if (selected) "mdc-evolution-chip--activated" else null,
    if (disabled) "mdc-evolution-chip--disabled" else null,
).toTypedArray()
