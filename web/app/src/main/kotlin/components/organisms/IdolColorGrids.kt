package components.organisms

import components.atoms.CLICK_HANDLER_CLASS_NAME
import components.atoms.clickHandler
import components.molecules.colorGridItem
import kotlinx.css.*
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
                clickHandler {
                    key = item.id

                    attrs {
                        onClick = { props.onClick(item, !selected) }
                        onDoubleClick = { props.onDoubleClick(item) }
                    }

                    colorGridItem {
                        attrs {
                            name = item.name
                            color = item.color
                            isBrighter = item.isBrighter
                            isSelected = selected
                        }
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

        descendants(".$CLICK_HANDLER_CLASS_NAME") {
            width = 100.pct
            margin(4.px)

            (theme.breakpoints.up(Breakpoint.sm)) {
                width = 200.px
            }
        }
    }
}
