package components.templates

import components.atoms.errorAlert
import components.atoms.infoAlert
import components.atoms.successAlert
import components.atoms.warningAlert
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
import net.subroh0508.colormaster.model.Titles
import net.subroh0508.colormaster.model.Types
import net.subroh0508.colormaster.model.UiModel
import net.subroh0508.colormaster.model.ui.idol.Filters
import react.*
import react.dom.div

fun RBuilder.idolSearchPanel(handler: RHandler<IdolSearchPanelProps>) = child(IdolSearchPanelComponent, handler = handler)

private val IdolSearchPanelComponent = functionalComponent<IdolSearchPanelProps> { props ->
    val classes = useStyles()
    val uiModel = props.model

    div(classes.root) {
        div(classes.panel) {
            alert(uiModel)
            idolColorGrids {
                attrs.items = uiModel.items
                attrs.onDoubleClick = props.onDoubleClickIdolColor
            }
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
                attrs.filters = uiModel.filters
                attrs.onChangeIdolName = props.onChangeIdolName
                attrs.onSelectTitle = props.onSelectTitle
                attrs.onSelectType = props.onSelectType
            }
        }
    }
}

private fun RBuilder.alert(uiModel: UiModel.Search) = when {
    uiModel.isLoading -> warningAlert {
        attrs.message = "検索中..."
    }
    uiModel.filters is Filters.Empty -> infoAlert {
        attrs.message = "ランダムに10人のアイドルを表示しています"
    }
    uiModel.error != null -> errorAlert {
        attrs.title = "エラー"
        attrs.message = uiModel.error?.message ?: ""
    }
    else -> successAlert {
        attrs.message = "検索結果: ${uiModel.items.size}件"
    }
}

external interface IdolSearchPanelProps : RProps {
    var model: UiModel.Search
    var onChangeIdolName: (String) -> Unit
    var onSelectTitle: (Titles, Boolean) -> Unit
    var onSelectType: (Types, Boolean) -> Unit
    var onDoubleClickIdolColor: (IdolColor) -> Unit
}

private external interface IdolSearchPanelStyle {
    val root: String
    val panel: String
    val toolbar: String
    val drawer: String
    val drawerPaper: String
}

private val DRAWER_WIDTH = 240.px

private val useStyles = makeStyles<IdolSearchPanelStyle> {
    "root" {
        display = Display.flex
    }
    "panel" {
        flexGrow = 1.0
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
