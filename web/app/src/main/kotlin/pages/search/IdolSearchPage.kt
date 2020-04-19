package pages.search

import components.atoms.ToolbarComponent
import components.atoms.appFrame
import components.templates.idolSearchToolbar
import containers.IdolSearchContainer
import react.*

@Suppress("FunctionName")
fun RBuilder.IdolSearchPage() = child(IdolSearchPageComponent)

private val IdolSearchPageComponent = functionalComponent<RProps> {
    val (openedSearchBox, setOpenedSearchBox) = useState(false)

    appFrame {
        attrs.ToolbarComponent {
            idolSearchToolbar {
                attrs.isOpenedSearchBox = openedSearchBox
                attrs.onClickSearchButton = { setOpenedSearchBox(!openedSearchBox) }
            }
        }

        IdolSearchContainer()
    }
}
