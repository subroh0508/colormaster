package components.molecules

import components.atoms.ColorPreviewItemProps
import components.atoms.colorPreviewItem
import kotlinext.js.jsObject
import net.subroh0508.colormaster.model.IdolColor
import react.RBuilder
import react.RProps
import react.child
import react.functionalComponent

fun RBuilder.colorPreview(props: ColorPreviewProps) = child(ColorPreviewComponent, props)

private val ColorPreviewComponent = functionalComponent<ColorPreviewProps> { props ->
    props.items.toItemProps(props.isColorOnly).forEach { itemProp -> colorPreviewItem(itemProp) }
}

private fun List<IdolColor>.toItemProps(colorOnly: Boolean = false): List<ColorPreviewItemProps> = map {
    jsObject {
        name = it.name
        color = it.color
        isBrighter = it.isBrighter
        isColorOnly = isColorOnly
        count = size
    }
}

external interface ColorPreviewProps : RProps {
    var items: List<IdolColor>
    var isColorOnly: Boolean
}
