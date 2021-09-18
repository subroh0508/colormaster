package components.atoms

import kotlinext.js.jsObject
import kotlinx.css.*
import materialui.styles.makeStyles
import react.*
import react.dom.br
import react.dom.div

const val COLOR_PREVIEW_ITEM_CLASS_NAME = "color-preview-item"

fun RBuilder.colorPreviewItem(
    props: ColorPreviewItemProps = jsObject {},
    handler: RHandler<ColorPreviewItemProps> = {}
) = child(ColorPreviewItemComponent, props, handler)

private val ColorPreviewItemComponent = memo(functionComponent<ColorPreviewItemProps> { props ->
    val classes = useStyles(props)

    div("$COLOR_PREVIEW_ITEM_CLASS_NAME ${classes.root}") {
        +props.name
        br { }
        +props.color
    }
})

external interface ColorPreviewItemProps : PropsWithChildren {
    var name: String
    var color: String
    var isBrighter: Boolean
}

private external interface ColorPreviewItemStyle {
    val root: String
}

private val useStyles = makeStyles<ColorPreviewItemStyle, ColorPreviewItemProps> {
    dynamic("root") { props ->
        display = Display.flex
        width = 100.pct
        height = 100.pct
        color = if (props.isBrighter) Color.black else Color.white
        backgroundColor = Color(props.color)
        textAlign = TextAlign.center
        fontWeight = FontWeight.w700
        alignItems = Align.center
        justifyContent = JustifyContent.center
    }
}
