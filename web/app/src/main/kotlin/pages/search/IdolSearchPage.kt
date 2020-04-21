package pages.search

import components.atoms.ToolbarComponent
import components.atoms.appFrame
import components.templates.IdolSearchPanelProps
import components.templates.idolSearchPanel
import components.templates.idolSearchToolbar
import kotlinext.js.Object
import react.*
import utilities.isMobile

@Suppress("FunctionName")
fun RBuilder.IdolSearchPage(handler: RHandler<IdolSearchPageProps>) = child(IdolSearchPageComponent, handler = handler)

private val IdolSearchPageComponent = functionalComponent<IdolSearchPageProps> { props ->
    // TODO Use useMediaQuery
    val (openedGrids, setOpenedGrids) = useState(!isMobile)

    appFrame {
        attrs.ToolbarComponent {
            idolSearchToolbar {
                attrs.isOpenedGrids = openedGrids
                attrs.onClickSearchButton = { setOpenedGrids(!openedGrids) }
            }
        }

        idolSearchPanel {
            Object.assign(attrs, props)
            attrs.isOpenedGrids = openedGrids
            attrs.onClickOpenGrids = { setOpenedGrids(true) }
            attrs.onCloseGrids = { setOpenedGrids(false) }
        }
    }
}

external interface IdolSearchPageProps : IdolSearchPanelProps
