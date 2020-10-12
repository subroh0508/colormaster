package containers

import appDI
import components.organisms.FullscreenPenlightComponent
import components.organisms.FullscreenPreviewComponent
import components.templates.previewModal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import mainScope
import net.subroh0508.colormaster.model.IdolColor
import net.subroh0508.colormaster.presentation.preview.model.FullscreenPreviewUiModel
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import react.*
import useQuery
import utilities.Actions
import utilities.useEffectDidMount

@Suppress("FunctionName")
fun RBuilder.PenlightContainer() = FullscreenPreviewControllerContext.Provider(FullscreenPreviewController) {
    childFunction(FullscreenPreviewContainerComponentImpl) { model: FullscreenPreviewUiModel ->
        previewModal {
            attrs.model = model
            attrs.PreviewComponent = FullscreenPenlightComponent
        }
    }
}

@Suppress("FunctionName")
fun RBuilder.PreviewContainer() = FullscreenPreviewControllerContext.Provider(FullscreenPreviewController) {
    childFunction(FullscreenPreviewContainerComponentImpl) { model: FullscreenPreviewUiModel ->
        previewModal {
            attrs.model = model
            attrs.PreviewComponent = FullscreenPreviewComponent
        }
    }
}

private val FullscreenPreviewContainerComponentImpl = functionalComponent<RProps> { props ->
    val ids = useQuery().getAll("id").toList()

    val controller = useContext(FullscreenPreviewControllerContext)

    val (uiModel, dispatch) = useReducer(reducer, FullscreenPreviewUiModel.INITIALIZED)

    fun onSuccess(items: List<IdolColor>) = dispatch(actions(type = FullscreenPreviewActionType.ON_SUCCESS, items = items))
    fun onFailure(e: Throwable) = dispatch(actions(type = FullscreenPreviewActionType.ON_FAILURE, error = e))

    fun FullscreenPreviewController.fetch(ids: List<String>) = launch {
        runCatching { fetchItems(ids) }
            .onSuccess(::onSuccess)
            .onFailure(::onFailure)
    }

    useEffectDidMount { controller.fetch(ids) }

    props.children(uiModel)
}

private enum class FullscreenPreviewActionType {
    ON_SUCCESS, ON_FAILURE
}

private fun actions(
    type: FullscreenPreviewActionType,
    items: List<IdolColor> = listOf(),
    error: Throwable? = null
) = utilities.actions<FullscreenPreviewActionType, FullscreenPreviewUiModel> {
    this.type = type
    this.payload = FullscreenPreviewUiModel(items = items, error = error)
}

private val reducer = { state: FullscreenPreviewUiModel, action: Actions<FullscreenPreviewActionType, FullscreenPreviewUiModel> ->
    val (items, error, _) = action.payload

    when (action.type) {
        FullscreenPreviewActionType.ON_SUCCESS -> state.copy(items = items, error = null, isLoading = false)
        FullscreenPreviewActionType.ON_FAILURE -> state.copy(items = listOf(), error = error, isLoading = false)
    }
}

private val FullscreenPreviewControllerContext = createContext<FullscreenPreviewController>()

private object FullscreenPreviewController : CoroutineScope by mainScope, KoinComponent {
    val repository: IdolColorsRepository by inject()

    suspend fun fetchItems(ids: List<String>) = repository.search(ids)

    override fun getKoin() = appDI.koin
}
