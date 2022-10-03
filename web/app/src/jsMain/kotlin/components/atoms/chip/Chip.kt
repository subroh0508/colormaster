package components.atoms.chip

import MaterialTheme
import androidx.compose.runtime.Composable
import material.components.Chip
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLSpanElement

@Composable
fun OutlinedChip(
    label: String,
    disabled: Boolean = false,
    leadingIcon: String? = null,
    attrsScope: (AttrsScope<HTMLSpanElement>.() -> Unit)? = null,
    onClick: () -> Unit = {},
) {
    Style(OutlinedChipStyle)

    Chip(
        label,
        disabled = disabled,
        leadingIcon = leadingIcon,
        attrsScope = {
            classes(OutlinedChipStyle.content)
            attrsScope?.invoke(this)
        },
        onClick = onClick,
    )
}

private object OutlinedChipStyle : StyleSheet() {
    val content by style {
        backgroundColor(Color.transparent)
        border(
            1.px,
            LineStyle.Solid,
            MaterialTheme.Var.divider,
        )

        desc(self, type("button")) style  {
            desc(self, className("mdc-evolution-chip__text-label")) style  {
                color(MaterialTheme.Var.onSurface)
            }

            desc(self, className("material-icons")) style  {
                color(MaterialTheme.Var.onSurface)
            }
        }

        (desc(self, type("button")) + disabled) style  {
            desc(self, className("mdc-evolution-chip__text-label")) style {
                color(MaterialTheme.Var.textDisabled)
            }

            desc(self, className("material-icons")) style  {
                color(MaterialTheme.Var.textDisabled)
            }
        }
    }
}
