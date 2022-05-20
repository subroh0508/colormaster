package components

import MaterialTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun Chip(
    label: String,
    onClick: () -> Unit,
) = StyledChip(
    label,
    MaterialTheme.Var.onPrimary,
    {
        backgroundColor(MaterialTheme.Var.primary)
    },
    onClick,
)

@Composable
fun OutlinedChip(
    label: String,
    onClick: () -> Unit,
) = StyledChip(
    label,
    MaterialTheme.Var.primary,
    {
        backgroundColor(MaterialTheme.Var.background)
        color(MaterialTheme.Var.primary)
        border { color(MaterialTheme.Var.primary) }
        border {
            width(1.px)
            style(LineStyle.Solid)
        }
    },
    onClick,
)

@Composable
private fun StyledChip(
    label: String,
    color: CSSColorValue,
    style: StyleScope.() -> Unit,
    onClick: () -> Unit,
) {
    Span({
        classes("mdc-evolution-chip")
        style(style)
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
                    style { color(color) }
                }) { Text(label) }
            }
        }
    }
}
