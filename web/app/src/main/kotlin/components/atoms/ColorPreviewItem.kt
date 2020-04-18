package components.atoms

import kotlinext.js.jsObject
import kotlinx.css.*
import materialui.styles.makeStyles
import net.subroh0508.colormaster.model.IdolColor
import react.*
import react.dom.br
import react.dom.div

const val COLOR_PREVIEW_ITEM_CLASS_NAME = "color-preview-item"

fun RBuilder.colorPreview(props: ColorPreviewProps) = child(ColorPreviewComponent, props)

fun RBuilder.colorPreviewItem(
    props: ColorPreviewItemProps = jsObject {},
    handler: RHandler<ColorPreviewItemProps> = {}
) = child(ColorPreviewItemComponent, props, handler)

fun IdolColor.toPreviewItemProp(isColorOnly: Boolean = false, count: Int = 1): ColorPreviewItemProps = jsObject {
    name = this@toPreviewItemProp.name
    color = this@toPreviewItemProp.color
    isBrighter = this@toPreviewItemProp.isBrighter
    this.isColorOnly = isColorOnly
    this.count = count
}

private val ColorPreviewComponent = functionalComponent<ColorPreviewProps> { props ->
    props.items.forEach { item -> colorPreviewItem(item.toPreviewItemProp(props.isColorOnly, props.items.size)) }
}

private val ColorPreviewItemComponent = memo(functionalComponent<ColorPreviewItemProps> { props ->
    val classes = useStyles(props)

    div("$COLOR_PREVIEW_ITEM_CLASS_NAME ${classes.root}") {
        +props.name
        br { }
        +props.color
    }
})

external interface ColorPreviewProps : RProps {
    var items: List<IdolColor>
    var isColorOnly: Boolean
}

external interface ColorPreviewItemProps : RProps {
    var name: String
    var color: String
    var isBrighter: Boolean
    var isColorOnly: Boolean?
    var count: Int?
}

external interface ColorPreviewItemStyle {
    val root: String
}

private val useStyles = makeStyles<ColorPreviewItemStyle, ColorPreviewItemProps> {
    "root" { props ->
        val textColor = when {
            props.isColorOnly == true -> Color.transparent
            props.isBrighter -> Color.black
            !props.isBrighter -> Color.white
            else -> Color.transparent
        }

        display = Display.flex
        width = 100.pct
        height = 100.pct / (props.count ?: 1)
        backgroundColor = Color(props.color)
        color = textColor
        textAlign = TextAlign.center
        fontWeight = FontWeight.w700
        alignItems = Align.center
        justifyContent = JustifyContent.center
    }
}
