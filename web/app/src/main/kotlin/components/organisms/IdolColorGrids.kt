package components.organisms

import components.molecules.colorGridItem
import kotlinx.css.*
import materialui.styles.makeStyles
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.UiModel.Search.IdolColorItem
import org.w3c.dom.HTMLDivElement
import react.*
import react.dom.div

fun RBuilder.idolColorGrids(handler: RHandler<IdolColorGridsProps>) = child(IdolColorGridsComponent, handler = handler)

private val IdolColorGridsComponent = functionalComponent<IdolColorGridsProps> { props ->
    val classes = useStyles()
    val containerRef = useRef<HTMLDivElement?>(null)

    div(classes.root) {
        div(classes.container) {
            ref = containerRef

            props.items.forEach { (idolColor, selected) ->
                colorGridItem {
                    // TODO Change other way
                    val columns = (containerRef.current?.clientWidth ?: 200) / 200
                    val width = containerRef.current?.let { it.clientWidth / columns - 8 }

                    attrs {
                        item = idolColor
                        isSelected = selected
                        onClick = props.onClick
                        onDoubleClick = props.onDoubleClick
                        this.width = width
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
