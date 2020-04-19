package pages.search

import components.atoms.ToolbarComponent
import components.atoms.appFrame
import components.templates.IdolSearchPanelProps
import components.templates.IdolSearchToolbarProps
import components.templates.idolSearchPanel
import components.templates.idolSearchToolbar
import containers.IdolSearchContainer
import kotlinext.js.Object
import react.*
import utilities.isMobile

@Suppress("FunctionName")
fun RBuilder.IdolSearchPage(handler: RHandler<IdolSearchPageProps>) = child(IdolSearchPageComponent, handler = handler)

private val IdolSearchPageComponent = functionalComponent<IdolSearchPageProps> { props ->
    // TODO Use useMediaQuery
    val (openedSearchBox, setOpenedSearchBox) = useState(!isMobile)

    appFrame {
        attrs.ToolbarComponent {
            idolSearchToolbar {
                attrs.isOpenedSearchBox = openedSearchBox
                attrs.onClickSearchButton = { setOpenedSearchBox(!openedSearchBox) }
            }
        }

        idolSearchPanel {
            Object.assign(attrs, props)
            attrs.isOpenedSearchBox = openedSearchBox
            attrs.onCloseSearchBox = { setOpenedSearchBox(false) }
        }
    }
}

external interface IdolSearchPageProps : IdolSearchPanelProps
