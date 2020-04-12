package containers

import appKodein
import components.atoms.fullscreenPenlight
import components.templates.penlightModal
import kotlinext.js.Object
import kotlinext.js.assign
import kotlinext.js.js
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mainScope
import materialui.components.dialog.dialog
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogcontenttext.dialogContentText
import materialui.components.dialogtitle.dialogTitle
import materialui.components.slide.enums.SlideDirection
import materialui.components.slide.slide
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.UiModel
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.kodein.di.KodeinAware
import org.kodein.di.erased.instance
import react.*
import useQuery
import utilities.Actions
import utilities.useEffectDidMount

@Suppress("FunctionName")
fun RBuilder.PenlightContainer() = PenlightControllerContext.Provider(PenlightController) { child(PenlightContainerComponentImpl) }

private val PenlightContainerComponentImpl = functionalComponent<RProps> {
    val ids = useQuery().getAll("id").toList()

    val controller = useContext(PenlightControllerContext)

    val (uiModel, dispatch) = useReducer(reducer, UiModel.Penlight.INITIALIZED)

    fun onSuccess(items: List<IdolColor>) = dispatch(actions(type = PenlightActionTypes.ON_SUCCESS, items = items))
    fun onFailure(e: Throwable) = dispatch(actions(type = PenlightActionTypes.ON_FAILURE, error = e))

    fun PenlightController.fetch(ids: List<String>) = launch {
        runCatching { fetchItems(ids) }
            .onSuccess(::onSuccess)
            .onFailure(::onFailure)
    }

    useEffectDidMount { controller.fetch(ids) }

    penlightModal { attrs.model = uiModel }
}

private enum class PenlightActionTypes {
    ON_SUCCESS, ON_FAILURE
}

private fun actions(
    type: PenlightActionTypes,
    items: List<IdolColor> = listOf(),
    error: Throwable? = null
) = utilities.actions<PenlightActionTypes, UiModel.Penlight> {
    this.type = type
    this.payload = UiModel.Penlight(items = items, error = error)
}

private val reducer = { state: UiModel.Penlight, action: Actions<PenlightActionTypes, UiModel.Penlight> ->
    val (items, error, _) = action.payload

    when (action.type) {
        PenlightActionTypes.ON_SUCCESS -> state.copy(items = items, error = null, isLoading = false)
        PenlightActionTypes.ON_FAILURE -> state.copy(items = listOf(), error = error, isLoading = false)
    }
}

private val PenlightControllerContext = createContext<PenlightController>()

private object PenlightController : CoroutineScope by mainScope, KodeinAware {
    val repository: IdolColorsRepository by instance()

    suspend fun fetchItems(ids: List<String>) = repository.search(ids)

    override val kodein = appKodein
}
