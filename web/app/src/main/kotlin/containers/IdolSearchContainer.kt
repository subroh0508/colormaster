package containers

import appKodein
import components.templates.idolSearchPanel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mainScope
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.model.UiModel
import net.subroh0508.colormaster.model.toIdolName
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

    fun onChangeIdolName(name: String?) = dispatch(actions(type = ActionTypes.ON_CHANGE_IDOL_NAME, idolName = name.toIdolName()))
    fun onSuccess(items: List<IdolColor>) = dispatch(actions(type = ActionTypes.ON_SUCCESS, items = items))
    fun onFailure(e: Throwable) = dispatch(actions(type = ActionTypes.ON_FAILURE, error = e))

    fun Controller.search(idolName: IdolName? = null) = launch {
        runCatching { fetchItems(idolName) }
                .onSuccess(::onSuccess)
                .onFailure(::onFailure)
    }

    useEffectDidMount { controller.search() }
    useDebounceEffect(uiModel.idolName, 500) { controller.search(it) }

    idolSearchPanel {
        attrs.model = uiModel
        attrs.onChangeIdolName = ::onChangeIdolName
    }
}

private enum class ActionTypes {
    ON_CHANGE_IDOL_NAME, ON_SUCCESS, ON_FAILURE
}

private fun actions(
    type: ActionTypes,
    idolName: IdolName? = null,
    items: List<IdolColor> = listOf(),
    error: Throwable? = null
) = actions<ActionTypes, UiModel.Search> {
    this.type = type
    this.payload = UiModel.Search(idolName = idolName, items = items, error = error)
}

private val reducer = { state: UiModel.Search, action: Actions<ActionTypes, UiModel.Search> ->
    val (items, idolName, error, _) = action.payload

    when (action.type) {
        ActionTypes.ON_CHANGE_IDOL_NAME -> state.copy(items = listOf(), idolName = idolName, error = null, isLoading = true)
        ActionTypes.ON_SUCCESS -> state.copy(items = items, error = null, isLoading = false)
        ActionTypes.ON_FAILURE -> state.copy(items = listOf(), error = error, isLoading = false)
        else -> state
    }
}

private val IdolSearchController = createContext<Controller>()

private object Controller : CoroutineScope by mainScope, KodeinAware {
    const val LIMIT = 10

    val repository: IdolColorsRepository by instance()

    suspend fun fetchItems(
        idolName: IdolName?
    ) = if (idolName == null) repository.rand(LIMIT) else repository.search(idolName)

    override val kodein = appKodein
}
