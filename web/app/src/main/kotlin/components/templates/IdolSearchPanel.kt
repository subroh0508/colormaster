package components.templates

import components.atoms.*
import components.organisms.idolColorGridsActions
import components.organisms.idolColorGrids
import components.organisms.idolSearchBox
import kotlinx.css.*
import materialui.components.drawer.enums.DrawerAnchor
import materialui.styles.makeStyles
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

    val panelStyle = "${classes.panel} ${if (props.isOpenedSearchBox) classes.panelShift else ""}"

    div(classes.root) {
        div(panelStyle) {
            alert(uiModel)
            idolColorGrids {
                attrs.items = uiModel.items
                attrs.onClick = props.onClickIdolColor
                attrs.onDoubleClick = props.onDoubleClickIdolColor
            }
        }
        responsiveDrawer {
            attrs.anchor = DrawerAnchor.right
            attrs.opened = props.isOpenedSearchBox
            attrs.onClose = props.onCloseSearchBox
            attrs.HeaderComponent {
                idolColorGridsActions {
                    attrs.selected = uiModel.selected
                    attrs.onClickPreview = props.onClickPreview
                    attrs.onClickSelectAll = props.onClickSelectAll
                }
            }

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
    var isOpenedSearchBox: Boolean
    var onChangeIdolName: (String) -> Unit
    var onSelectTitle: (Titles, Boolean) -> Unit
    var onSelectType: (Types, Boolean) -> Unit
    var onClickIdolColor: (IdolColor, Boolean) -> Unit
    var onDoubleClickIdolColor: (IdolColor) -> Unit
    var onClickPreview: () -> Unit
    var onClickSelectAll: (Boolean) -> Unit
    var onCloseSearchBox: () -> Unit
}

private external interface IdolSearchPanelStyle {
    val root: String
    val panel: String
    val panelShift: String
}

private val useStyles = makeStyles<IdolSearchPanelStyle> {
    "root" {
        display = Display.flex
    }
    "panel" {
        flexGrow = 1.0
        marginRight = -DRAWER_WIDTH
    }
    "panelShift" {
        marginRight = 0.px
    }
}
