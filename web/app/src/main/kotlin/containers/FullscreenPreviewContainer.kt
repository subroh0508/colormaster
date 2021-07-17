package containers

import KoinReactComponent
import components.organisms.FullscreenPenlightComponent
import components.organisms.FullscreenPreviewComponent
import components.templates.previewModal
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import net.subroh0508.colormaster.presentation.preview.model.FullscreenPreviewUiModel
import net.subroh0508.colormaster.presentation.preview.viewmodel.PreviewViewModel
import org.koin.core.component.inject
import org.koin.dsl.module
import react.*
import useQuery

@Suppress("FunctionName")
fun RBuilder.PenlightContainer() = childFunction(FullscreenPreviewContainerComponentImpl) { model: FullscreenPreviewUiModel ->
    previewModal {
        attrs.model = model
        attrs.PreviewComponent = FullscreenPenlightComponent
    }
}

@Suppress("FunctionName")
fun RBuilder.PreviewContainer() = childFunction(FullscreenPreviewContainerComponentImpl) { model: FullscreenPreviewUiModel ->
    previewModal {
        attrs.model = model
        attrs.PreviewComponent = FullscreenPreviewComponent
    }
}

private val FullscreenPreviewContainerComponentImpl = functionalComponent<RProps> { props ->
    val ids = useQuery().getAll("id")

    childFunction(FullscreenPreviewContainer::class, {
        attrs.ids = ids
    }) { model: FullscreenPreviewUiModel -> props.children(model) }
}

@JsExport
class FullscreenPreviewContainer : KoinReactComponent<FullscreenPreviewProps, FullscreenPreviewState>(
    module {
        scope<FullscreenPreviewContainer> {
            scoped { PreviewViewModel(get()) }
        }
    }
) {
    private val viewModel: PreviewViewModel by inject()

    override fun FullscreenPreviewState.init() {
        uiModel = FullscreenPreviewUiModel.INITIALIZED
    }

    override fun componentDidMount() {
        viewModel.uiModel.onEach {
            setState { uiModel = it }
        }.launchIn(this)

        viewModel.fetch(props.ids.toList())
    }

    override fun RBuilder.render() {
        props.children(state.uiModel)
    }
}

external interface FullscreenPreviewProps : RProps {
    var ids: Array<String>
}

external interface FullscreenPreviewState : RState {
    var uiModel: FullscreenPreviewUiModel
}
