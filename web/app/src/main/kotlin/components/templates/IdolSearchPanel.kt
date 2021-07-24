package components.templates

import components.atoms.*
import components.organisms.IDOL_COLOR_GRID_ACTIONS_CLASS_NAME
import components.organisms.idolColorGridsActions
import components.organisms.idolColorGrids
import components.organisms.searchbox.idolSearchBox
import components.organisms.searchbox.message
import containers.AuthenticationProviderContext
import kotlinx.css.*
import kotlinx.css.properties.borderTop
import materialui.components.drawer.enums.DrawerAnchor
import materialui.styles.breakpoint.Breakpoint
import materialui.styles.breakpoint.up
import materialui.styles.makeStyles
import materialui.styles.mixins.toolbar
import materialui.styles.palette.default
import materialui.styles.palette.divider
import materialui.styles.palette.paper
import materialui.useMediaQuery
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.authentication.CurrentUser
import net.subroh0508.colormaster.presentation.search.model.SearchUiModel
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import react.*
import react.dom.div
import themes.APP_BAR_SM_UP
import utilities.useTranslation

fun RBuilder.idolSearchPanel(handler: RHandler<IdolSearchPanelProps>) = child(IdolSearchPanelComponent, handler = handler)

private val IdolSearchPanelComponent = functionalComponent<IdolSearchPanelProps> { props ->
    val classes = useStyles()
    val (t, _) = useTranslation()
    val isSmUp = useMediaQuery("@media (min-width: 600px)")
    val uiModel = props.model

    val currentUser = useContext(AuthenticationProviderContext)

    val drawerAnchor = if (isSmUp) DrawerAnchor.right else DrawerAnchor.bottom
    val actionsStyle = "${classes.actions} ${if (props.isOpenedGrids) "" else classes.actionsHide}"

    div(classes.root) {
        div(classes.searchBox) {
            div(classes.searchBoxTop) {}
            idolSearchBox(uiModel) {
                attrs.onChangeSearchParams = props.onChangeSearchParams
                attrs.onChangeSearchQuery = props.onChangeSearchQuery
            }
        }

        responsiveDrawer {
            attrs.anchor = drawerAnchor
            attrs.opened = props.isOpenedGrids
            attrs.onClose = props.onCloseGrids
            attrs.onClickExpandIcon = props.onClickToggleGrids
            attrs.HeaderComponent {
                message(props.isOpenedGrids, uiModel)
            }

            div(classes.panel) {
                idolColorGrids {
                    attrs.items = uiModel.items
                    attrs.isSignedIn = currentUser != null
                    attrs.onClick = props.onClickIdolColor
                    attrs.onDoubleClick = props.onDoubleClickIdolColor
                    attrs.onFavoriteClick = props.onFavoriteClickIdolColor
                }
            }

            div(actionsStyle) {
                div(classes.toolbar) {}
                idolColorGridsActions {
                    attrs.selected = uiModel.selectedItems
                    attrs.onClickPreview = props.onClickPreview
                    attrs.onClickPenlight = props.onClickPenlight
                    attrs.onClickSelectAll = props.onClickSelectAll
                }
            }
        }
    }
}

external interface IdolSearchPanelProps : RProps {
    var model: SearchUiModel
    var currentUser: CurrentUser?
    var isOpenedGrids: Boolean
    var onClickToggleGrids: () -> Unit
    var onChangeSearchParams: (SearchParams) -> Unit
    var onChangeSearchQuery: (String?) -> Unit
    var onClickIdolColor: (IdolColor, Boolean) -> Unit
    var onDoubleClickIdolColor: (IdolColor) -> Unit
    var onFavoriteClickIdolColor: (IdolColor, Boolean) -> Unit
    var onClickPreview: () -> Unit
    var onClickPenlight: () -> Unit
    var onClickSelectAll: (Boolean) -> Unit
    var onCloseGrids: () -> Unit
}

private external interface IdolSearchPanelStyle {
    val root: String
    val searchBox: String
    val panel: String
    val toolbar: String
    val actions: String
    val actionsHide: String
    val searchBoxTop: String
}

private val TOOLBAR_HEIGHT = 64.px
private val TABS_HEIGHT = 48.px
private val ACTION_BUTTONS_HEIGHT = 48.px

private val useStyles = makeStyles<IdolSearchPanelStyle> {
    "root" {
        display = Display.flex
        height = 100.vh
    }
    "searchBox" {
        width = 100.pct
        backgroundColor = theme.palette.background.default
    }
    "panel" {
        flexGrow = 1.0
        marginBottom = 120.px

        (theme.breakpoints.up(Breakpoint.sm)) {
            marginBottom = 0.px
        }
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
            height = 0.px
            width = 0.px
        }

        descendants(".$IDOL_COLOR_GRID_ACTIONS_CLASS_NAME") {
            padding(8.px, 4.px)
            backgroundColor = theme.palette.background.paper
            borderTop(1.px, BorderStyle.solid, theme.palette.divider)
            borderRadius = 0.px
            width = 100.pct

            (theme.breakpoints.up(Breakpoint.sm)) {
                borderTop = "none"
                width = APP_BAR_SM_UP
                padding(16.px, 16.px, 0.px)
                backgroundColor = theme.palette.background.default
            }
        }
    }
    "actionsHide" {
        display = Display.none
    }
    "toolbar"(theme.mixins.toolbar.apply {
        display = Display.none

        (theme.breakpoints.up(Breakpoint.sm)) {
            display = Display.block
            minHeight = TOOLBAR_HEIGHT + TABS_HEIGHT
        }
    })
    "searchBoxTop"(theme.mixins.toolbar.apply {
        marginBottom = TABS_HEIGHT + 16.px

        (theme.breakpoints.up(Breakpoint.sm)) {
            minHeight = TOOLBAR_HEIGHT
            marginBottom = TABS_HEIGHT + ACTION_BUTTONS_HEIGHT + 16.px
        }
    })
}
