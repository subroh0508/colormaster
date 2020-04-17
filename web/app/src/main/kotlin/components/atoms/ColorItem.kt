package components.atoms

import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onDoubleClickFunction
import kotlinx.html.js.onMouseOutFunction
import kotlinx.html.js.onMouseOverFunction
import materialui.components.icon.icon
import materialui.components.paper.paper
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import materialui.styles.palette.default
import org.w3c.dom.events.Event
import react.*
import react.dom.br
import styled.animation

fun RBuilder.colorItem(handler: RHandler<ColorItemProps>) = child(ColorItemComponent, handler = handler)

private val ColorItemComponent = functionalComponent<ColorItemProps> { props ->
    val classes = useStyles(props)
    val (mouse, setMouseEvent) = useState(Mouse.NONE)

    fun rootStyle(mouse: Mouse, isSelected: Boolean) = when {
        isSelected -> "${classes.root} ${classes.small}"
        mouse == Mouse.OVER -> "${classes.root} ${classes.mouseOver}"
        mouse == Mouse.OUT -> "${classes.root} ${classes.mouseOut}"
        mouse == Mouse.CLICK -> "${classes.root} ${classes.small}"
        else -> classes.root
    }

    paper {
        attrs {
            classes(rootStyle(mouse, props.isSelected))
            onClickFunction = {
                setMouseEvent(Mouse.CLICK)
                props.onClick(it)
            }
            onMouseOverFunction = { setMouseEvent(Mouse.OVER) }
            onMouseOutFunction = { setMouseEvent(Mouse.OUT) }
            //onDoubleClickFunction = props.onDoubleClick
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
}

external interface ColorItemProps : RProps {
    var name: String
    var color: String
    var isBrighter: Boolean
    var isSelected: Boolean
    var onClick: (event: Event) -> Unit
    var onDoubleClick: (event: Event) -> Unit
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
        position = Position.relative
        width = 100.pct
        height = 50.px
        backgroundColor = Color(props.color)
        color = if (props.isBrighter) Color.black else Color.white
        margin(4.px)
        textAlign = TextAlign.center
        fontWeight = FontWeight.w700

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
        width = 24.px
        height = 24.px
        borderRadius = 50.pct
    }
}
