package components.molecules

import components.atoms.COLOR_PREVIEW_ITEM_CLASS_NAME
import components.atoms.ColorPreviewItemProps
import components.atoms.colorPreviewItem
import kotlinext.js.jsObject
import kotlinx.css.Color
import kotlinx.css.color
import kotlinx.css.height
import kotlinx.css.pct
import materialui.styles.makeStyles
import net.subroh0508.colormaster.model.IdolColor
import react.RBuilder
import react.RProps
import react.child
import react.dom.div
import react.functionalComponent

fun RBuilder.colorPreview(props: ColorPreviewProps) = child(ColorPreviewComponent, props)

private val ColorPreviewComponent = functionalComponent<ColorPreviewProps> { props ->
    val classes = useStyle(props)
    val rootStyle = "${classes.root} ${if (props.isColorOnly) classes.text else ""}"

    div(rootStyle) {
        props.items.toItemProps().forEach { itemProp -> colorPreviewItem(itemProp) }
    }
}

private fun List<IdolColor>.toItemProps(): List<ColorPreviewItemProps> = map {
    jsObject<ColorPreviewItemProps> {
        name = it.name
        color = it.color
        isBrighter = it.isBrighter
    }
}

external interface ColorPreviewProps : RProps {
    var items: List<IdolColor>
    var isColorOnly: Boolean
}

private external interface ColorPreviewStyle {
    val root: String
    val text: String
}

private val useStyle = makeStyles<ColorPreviewStyle, ColorPreviewProps> {
    "root" { props ->
        height = 100.pct

        descendants(".$COLOR_PREVIEW_ITEM_CLASS_NAME") {
            height = 100.pct / props.items.size
        }
    }
    "text" {
        descendants(".$COLOR_PREVIEW_ITEM_CLASS_NAME") {
            color = Color.transparent
        }
    }
}
