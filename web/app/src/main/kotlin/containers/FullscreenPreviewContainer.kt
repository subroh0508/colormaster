package containers

import KoinAppContext
import components.organisms.FullscreenPenlightComponent
import components.organisms.FullscreenPreviewComponent
import components.templates.previewModal
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.subroh0508.colormaster.presentation.preview.model.FullscreenPreviewUiModel
import net.subroh0508.colormaster.presentation.preview.viewmodel.PreviewViewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import react.*
import useQuery

@Suppress("FunctionName")
fun RBuilder.PenlightContainer() = childFunction(FullscreenPreviewContextProviderContainer) { model: FullscreenPreviewUiModel ->
    previewModal {
        attrs.model = model
        attrs.PreviewComponent = FullscreenPenlightComponent
    }
}

@Suppress("FunctionName")
fun RBuilder.PreviewContainer() = childFunction(FullscreenPreviewContextProviderContainer) { model: FullscreenPreviewUiModel ->
    previewModal {
        attrs.model = model
        attrs.PreviewComponent = FullscreenPreviewComponent
    }
}

private const val PREVIEW_SCOPE_ID = "PREVIEW_SCOPE"

private val FullscreenPreviewContext = createContext<PreviewViewModel>()

private val FullscreenPreviewContextProviderContainer = functionalComponent<RProps> { props ->
    val ids = useQuery().getAll("id")

    val (koinApp, appScope) = useContext(KoinAppContext)
    val (viewModel, setViewModel) = useState<PreviewViewModel>()

    useEffectOnce {
        val module = module {
            scope(named(PREVIEW_SCOPE_ID)) {
                scoped { PreviewViewModel(get(), appScope) }
            }
        }

        koinApp.modules(module)
        koinApp.koin.createScope(PREVIEW_SCOPE_ID, named(PREVIEW_SCOPE_ID))

        setViewModel(value = koinApp.koin.getScope(PREVIEW_SCOPE_ID).get())

        cleanup {
            koinApp.unloadModules(module)
            koinApp.koin.deleteScope(PREVIEW_SCOPE_ID)
        }
    }

    viewModel ?: return@functionalComponent

    FullscreenPreviewContext.Provider {
        attrs.value = viewModel

        childFunction(FullscreenPreviewContainerComponent, handler = {
            attrs.ids = ids
        }) { model: FullscreenPreviewUiModel -> props.children(model) }
    }
}

private val FullscreenPreviewContainerComponent = functionalComponent<FullscreenPreviewProps> { props ->
    val (_, appScope) = useContext(KoinAppContext)
    val viewModel = useContext(FullscreenPreviewContext)

    val (uiModel, setUiModel) = useState(FullscreenPreviewUiModel.INITIALIZED)

    useEffectOnce {
        viewModel.uiModel.onEach {
            setUiModel(it)
        }.launchIn(appScope)

        viewModel.fetch(props.ids.toList())
    }

    props.children(uiModel)
}

private external interface FullscreenPreviewProps : RProps {
    var ids: Array<String>
}
