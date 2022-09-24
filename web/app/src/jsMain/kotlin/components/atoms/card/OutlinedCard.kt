package components.atoms.card

import MaterialTheme
import androidx.compose.runtime.Composable
import material.components.CardContent
import material.components.CardHeader
import material.components.TypographyHeadline5
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.color
import org.w3c.dom.HTMLDivElement
import material.components.OutlinedCard as MaterialOutlinedCard

@Composable
fun OutlinedCard(
    header: @Composable () -> Unit,
    contentAttrsScope: ((AttrsScope<HTMLDivElement>).() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) = MaterialOutlinedCard({
    style {
        property("border-color", MaterialTheme.Var.divider)
    }
}) {
    CardHeader(attrsScope = {
        style { color(MaterialTheme.Var.textPrimary) }
    }) {
        TypographyHeadline5 { header() }
    }

    CardContent(attrsScope = {
        classes("mdc-typography--body2")
        style { color(MaterialTheme.Var.textPrimary) }
        contentAttrsScope?.invoke(this)
    }) {
        content?.invoke()
    }
}
