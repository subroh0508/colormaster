package components.atoms

import kotlinx.css.*
import kotlinx.css.properties.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onDoubleClickFunction
import materialui.components.paper.paper
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import materialui.styles.palette.default
import org.w3c.dom.events.Event
import react.*
import react.dom.br

fun RBuilder.colorItem(handler: RHandler<ColorItemProps>) = child(ColorItemComponent, handler = handler)

private val ColorItemComponent = functionalComponent<ColorItemProps> { props ->
    val classes = useStyles(props)
    val rootStyle = "${classes.root} ${if (props.isSelected) classes.selected else classes.unselected}"

    paper {
        attrs {
            classes(rootStyle)
            onClickFunction = props.onClick
            //onDoubleClickFunction = props.onDoubleClick
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
    val selected: String
    val unselected: String
}

private val useStyles = makeStyles<ColorItemStyle, ColorItemProps> {
    "root" { props ->
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

    "selected" {
        border(4.px, BorderStyle.solid, Color("#005cbf").withAlpha(0.5), theme.shape.borderRadius.px)
        boxShadow = BoxShadows.none
    }

    "unselected" {
        hover {
            border(4.px, BorderStyle.solid, theme.palette.background.default)
            boxShadow = BoxShadows.none
        }
    }
}
