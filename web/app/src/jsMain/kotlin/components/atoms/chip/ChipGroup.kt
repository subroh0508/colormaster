package components.atoms.chip

import androidx.compose.runtime.Composable
import material.components.Chip
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

@Composable
fun <T> ChipGroup(
    items: List<T>,
    selected: T?,
    attrsScope: (AttrsScope<HTMLDivElement>.() -> Unit)? = null,
    onClick: (T?) -> Unit,
)= Div({
    style {
        display(DisplayStyle.Flex)
        flexWrap(FlexWrap.Wrap)
    }
    attrsScope?.invoke(this)
}) {
    items.forEach {
        Chip(
            it.toString(),
            it == selected,
            { style { margin(0.px, 8.px, 8.px, 0.px) } },
        ) { onClick(if (it != selected) it else null) }
    }
}
