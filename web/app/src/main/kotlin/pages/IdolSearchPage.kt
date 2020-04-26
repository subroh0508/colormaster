package pages

import components.templates.IdolSearchPanelProps
import components.templates.idolSearchPanel
import kotlinext.js.Object
import react.*
import utilities.isMobile

@Suppress("FunctionName")
fun RBuilder.IdolSearchPage(handler: RHandler<IdolSearchPageProps>) = child(IdolSearchPageComponent, handler = handler)

private val IdolSearchPageComponent = functionalComponent<IdolSearchPageProps> { props ->
    // TODO Use useMediaQuery
    val (openedGrids, setOpenedGrids) = useState(!isMobile)

    idolSearchPanel {
        Object.assign(attrs, props)
        attrs.isOpenedGrids = openedGrids
        attrs.onClickToggleGrids = { setOpenedGrids(!openedGrids) }
        attrs.onCloseGrids = { setOpenedGrids(false) }
    }
}

external interface IdolSearchPageProps : IdolSearchPanelProps
