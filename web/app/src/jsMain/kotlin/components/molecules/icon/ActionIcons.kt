package components.molecules.icon

import androidx.compose.runtime.Composable
import androidx.compose.web.events.SyntheticMouseEvent
import material.components.Icon
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

@Composable
fun ActionIcons(
    attrsScope: (AttrsScope<HTMLDivElement>.() -> Unit)?,
    vararg icon: Pair<String, (SyntheticMouseEvent) -> Unit>,
) {
    Style(ActionIconsStyle)

    Div({
        classes(ActionIconsStyle.content)
        attrsScope?.invoke(this)
    }) { icon.forEach { (name, onClick) -> Icon(name) { onClick(onClick) } } }
}

private object ActionIconsStyle : StyleSheet() {
    val content by style {
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.FlexEnd)

        (className("material-icons") + not(lastChild)) style {
            marginRight(4.px)
        }
    }
}
