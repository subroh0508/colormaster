package components.templates

import components.atoms.*
import components.organisms.IDOL_COLOR_GRID_ACTIONS_CLASS_NAME
import components.organisms.idolColorGridsActions
import components.organisms.idolColorGrids
import components.organisms.idolSearchBox
import kotlinx.css.*
import kotlinx.css.properties.borderTop
import materialui.components.drawer.enums.DrawerAnchor
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import materialui.styles.palette.divider
import materialui.styles.palette.paper
import materialui.useMediaQuery
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
    val isSmUp = useMediaQuery("@media (min-width: 600px)")
    val uiModel = props.model

    val drawerAnchor = if (isSmUp) DrawerAnchor.right else DrawerAnchor.bottom
    val actionsStyle = "${classes.actions} ${if (props.isOpenedGrids) "" else classes.actionsHide}"

    div(classes.root) {
        div {
            div(classes.toolbar) {}
            div(classes.searchBoxTop) {}
            idolSearchBox {
                attrs.filters = uiModel.filters
                attrs.onChangeIdolName = props.onChangeIdolName
                attrs.onSelectTitle = props.onSelectTitle
                attrs.onSelectType = props.onSelectType
            }
        }

        responsiveDrawer {
            attrs.anchor = drawerAnchor
            attrs.opened = props.isOpenedGrids
            attrs.onClose = props.onCloseGrids
            attrs.onClickExpandIcon = props.onClickToggleGrids
            attrs.HeaderComponent {
                alert(props.isOpenedGrids, uiModel)
            }

            div(classes.panel) {
                idolColorGrids {
                    attrs.items = uiModel.items
                    attrs.onClick = props.onClickIdolColor
                    attrs.onDoubleClick = props.onDoubleClickIdolColor
                }
            }

            div(actionsStyle) {
                div(classes.toolbar) {}
                idolColorGridsActions {
                    attrs.selected = uiModel.selected
                    attrs.onClickPreview = props.onClickPreview
                    attrs.onClickPenlight = props.onClickPenlight
                    attrs.onClickSelectAll = props.onClickSelectAll
                }
            }
        }
    }
}

private fun RBuilder.alert(opened: Boolean, uiModel: UiModel.Search) = when {
    uiModel.isLoading -> warningAlert {
        attrs.message = "検索中..."
    }
    uiModel.filters is Filters.Empty -> infoAlert {
        attrs.message = "ランダムに10人表示中"
    }
    uiModel.error != null -> errorAlert {
        attrs.title = "エラー"
        attrs.message = if (opened) uiModel.error?.message ?: "" else ""
    }
    else -> successAlert {
        attrs.message = "検索結果: ${uiModel.items.size}件"
    }
}

external interface IdolSearchPanelProps : RProps {
    var model: UiModel.Search
    var isOpenedGrids: Boolean
    var onClickToggleGrids: () -> Unit
    var onChangeIdolName: (String) -> Unit
    var onSelectTitle: (Titles, Boolean) -> Unit
    var onSelectType: (Types, Boolean) -> Unit
    var onClickIdolColor: (IdolColor, Boolean) -> Unit
    var onDoubleClickIdolColor: (IdolColor) -> Unit
    var onClickPreview: () -> Unit
    var onClickPenlight: () -> Unit
    var onClickSelectAll: (Boolean) -> Unit
    var onCloseGrids: () -> Unit
}

private external interface IdolSearchPanelStyle {
    val root: String
    val panel: String
    val toolbar: String
    val actions: String
    val actionsHide: String
    val searchBoxTop: String
}

private val useStyles = makeStyles<IdolSearchPanelStyle> {
    "root" {
        display = Display.flex
    }
    "panel" {
        flexGrow = 1.0
    }
    "actions" {
        position = Position.fixed
        zIndex = theme.zIndex.drawer.toInt() + 1
        left = 0.px

        (theme.breakpoints.up(Breakpoint.xs)) {
            right = 0.px
            bottom = 0.px
        }

        (theme.breakpoints.up(Breakpoint.sm)) {
            top = 0.px
        }

        descendants(".$IDOL_COLOR_GRID_ACTIONS_CLASS_NAME") {
            padding(8.px, 4.px)
            backgroundColor = theme.palette.background.paper
            borderTop(1.px, BorderStyle.solid, theme.palette.divider)
            borderRadius = 0.px

            (theme.breakpoints.up(Breakpoint.sm)) {
                borderTop = "none"
                width = APP_BAR_SM_UP
                padding(8.px, 16.px)
            }
        }
    }
    "actionsHide" {
        display = Display.none
    }
    "toolbar"(theme.mixins.toolbar)
    "searchBoxTop" {
        height = 0.px

        (theme.breakpoints.up(Breakpoint.sm)) {
            height = 48.px
        }
    }
}
