package components.atoms

import kotlinext.js.js
import kotlinext.js.jsObject
import kotlinx.css.*
import materialui.components.paper.paper
import materialui.styles.makeStyles
import react.*
import react.dom.br

fun RBuilder.colorItem(handler: RHandler<ColorItemProps>) = child(functionalComponent<ColorItemProps> { props ->
    val classes = useStyles()

    paper {
        attrs {
            classes(classes.root)
            setProp("style", js {
                this["background-color"] = props.color
                this["color"] = if (props.isBrighter) Color.black.toString() else Color.white.toString()
            } as Any)
        }

        +props.name
        br { }
        +props.color
    }
}, handler = handler)

external interface ColorItemProps : RProps {
    var name: String
    var color: String
    var isBrighter: Boolean
}

external interface ColorItemStyle {
    val root: String
}

private val useStyles = makeStyles<ColorItemStyle> {
    "root" {
        width = 200.px
        height = 50.px
        margin(4.px)
        textAlign = TextAlign.center
        fontWeight = FontWeight.w700
    }
}
