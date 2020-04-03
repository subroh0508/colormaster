package components.templates

import components.organisms.idolColorGrids
import kotlinx.css.*
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.drawer.enums.DrawerStyle
import materialui.components.drawer.enums.DrawerVariant
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.textfield.textField
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import net.subroh0508.colormaster.model.IdolColor
import react.*
import react.dom.div
import react.dom.form

fun RBuilder.idolSearchPanel(handler: RHandler<IdolSearchPanelProps>) = child(functionalComponent<IdolSearchPanelProps> { props ->
    val classes = useStyles()

    div(classes.root) {
        idolColorGrids { attrs.items = props.items }
        drawer(
            DrawerStyle.root to classes.drawer,
            DrawerStyle.paper to classes.drawerPaper
        ) {
            attrs {
                variant = DrawerVariant.permanent
                anchor = DrawerAnchor.right
            }

            div(classes.toolbar) {}
            list {
                listItem {
                    form {
                        textField {
                            attrs {
                                label { +"アイドル名" }
                                variant = FormControlVariant.outlined
                                value = props.idolName
                            }
                        }
                    }
                }
            }
        }
    }
}, handler = handler)

external interface IdolSearchPanelProps : RProps {
    var items: List<IdolColor>
    var idolName: String
}

private external interface IdolSearchPanelStyle {
    val root: String
    val toolbar: String
    val drawer: String
    val drawerPaper: String
}

private val DRAWER_WIDTH = 240.px

private val useStyles = makeStyles<IdolSearchPanelStyle> {
    "root" {
        display = Display.flex
    }
    css["toolbar"] = theme.mixins.toolbar
    "drawer" {
        width = DRAWER_WIDTH
        flexShrink = 0.0
    }
    "drawerPaper" {
        width = DRAWER_WIDTH
    }
}
