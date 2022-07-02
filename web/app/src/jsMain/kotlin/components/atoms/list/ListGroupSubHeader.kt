package components.atoms.list

import androidx.compose.runtime.Composable
import material.components.Icon
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import material.components.ListGroupSubHeader as MaterialListGroupSubHeader

@Composable
fun ListGroupSubHeader(label: String) = MaterialListGroupSubHeader(label)

@Composable
fun ListGroupSubHeader(
    id: String,
    label: String,
    helpContent: @Composable (String) -> Unit,
) = MaterialListGroupSubHeader(applyAttrs = {
    style {
        display(DisplayStyle.Flex)
    }
}) {
    Span({
        style {
            flexShrink(0)
            height(16.px)
            marginTop(12.px)
            property("vertical-align", "bottom")
        }
    }) { Text(label) }
    Icon("help_outline_icon", applyAttrs = {
        attr("aria-describedby", id)

        style {
            height(16.px)
            width(16.px)
            margin(14.px, 2.px, 0.px)
            fontSize(16.px)
        }
    })

    helpContent(id)
}
