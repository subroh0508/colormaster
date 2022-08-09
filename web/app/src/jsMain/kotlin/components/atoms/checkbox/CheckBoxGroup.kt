package components.atoms.checkbox

import androidx.compose.runtime.Composable
import material.components.Checkbox
import org.jetbrains.compose.web.attributes.AttrsScope
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement

@Composable
fun <T> CheckBoxGroup(
    items: List<Pair<String, T>>,
    selections: List<T>,
    attrsScope: (AttrsScope<HTMLDivElement>.() -> Unit)? = null,
    onClick: (T, Boolean) -> Unit,
) = Div({
    style {
        display(DisplayStyle.Flex)
        flexWrap(FlexWrap.Wrap)
    }
    attrsScope?.invoke(this)
}) {
    items.forEach{ (id, item) ->
        val checked = selections.contains(item)

        Checkbox(
            item.toString(),
            checked,
            id,
            { style { marginRight(16.px) } },
        ) { onClick(item, !checked) }
    }
}
