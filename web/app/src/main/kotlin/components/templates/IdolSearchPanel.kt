package components.templates

import components.atoms.errorAlert
import components.atoms.infoAlert
import components.atoms.successAlert
import components.organisms.idolColorGrids
import components.organisms.idolSearchBox
import kotlinx.css.*
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.drawer.enums.DrawerStyle
import materialui.components.drawer.enums.DrawerVariant
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import net.subroh0508.colormaster.model.IdolColor
import react.*
import react.dom.div

fun RBuilder.idolSearchPanel(handler: RHandler<IdolSearchPanelProps>) = child(IdolSearchPanelComponent, handler = handler)

private val IdolSearchPanelComponent = functionalComponent<IdolSearchPanelProps> { props ->
    val classes = useStyles()

    div(classes.root) {
        div {
            alert(props)
            idolColorGrids { attrs.items = props.items }
        }
        drawer(
            DrawerStyle.root to classes.drawer,
            DrawerStyle.paper to classes.drawerPaper
        ) {
            attrs {
                variant = DrawerVariant.permanent
                anchor = DrawerAnchor.right
            }

            div(classes.toolbar) {}
            idolSearchBox {
                attrs.idolName = props.idolName
                attrs.onChangeIdolName = props.onChangeIdolName
            }
        }
    }
}

private fun RBuilder.alert(props: IdolSearchPanelProps) = when {
    props.idolName.isNullOrBlank() && props.items.size == 10 -> infoAlert {
        attrs.message = "ランダムに10人のアイドルを表示しています"
    }
    props.error != null -> errorAlert {
        attrs.title = "エラー"
        attrs.message = props.error ?: ""
    }
    else -> successAlert {
        attrs.message = "検索結果: ${props.items.size}件"
    }
}

external interface IdolSearchPanelProps : RProps {
    var items: List<IdolColor>
    var idolName: String?
    var error: String?
    var onChangeIdolName: (String) -> Unit
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
