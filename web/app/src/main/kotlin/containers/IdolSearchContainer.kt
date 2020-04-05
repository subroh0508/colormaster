package containers

import appKodein
import components.templates.idolSearchPanel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mainScope
import net.subroh0508.colormaster.model.*
import net.subroh0508.colormaster.model.ui.idol.Filters
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.kodein.di.KodeinAware
import org.kodein.di.erased.instance
import react.*
import utilities.Actions
import utilities.actions
import utilities.useDebounceEffect
import utilities.useEffectDidMount

@Suppress("FunctionName")
fun RBuilder.IdolSearchContainer() = IdolSearchController.Provider(Controller) { child(IdolSearchContainerImpl) }

private val IdolSearchContainerImpl = functionalComponent<RProps> {
    val controller = useContext(IdolSearchController)

    val (uiModel, dispatch) = useReducer(reducer, UiModel.Search.INITIALIZED)

    fun onChangeIdolName(name: String?) = dispatch(actions(type = ActionTypes.ON_CHANGE_IDOL_NAME, idolName = name.toIdolName(), filters = uiModel.filters))
    fun onSelectTitle(title: Titles, checked: Boolean) = dispatch(actions(type = ActionTypes.ON_SELECT_TITLE, idolName = uiModel.idolName, filters = if (checked) Filters(title) else Filters.Empty))
    fun onSelectType(filters: Filters, type: Types, checked: Boolean) = dispatch(actions(type = ActionTypes.ON_SELECT_TYPE, idolName = uiModel.idolName, filters = if (checked) filters + type else filters - type))
    fun onSuccess(items: List<IdolColor>) = dispatch(actions(type = ActionTypes.ON_SUCCESS, items = items))
    fun onFailure(e: Throwable) = dispatch(actions(type = ActionTypes.ON_FAILURE, error = e))

    fun Controller.search(model: UiModel.Search = UiModel.Search.INITIALIZED) = launch {
        runCatching { fetchItems(model) }
                .onSuccess(::onSuccess)
                .onFailure(::onFailure)
    }

    useEffectDidMount { controller.search() }
    useDebounceEffect(uiModel.idolName, 500) { controller.search(uiModel) }

    idolSearchPanel {
        attrs.model = uiModel
        attrs.onChangeIdolName = ::onChangeIdolName
        attrs.onSelectTitle = ::onSelectTitle
        attrs.onSelectType = { type, checked -> onSelectType(uiModel.filters, type, checked) }
    }
}

private enum class ActionTypes {
    ON_CHANGE_IDOL_NAME, ON_SELECT_TITLE, ON_SELECT_TYPE, ON_SUCCESS, ON_FAILURE
}

private fun actions(
    type: ActionTypes,
    idolName: IdolName? = null,
    filters: Filters = Filters.Empty,
    items: List<IdolColor> = listOf(),
    error: Throwable? = null
) = actions<ActionTypes, UiModel.Search> {
    this.type = type
    this.payload = UiModel.Search(idolName = idolName, filters = filters, items = items, error = error)
}

private val reducer = { state: UiModel.Search, action: Actions<ActionTypes, UiModel.Search> ->
    val (items, idolName, filters, error, _) = action.payload

    when (action.type) {
        ActionTypes.ON_CHANGE_IDOL_NAME -> state.copy(items = listOf(), idolName = idolName, error = null, isLoading = true)
        ActionTypes.ON_SELECT_TITLE,
        ActionTypes.ON_SELECT_TYPE -> state.copy(items = listOf(), filters = filters, error = null, isLoading = true)
        ActionTypes.ON_SUCCESS -> state.copy(items = items, error = null, isLoading = false)
        ActionTypes.ON_FAILURE -> state.copy(items = listOf(), error = error, isLoading = false)
    }
}

private val IdolSearchController = createContext<Controller>()

private object Controller : CoroutineScope by mainScope, KodeinAware {
    const val LIMIT = 10

    val repository: IdolColorsRepository by instance()

    suspend fun fetchItems(model: UiModel.Search) =
        if (model.isConditionsEmpty)
            repository.rand(LIMIT)
        else
            repository.search(model.idolName, model.filters.title, model.filters.types)

    override val kodein = appKodein
}