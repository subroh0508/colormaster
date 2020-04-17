package components.atoms

import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.js.*
import materialui.components.icon.icon
import materialui.components.paper.paper
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import react.*
import react.dom.br
import styled.animation
import utilities.isMobile

fun RBuilder.colorItem(handler: RHandler<ColorItemProps>) = child(ColorItemComponent, handler = handler)

private val ColorItemComponent = memo(functionalComponent<ColorItemProps> { props ->
    val classes = useStyles(props)
    val (mouse, setMouseEvent) = useState(Mouse.NONE)

    console.log(props.name, mouse.name, props.isSelected)
    paper {
        attrs {
            classes(rootStyle(classes, mouse, props.isSelected))

            onClickFunction = { setMouseEvent(Mouse.CLICK) }
            onMouseOverFunction = { console.log("over"); setMouseEvent(Mouse.OVER) }
            onMouseOutFunction = { console.log("out"); setMouseEvent(Mouse.OUT) }
        }

        if (props.isSelected) {
            icon {
                attrs.classes(classes.checkIcon)
                +"check_circle_outline_icon"
            }
        }

        +props.name
        br { }
        +props.color
    }
})

external interface ColorItemProps : RProps {
    var id: String
    var name: String
    var color: String
    var isBrighter: Boolean
    var isSelected: Boolean
}

private external interface ColorItemStyle {
    val root: String
    val small: String
    val mouseOver: String
    val mouseOut: String
    val checkIcon: String
}

private enum class Mouse {
    NONE, OVER, OUT, CLICK
}

private val useStyles = makeStyles<ColorItemStyle, ColorItemProps> {
    "root" { props ->
        display = Display.flex
        width = 100.pct
        height = 50.px
        backgroundColor = Color(props.color)
        color = if (props.isBrighter) Color.black else Color.white
        textAlign = TextAlign.center
        fontWeight = FontWeight.w700
        alignItems = Align.center
        justifyContent = JustifyContent.center

        (theme.breakpoints.up(Breakpoint.sm)) {
            width = 200.px
        }
    }
    "small" {
        transform.scale(0.9)
    }
    "mouseOver" {
        animation(0.1.s, fillMode = FillMode.forwards) {
            100 { transform.scale(0.9) }
        }
    }
    "mouseOut" {
        animation(0.1.s, fillMode = FillMode.forwards) {
            0 { transform.scale(0.9) }
            100 { transform.scale(1.0) }
        }
    }
    "checkIcon" {
        position = Position.absolute
        top = 0.px
        left = 0.px
        bottom = 0.px
        margin(LinearDimension.auto, 4.px)
    }
}

private fun rootStyle(classes: ColorItemStyle, mouse: Mouse, isSelected: Boolean): String {
    if (isMobile) {
        return when (isSelected) {
            true -> "${classes.root} ${classes.mouseOver}"
            false -> "${classes.root} ${classes.mouseOut}"
        }
    }

    if (isSelected) {
        return "${classes.root} ${classes.small}"
    }

    return when (mouse) {
        Mouse.OVER -> "${classes.root} ${classes.mouseOver}"
        Mouse.OUT -> "${classes.root} ${classes.mouseOut}"
        Mouse.CLICK -> "${classes.root} ${classes.small}"
        else -> classes.root
    }
}
