package containers

import KoinComponent
import KoinContext
import components.organisms.FullscreenPenlightComponent
import components.organisms.FullscreenPreviewComponent
import components.templates.previewModal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.subroh0508.colormaster.presentation.preview.model.FullscreenPreviewUiModel
import net.subroh0508.colormaster.presentation.preview.viewmodel.PreviewViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import react.*
import useQuery

@Suppress("FunctionName")
fun RBuilder.PenlightContainer() = child(FullscreenPreviewContextProvider) {
    attrs.children { model ->
        previewModal {
            attrs.model = model
            attrs.PreviewComponent = FullscreenPenlightComponent
        }
    }
}

@Suppress("FunctionName")
fun RBuilder.PreviewContainer() = child(FullscreenPreviewContextProvider) {
    attrs.children { model ->
        previewModal {
            attrs.model = model
            attrs.PreviewComponent = FullscreenPreviewComponent
        }
    }
}

private const val PREVIEW_SCOPE_ID = "PREVIEW_SCOPE"

private val FullscreenPreviewContext = createContext<PreviewViewModel>()
private fun module(appScope: CoroutineScope) = module {
    scope(named(PREVIEW_SCOPE_ID)) {
        scoped { PreviewViewModel(get(), appScope) }
    }
}

private val FullscreenPreviewContextProvider = KoinComponent<PreviewViewModel, FullscreenPreviewContainerProps>(
    FullscreenPreviewContext,
    PREVIEW_SCOPE_ID,
    ::module,
) { props ->
    child(FullscreenPreviewContainer) {
        attrs.children = { model -> props.children(model) }
    }
}

private val FullscreenPreviewContainer = functionComponent<FullscreenPreviewContainerProps> { props ->
    val ids = useQuery().getAll("id")

    val (_, appScope) = useContext(KoinContext)
    val viewModel = useContext(FullscreenPreviewContext)

    val (uiModel, setUiModel) = useState(FullscreenPreviewUiModel.INITIALIZED)

    useEffectOnce {
        viewModel.uiModel.onEach {
            setUiModel(it)
        }.launchIn(appScope)

        viewModel.fetch(ids.toList())
    }

    props.children(this, uiModel)
}

private external interface FullscreenPreviewContainerProps : Props {
    var children: (FullscreenPreviewUiModel) -> ReactElement
}

private fun FullscreenPreviewContainerProps.children(render: RBuilder.(FullscreenPreviewUiModel) -> Unit) {
    children = { model -> buildElement(RBuilder()) { render(model) } }
}

private fun FullscreenPreviewContainerProps.children(builder: RBuilder, model: FullscreenPreviewUiModel) {
    builder.child(children(model))
}
