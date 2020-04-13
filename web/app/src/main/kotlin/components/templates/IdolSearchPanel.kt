package components.templates

import components.atoms.*
import components.organisms.idolColorGrids
import components.organisms.idolSearchBox
import kotlinx.css.*
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.drawer.enums.DrawerStyle
import materialui.components.drawer.enums.DrawerVariant
import materialui.components.hidden.hidden
import materialui.components.icon.icon
import materialui.components.iconbutton.iconButton
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import materialui.styles.muitheme.spacing
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
        responsiveDrawer {
            attrs.anchor = DrawerAnchor.right

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
}

private val useStyles = makeStyles<IdolSearchPanelStyle> {
    "root" {
        display = Display.flex
    }
    "panel" {
        flexGrow = 1.0
    }
}
