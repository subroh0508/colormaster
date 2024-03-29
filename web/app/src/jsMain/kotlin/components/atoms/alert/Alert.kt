package components.atoms.alert

import MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import material.components.Icon
import material.components.TypographyBody2
import material.utilities.MEDIA_QUERY_TABLET_SMALL
import material.utilities.rememberMediaQuery
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

enum class AlertType { Info, Success, Warning, Error }

@Composable
fun Alert(
    type: AlertType,
    message: String,
) {
    val wide by rememberMediaQuery(MEDIA_QUERY_TABLET_SMALL)

    Div({
        style {
            display(DisplayStyle.Flex)
            alignItems(AlignItems.Center)
            padding(8.px, 16.px)
            if (wide)
                borderRadius(16.px, 0.px, 0.px, 16.px)
            else
                borderRadius(16.px)
            backgroundColor(type.backgroundColor)
        }
    }) {
        Icon(type.icon, applyAttrs = {
            style {
                width(1.5.cssRem)
                height(1.5.cssRem)
                color(type.color)
                marginRight(12.px)
            }
        })

        TypographyBody2(
            tag = "div",
            {
                style {
                    padding(8.px, 0.px)
                    color(type.textColor)
                }
            },
        ) { Text(message) }
    }
}

private val AlertType.icon get() = when (this) {
    AlertType.Info -> "info_outlined"
    AlertType.Success -> "check_circle_outlined"
    AlertType.Warning -> "report_problem_outlined"
    AlertType.Error -> "error_outlined"
}

private val AlertType.color get()  = when (this) {
    AlertType.Info -> MaterialTheme.Var.info
    AlertType.Success -> MaterialTheme.Var.success
    AlertType.Warning -> MaterialTheme.Var.warning
    AlertType.Error -> MaterialTheme.Var.error
}

private val AlertType.backgroundColor get()  = when (this) {
    AlertType.Info -> MaterialTheme.Var.backgroundInfo
    AlertType.Success -> MaterialTheme.Var.backgroundSuccess
    AlertType.Warning -> MaterialTheme.Var.backgroundWarning
    AlertType.Error -> MaterialTheme.Var.backgroundError
}

private val AlertType.textColor get()  = when (this) {
    AlertType.Info -> MaterialTheme.Var.textInfo
    AlertType.Success -> MaterialTheme.Var.textSuccess
    AlertType.Warning -> MaterialTheme.Var.textWarning
    AlertType.Error -> MaterialTheme.Var.textError
}
