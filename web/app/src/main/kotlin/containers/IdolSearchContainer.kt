package containers

import appDI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mainScope
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.presentation.search.model.IdolColorListItem
import net.subroh0508.colormaster.presentation.search.model.ManualSearchUiModel
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import pages.IdolSearchPage
import react.*
import react.router.dom.useHistory
import toPenlight
import toPreview
import utilities.*

@Suppress("FunctionName")
fun RBuilder.IdolSearchContainer() = IdolSearchControllerContext.Provider(IdolSearchController) { child(IdolSearchContainerImpl) }

private val IdolSearchContainerImpl = functionalComponent<RProps> {
    val history = useHistory()

    fun preview(items: List<IdolColor>) = history.toPreview(items.joinToString("&") { "id=${it.id}" })
    fun turnOnPenlight(items: List<IdolColor>) = history.toPenlight(items.joinToString("&") { "id=${it.id}" })

    val controller = useContext(IdolSearchControllerContext)

    val (uiModel, dispatch) = useReducer(reducer, ManualSearchUiModel.INITIALIZED)

    fun onChangeIdolName(params: SearchParams, name: String?) = dispatch(actions(type = IdolSearchActionTypes.ON_CHANGE_FILTER, params = params.change(name.toIdolName())))
    fun onSelectBrands(params: SearchParams, brands: Brands, checked: Boolean) = dispatch(actions(type = IdolSearchActionTypes.ON_CHANGE_FILTER, params = params.change(if (checked) brands else null)))
    fun onSelectType(params: SearchParams, type: Types, checked: Boolean) = dispatch(actions(type = IdolSearchActionTypes.ON_CHANGE_FILTER, params = params.change(type, checked)))
    fun onSuccess(items: List<IdolColor>) = dispatch(actions(type = IdolSearchActionTypes.ON_SUCCESS, items = items.map(::IdolColorListItem)))
    fun onFailure(e: Throwable) = dispatch(actions(type = IdolSearchActionTypes.ON_FAILURE, error = e))
    fun onSelectIdol(item: IdolColor, selected: Boolean) = dispatch(actions(type = IdolSearchActionTypes.ON_SELECT, items = listOf(IdolColorListItem(item, selected))))
    fun onSelectAll(selected: Boolean) = dispatch(actions(type = if (selected) IdolSearchActionTypes.ON_SELECT_ALL else IdolSearchActionTypes.ON_CLEAR_ALL))

    fun IdolSearchController.search(params: SearchParams = SearchParams.EMPTY) = launch {
        runCatching { fetchItems(params) }
                .onSuccess(::onSuccess)
                .onFailure(::onFailure)
    }

    useEffectDidMount { controller.search() }
    useDebounceEffect(uiModel.params, 500) { controller.search(it) }

    IdolSearchPage {
        attrs.model = uiModel
        attrs.onChangeIdolName = { name -> onChangeIdolName(uiModel.params, name) }
        attrs.onSelectTitle = { title, checked -> onSelectBrands(uiModel.params, title, checked) }
        attrs.onSelectType = { type, checked -> onSelectType(uiModel.params, type, checked) }
        attrs.onClickIdolColor = { item, selected -> onSelectIdol(item, selected) }
        attrs.onClickSelectAll = { selected -> onSelectAll(selected) }
        attrs.onDoubleClickIdolColor = { item -> turnOnPenlight(listOf(item)) }
        attrs.onClickPreview = { preview(uiModel.selectedItems) }
        attrs.onClickPenlight = { turnOnPenlight(uiModel.selectedItems) }
    }
}

private enum class IdolSearchActionTypes {
    ON_CHANGE_FILTER, ON_SUCCESS, ON_FAILURE, ON_SELECT, ON_SELECT_ALL, ON_CLEAR_ALL
}

private fun actions(
    type: IdolSearchActionTypes,
    params: SearchParams = SearchParams.EMPTY,
    items: List<IdolColorListItem> = listOf(),
    error: Throwable? = null
) = actions<IdolSearchActionTypes, ManualSearchUiModel> {
    this.type = type
    this.payload = ManualSearchUiModel(params = params, items = items, error = error)
}

private val reducer = { state: ManualSearchUiModel, action: Actions<IdolSearchActionTypes, ManualSearchUiModel> ->
    val (items, params, error, _) = action.payload

    when (action.type) {
        IdolSearchActionTypes.ON_CHANGE_FILTER -> state.copy(items = listOf(), params = params, error = null, isLoading = true)
        IdolSearchActionTypes.ON_SUCCESS -> state.copy(items = items, error = null, isLoading = false)
        IdolSearchActionTypes.ON_SELECT -> state.copy(items = state.items.map { item -> items.first().let { if (it.id == item.id) item.copy(selected = it.selected) else item } })
        IdolSearchActionTypes.ON_SELECT_ALL -> state.copy(items = state.items.map { it.copy(selected = true) })
        IdolSearchActionTypes.ON_CLEAR_ALL -> state.copy(items = state.items.map { it.copy(selected = false) })
        IdolSearchActionTypes.ON_FAILURE -> state.copy(items = listOf(), error = error, isLoading = false)
    }
}

private val IdolSearchControllerContext = createContext<IdolSearchController>()

private object IdolSearchController : CoroutineScope by mainScope, KoinComponent {
    const val LIMIT = 10

    val repository: IdolColorsRepository by inject()

    suspend fun fetchItems(params: SearchParams) =
        if (params == SearchParams.EMPTY)
            repository.rand(LIMIT)
        else
            repository.search(params.idolName, params.brands, params.types)

    override fun getKoin() = appDI.koin
}
