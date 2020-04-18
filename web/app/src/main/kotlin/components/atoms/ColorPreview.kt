package components.atoms

import kotlinext.js.jsObject
import kotlinx.css.*
import materialui.styles.makeStyles
import net.subroh0508.colormaster.model.IdolColor
import react.*
import react.dom.br
import react.dom.div

fun RBuilder.colorPreview(props: ColorPreviewProps) = child(ColorPreviewComponent, props)

private val ColorPreviewComponent = functionalComponent<ColorPreviewProps> { props ->
    props.items.toItemProps(props.isColorOnly).forEach { itemProp -> child(ColorPreviewItemComponent, itemProp) }
}

private fun List<IdolColor>.toItemProps(colorOnly: Boolean): List<ColorPreviewItemProps> = map { item ->
    jsObject<ColorPreviewItemProps> {
        name = item.name
        color = item.color
        isBrighter = item.isBrighter
        isColorOnly = colorOnly
        count = size
    }
}

external interface ColorPreviewProps : RProps {
    var items: List<IdolColor>
    var isColorOnly: Boolean
}

private val ColorPreviewItemComponent = functionalComponent<ColorPreviewItemProps> { props ->
    val classes = useStyles(props)

    div(classes.root) {
        +props.name
        br { }
        +props.color
    }
}

private external interface ColorPreviewItemProps : RProps {
    var name: String
    var color: String
    var isBrighter: Boolean
    var isColorOnly: Boolean
    var count: Int
}

private external interface ColorPreviewItemStyle {
    val root: String
}

private val useStyles = makeStyles<ColorPreviewItemStyle, ColorPreviewItemProps> {
    "root" { props ->
        val textColor = when {
            props.isColorOnly -> Color.transparent
            props.isBrighter -> Color.black
            !props.isBrighter -> Color.white
            else -> Color.transparent
        }

        display = Display.flex
        width = 100.pct
        height = 100.pct / props.count
        backgroundColor = Color(props.color)
        color = textColor
        textAlign = TextAlign.center
        fontWeight = FontWeight.w700
        alignItems = Align.center
        justifyContent = JustifyContent.center
    }
}
