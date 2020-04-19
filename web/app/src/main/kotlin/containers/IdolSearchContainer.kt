package containers

import appKodein
import components.templates.idolSearchPanel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mainScope
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.model.UiModel.Search.IdolColorItem
import net.subroh0508.colormaster.model.ui.idol.Filters
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.kodein.di.KodeinAware
import org.kodein.di.erased.instance
import pages.search.IdolSearchPage
import react.*
import react.router.dom.useHistory
import utilities.*

@Suppress("FunctionName")
fun RBuilder.IdolSearchContainer() = IdolSearchControllerContext.Provider(IdolSearchController) { child(IdolSearchContainerImpl) }

private val IdolSearchContainerImpl = functionalComponent<RProps> {
    val history = useHistory()

    fun preview(items: List<IdolColor>) = history.push("/preview?${items.joinToString("&") { "id=${it.id}" }}")
    fun turnOnPenlight(items: List<IdolColor>) = history.push("/penlight?${items.joinToString("&") { "id=${it.id}" }}")

    val controller = useContext(IdolSearchControllerContext)

    val (uiModel, dispatch) = useReducer(reducer, UiModel.Search.INITIALIZED)

    fun onChangeIdolName(filters: Filters, name: String?) = dispatch(actions(type = IdolSearchActionTypes.ON_CHANGE_FILTER, filters = Filters(name.toIdolName(), filters.title, filters.types)))
    fun onSelectTitle(filters: Filters, title: Titles, checked: Boolean) = dispatch(actions(type = IdolSearchActionTypes.ON_CHANGE_FILTER, filters = if (checked) Filters(filters.idolName, title) else Filters.Empty))
    fun onSelectType(filters: Filters, type: Types, checked: Boolean) = dispatch(actions(type = IdolSearchActionTypes.ON_CHANGE_FILTER, filters = if (checked) filters + type else filters - type))
    fun onSuccess(items: List<IdolColor>) = dispatch(actions(type = IdolSearchActionTypes.ON_SUCCESS, items = items.map(::IdolColorItem)))
    fun onFailure(e: Throwable) = dispatch(actions(type = IdolSearchActionTypes.ON_FAILURE, error = e))
    fun onSelectIdol(item: IdolColor, selected: Boolean) = dispatch(actions(type = IdolSearchActionTypes.ON_SELECT, items = listOf(IdolColorItem(item, selected))))
    fun onSelectAll(selected: Boolean) = dispatch(actions(type = if (selected) IdolSearchActionTypes.ON_SELECT_ALL else IdolSearchActionTypes.ON_CLEAR_ALL))

    fun IdolSearchController.search(filters: Filters = Filters.Empty) = launch {
        runCatching { fetchItems(filters) }
                .onSuccess(::onSuccess)
                .onFailure(::onFailure)
    }

    useEffectDidMount { controller.search() }
    useDebounceEffect(uiModel.filters, 500) { controller.search(it) }

    IdolSearchPage {
        attrs.model = uiModel
        attrs.onChangeIdolName = { name -> onChangeIdolName(uiModel.filters, name) }
        attrs.onSelectTitle = { title, checked -> onSelectTitle(uiModel.filters, title, checked) }
        attrs.onSelectType = { type, checked -> onSelectType(uiModel.filters, type, checked) }
        attrs.onClickIdolColor = { item, selected -> onSelectIdol(item, selected) }
        attrs.onClickSelectAll = { selected -> onSelectAll(selected) }
        attrs.onDoubleClickIdolColor = { item -> turnOnPenlight(listOf(item)) }
        attrs.onClickPreview = { preview(uiModel.selected) }
    }
}

private enum class IdolSearchActionTypes {
    ON_CHANGE_FILTER, ON_SUCCESS, ON_FAILURE, ON_SELECT, ON_SELECT_ALL, ON_CLEAR_ALL
}

private fun actions(
    type: IdolSearchActionTypes,
    filters: Filters = Filters.Empty,
    items: List<IdolColorItem> = listOf(),
    error: Throwable? = null
) = actions<IdolSearchActionTypes, UiModel.Search> {
    this.type = type
    this.payload = UiModel.Search(filters = filters, items = items, error = error)
}

private val reducer = { state: UiModel.Search, action: Actions<IdolSearchActionTypes, UiModel.Search> ->
    val (items, filters, error, _) = action.payload

    when (action.type) {
        IdolSearchActionTypes.ON_CHANGE_FILTER -> state.copy(items = listOf(), filters = filters, error = null, isLoading = true)
        IdolSearchActionTypes.ON_SUCCESS -> state.copy(items = items, error = null, isLoading = false)
        IdolSearchActionTypes.ON_SELECT -> state.copy(items = state.items.map { item -> items.first().let { (color, selected) -> if (color.id == item.idolColor.id) item.copy(selected = selected) else item } })
        IdolSearchActionTypes.ON_SELECT_ALL -> state.copy(items = state.items.map { it.copy(selected = true) })
        IdolSearchActionTypes.ON_CLEAR_ALL -> state.copy(items = state.items.map { it.copy(selected = false) })
        IdolSearchActionTypes.ON_FAILURE -> state.copy(items = listOf(), error = error, isLoading = false)
    }
}

private val IdolSearchControllerContext = createContext<IdolSearchController>()

private object IdolSearchController : CoroutineScope by mainScope, KodeinAware {
    const val LIMIT = 10

    val repository: IdolColorsRepository by instance()

    suspend fun fetchItems(filters: Filters) =
        if (filters is Filters.Empty)
            repository.rand(LIMIT)
        else
            repository.search(filters.idolName, filters.title, filters.types)

    override val kodein = appKodein
}
