package pages.search

import components.atoms.ToolbarComponent
import components.atoms.appFrame
import components.templates.IdolSearchPanelProps
import components.templates.IdolSearchToolbarProps
import components.templates.idolSearchPanel
import components.templates.idolSearchToolbar
import containers.IdolSearchContainer
import react.*

@Suppress("FunctionName")
fun RBuilder.IdolSearchPage(handler: RHandler<IdolSearchPageProps>) = child(IdolSearchPageComponent, handler = handler)

private val IdolSearchPageComponent = functionalComponent<IdolSearchPageProps> { props ->
    val (openedSearchBox, setOpenedSearchBox) = useState(false)

    appFrame {
        attrs.ToolbarComponent {
            idolSearchToolbar {
                attrs.isOpenedSearchBox = openedSearchBox
                attrs.onClickSearchButton = { setOpenedSearchBox(!openedSearchBox) }
            }
        }

        idolSearchPanel(props)
    }
}

external interface IdolSearchPageProps : IdolSearchPanelProps
