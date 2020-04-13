package components.atoms

import kotlinext.js.js
import kotlinext.js.jsObject
import kotlinx.css.*
import kotlinx.html.js.onDoubleClickFunction
import materialui.components.paper.paper
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import org.w3c.dom.events.Event
import react.*
import react.dom.br

fun RBuilder.colorItem(handler: RHandler<ColorItemProps>) = child(ColorItemComponent, handler = handler)

private val ColorItemComponent = functionalComponent<ColorItemProps> { props ->
    val classes = useStyles()

    paper {
        attrs {
            classes(classes.root)
            setProp("style", js {
                this["backgroundColor"] = props.color
                this["color"] = if (props.isBrighter) Color.black.toString() else Color.white.toString()
            } as Any)
            onDoubleClickFunction = props.onDoubleClick
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
    var onDoubleClick: (event: Event) -> Unit
}

private external interface ColorItemStyle {
    val root: String
}

private val useStyles = makeStyles<ColorItemStyle> {
    "root" {
        width = 100.pct
        height = 50.px
        margin(4.px)
        textAlign = TextAlign.center
        fontWeight = FontWeight.w700

        (theme.breakpoints.up(Breakpoint.sm)) {
            width = 200.px
        }
    }
}
