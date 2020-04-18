package components.organisms

import components.molecules.colorGridItem
import kotlinx.css.*
import materialui.styles.makeStyles
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.UiModel.Search.IdolColorItem
import react.*
import react.dom.div

fun RBuilder.idolColorGrids(handler: RHandler<IdolColorGridsProps>) = child(IdolColorGridsComponent, handler = handler)

private val IdolColorGridsComponent = functionalComponent<IdolColorGridsProps> { props ->
    val classes = useStyles()

    div(classes.root) {
        div(classes.container) {
            props.items.forEach { (idolColor, selected) ->
                colorGridItem {
                    attrs {
                        item = idolColor
                        isSelected = selected
                        onClick = props.onClick
                        onDoubleClick = props.onDoubleClick
                    }
                }
            }
        }
    }
}

external interface IdolColorGridsProps : RProps {
    var items: List<IdolColorItem>
    var onClick: (item: IdolColor, isSelected: Boolean) -> Unit
    var onDoubleClick: (item: IdolColor) -> Unit
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
