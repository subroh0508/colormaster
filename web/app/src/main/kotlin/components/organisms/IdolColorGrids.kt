package components.organisms

import components.atoms.colorItem
import kotlinx.css.*
import materialui.styles.makeStyles
import net.subroh0508.colormaster.model.IdolColor
import react.*
import react.dom.div

fun RBuilder.idolColorGrids(handler: RHandler<IdolColorGridsProps>) = child(IdolColorGridsComponent, handler = handler)

private val IdolColorGridsComponent = functionalComponent<IdolColorGridsProps> { props ->
    val classes = useStyles()

    div(classes.root) {
        div(classes.container) {
            props.items.forEach { idolColor ->
                colorItem {
                    attrs {
                        name = idolColor.name
                        color = idolColor.color
                        isBrighter = idolColor.isBrighter
                    }
                }
            }
        }
    }
}

external interface IdolColorGridsProps : RProps {
    var items: List<IdolColor>
}

private external interface IdolColorGridsStyle {
    val root: String
    val container: String
}

private val useStyles = makeStyles<IdolColorGridsStyle> {
    "root" {
        margin(8.px)
        paddingTop = 16.px
    }
    "container" {
        flexGrow = 1.0
        display = Display.flex
        flexDirection = FlexDirection.row
        flexWrap = FlexWrap.wrap
    }
}
