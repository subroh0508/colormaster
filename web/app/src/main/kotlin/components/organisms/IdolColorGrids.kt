package components.organisms

import components.molecules.colorGridItem
import kotlinext.js.jsObject
import kotlinx.css.*
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.search.model.IdolColorListItem
import org.w3c.dom.HTMLDivElement
import react.*
import react.dom.div

fun RBuilder.idolColorGrids(handler: RHandler<IdolColorGridsProps>) = child(IdolColorGridsComponent, handler = handler)

private val IdolColorGridsComponent = functionalComponent<IdolColorGridsProps> { props ->
    val containerRef = useRef<HTMLDivElement?>(null)
    val classes = useStyles(jsObject {
        val width = containerRef.current?.clientWidth ?: 200

        containerWidth = width
        columns = width / 200
    })

    div(classes.root) {
        div(classes.container) {
            ref = containerRef

            props.items.forEach { item ->
                colorGridItem {
                    attrs {
                        this.item = IdolColor(item.id, item.name.value, item.hexColor)
                        isSelected = item.selected
                        onClick = props.onClick
                        onDoubleClick = props.onDoubleClick
                    }
                }
            }
        }
    }
}

external interface IdolColorGridsProps : RProps {
    var items: List<IdolColorListItem>
    var onClick: (item: IdolColor, isSelected: Boolean) -> Unit
    var onDoubleClick: (item: IdolColor) -> Unit
}

private external interface GridsWidth : RProps {
    var containerWidth: Int
    var columns: Int
}

private external interface IdolColorGridsStyle {
    val root: String
    val container: String
}

private val useStyles = makeStyles<IdolColorGridsStyle, GridsWidth> {
    "root" {
        margin(8.px, 4.px)
    }
    "container" { props ->
        flexGrow = 1.0
        display = Display.flex
        flexDirection = FlexDirection.row
        flexWrap = FlexWrap.wrap

        children("div") {
            (theme.breakpoints.up(Breakpoint.sm)) {
                width = (props.containerWidth / props.columns - 8).px
            }
        }
    }
}
