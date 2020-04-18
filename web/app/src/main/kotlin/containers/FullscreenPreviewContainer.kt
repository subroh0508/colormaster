package containers

import appKodein
import components.atoms.FullscreenPenlightComponent
import components.atoms.FullscreenPreviewComponent
import components.templates.previewModal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mainScope
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.model.UiModel
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.kodein.di.KodeinAware
import org.kodein.di.erased.instance
import react.*
import useQuery
import utilities.Actions
import utilities.child
import utilities.children
import utilities.useEffectDidMount

@Suppress("FunctionName")
fun RBuilder.PenlightContainer() = FullscreenPreviewControllerContext.Provider(FullscreenPreviewController) {
    child(FullscreenPreviewContainerComponentImpl) { model ->
        previewModal {
            attrs.model = model
            attrs.PreviewComponent = FullscreenPenlightComponent
        }
    }
}

@Suppress("FunctionName")
fun RBuilder.PreviewContainer() = FullscreenPreviewControllerContext.Provider(FullscreenPreviewController) {
    child(FullscreenPreviewContainerComponentImpl) { model ->
        previewModal {
            attrs.model = model
            attrs.PreviewComponent = FullscreenPreviewComponent
        }
    }
}

private val FullscreenPreviewContainerComponentImpl = functionalComponent<FullscreenPreviewContainerProps> { props ->
    val ids = useQuery().getAll("id").toList()

    val controller = useContext(FullscreenPreviewControllerContext)

    val (uiModel, dispatch) = useReducer(reducer, UiModel.FullscreenPreview.INITIALIZED)

    fun onSuccess(items: List<IdolColor>) = dispatch(actions(type = FullscreenPreviewActionType.ON_SUCCESS, items = items))
    fun onFailure(e: Throwable) = dispatch(actions(type = FullscreenPreviewActionType.ON_FAILURE, error = e))

    fun FullscreenPreviewController.fetch(ids: List<String>) = launch {
        runCatching { fetchItems(ids) }
            .onSuccess(::onSuccess)
            .onFailure(::onFailure)
    }

    useEffectDidMount { controller.fetch(ids) }

    children(props, uiModel)
}

external interface FullscreenPreviewContainerProps : RConsumerProps<UiModel.FullscreenPreview>

private enum class FullscreenPreviewActionType {
    ON_SUCCESS, ON_FAILURE
}

private fun actions(
    type: FullscreenPreviewActionType,
    items: List<IdolColor> = listOf(),
    error: Throwable? = null
) = utilities.actions<FullscreenPreviewActionType, UiModel.FullscreenPreview> {
    this.type = type
    this.payload = UiModel.FullscreenPreview(items = items, error = error)
}

private val reducer = { state: UiModel.FullscreenPreview, action: Actions<FullscreenPreviewActionType, UiModel.FullscreenPreview> ->
    val (items, error, _) = action.payload

    when (action.type) {
        FullscreenPreviewActionType.ON_SUCCESS -> state.copy(items = items, error = null, isLoading = false)
        FullscreenPreviewActionType.ON_FAILURE -> state.copy(items = listOf(), error = error, isLoading = false)
    }
}

private val FullscreenPreviewControllerContext = createContext<FullscreenPreviewController>()

private object FullscreenPreviewController : CoroutineScope by mainScope, KodeinAware {
    val repository: IdolColorsRepository by instance()

    suspend fun fetchItems(ids: List<String>) = repository.search(ids)

    override val kodein = appKodein
}
