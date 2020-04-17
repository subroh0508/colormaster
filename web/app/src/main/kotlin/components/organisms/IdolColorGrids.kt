package components.organisms

import components.atoms.colorItem
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onDoubleClickFunction
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
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
            props.items.forEach { (item, selected) ->
                child(GridItemComponent) {
                    attrs.item = item
                    attrs.selected = selected
                    attrs.onClick = props.onClick
                    attrs.onDoubleClick = props.onDoubleClick
                }
            }
        }
    }
}

private val GridItemComponent = functionalComponent<GridItemProps> { props ->
    div("grid-item") {
        attrs {
            onClickFunction = { props.onClick(props.item, !props.selected) }
            //onDoubleClickFunction  = { props.onDoubleClick(props.item) }
        }

        colorItem {
            attrs {
                id = props.item.id
                name = props.item.name
                color = props.item.color
                isBrighter = props.item.isBrighter
                isSelected = props.selected
            }
        }
    }
}

external interface IdolColorGridsProps : RProps {
    var items: List<IdolColorItem>
    var onClick: (item: IdolColor, isSelected: Boolean) -> Unit
    var onDoubleClick: (item: IdolColor) -> Unit
}

private external interface GridItemProps : RProps {
    var item: IdolColor
    var selected: Boolean
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

        descendants(".grid-item") {
            width = 100.pct
            margin(4.px)

            (theme.breakpoints.up(Breakpoint.sm)) {
                width = 200.px
            }
        }
    }
}
