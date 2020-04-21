package components.templates

import kotlinx.css.flexGrow
import kotlinx.css.marginRight
import kotlinx.html.js.onClickFunction
import materialui.components.button.enums.ButtonColor
import materialui.components.icon.icon
import materialui.components.iconbutton.enums.IconButtonEdge
import materialui.components.iconbutton.iconButton
import materialui.components.toolbar.toolbar
import materialui.components.typography.enums.TypographyVariant
import materialui.components.typography.typography
import materialui.styles.makeStyles
import materialui.styles.muitheme.spacing
import org.w3c.dom.events.Event
import react.*

fun RBuilder.idolSearchToolbar(handler: RHandler<IdolSearchToolbarProps>) = child(IdolSearchToolbarComponent, handler = handler)

private val IdolSearchToolbarComponent = functionalComponent<IdolSearchToolbarProps> { props ->
    val classes = useStyle()

    toolbar {
        iconButton {
            attrs {
                classes(classes.menuButton)
                color = ButtonColor.inherit
                edge = IconButtonEdge.start
            }

            icon { +"menu_icon" }
        }

        typography {
            attrs {
                classes(classes.title)
                variant = TypographyVariant.h6
            }

            +"COLOR M@STER"
        }

        if (props.isOpenedGrids) {
            iconButton {
                attrs {
                    color = ButtonColor.inherit
                    onClickFunction = props.onClickSearchButton
                    edge = IconButtonEdge.end
                }

                icon { +"search_icon" }
            }
        }
    }
}

external interface IdolSearchToolbarProps : RProps {
    var isOpenedGrids: Boolean
    var onClickSearchButton: (Event) -> Unit
}

private external interface IdolSearchToolbarStyle {
    var menuButton: String
    var title: String
    var searchButton: String
}

private val useStyle = makeStyles<IdolSearchToolbarStyle> {
    "menuButton" {
        marginRight = theme.spacing(2)
    }
    "title" {
        flexGrow = 1.0
    }
    "searchButton" {

    }
}
