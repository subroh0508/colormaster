package components.molecules.card

import androidx.compose.runtime.Composable
import androidx.compose.web.events.SyntheticMouseEvent
import material.components.Card
import material.components.Icon
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.w3c.dom.HTMLDivElement

@Composable
fun ListItemCard(
    selected: Boolean,
    attrsScope: (AttrsScope<HTMLDivElement>.() -> Unit)? = null,
    onClick: (SyntheticMouseEvent) -> Unit = {},
    onDoubleClick: (SyntheticMouseEvent) -> Unit = {},
    content: @Composable () -> Unit,
) = Card({
    classes("mdc-typography--body2")
    style {
        borderRadius(16.px)
        justifyContent(JustifyContent.Center)
        textAlign("center")
        fontWeight("bold")
    }
    onClick(onClick)
    onDoubleClick(onDoubleClick)
    attrsScope?.invoke(this)
}) {
    content()

    if (selected) {
        Icon("check_circle_outline") {
            style {
                position(Position.Absolute)
                left(0.px)
                margin(0.px, 4.px)
            }
        }
    }
}
