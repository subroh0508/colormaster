package page

import CurrentLocalKoinApp
import androidx.compose.runtime.*
import components.atoms.backdrop.Backdrop
import components.atoms.backdrop.BackdropValues
import components.atoms.backdrop.rememberBackdropState
import components.templates.search.BackLayer
import components.templates.search.FrontLayer
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.model.HexColor
import net.subroh0508.colormaster.model.IdolName
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.koin.core.KoinApplication

data class IdolColorState(
    val id: String,
    val name: IdolName,
    val hexColor: HexColor,
    val selected: Boolean = false,
    val inCharge: Boolean = false,
    val favorite: Boolean = false,
)

data class SearchUiModel(
    private val items: List<IdolColorState> = listOf(),
    val loading: Boolean = false,
    val error: Throwable? = null,
) : List<IdolColorState> by items {
    constructor(loadState: LoadState) : this(
        loadState.getValueOrNull() ?: listOf(),
        loadState.isLoading,
        loadState.getErrorOrNull(),
    )
}

@Composable
fun SearchIdolPage(
    variant: String,
    appBar: @Composable (String) -> Unit,
) {
    val backdropState = rememberBackdropState(BackdropValues.Concealed)
    val model = rememberSearchUiModel()

    Backdrop(
        backdropState,
        appBar = { appBar(variant) },
        backLayerContent = { BackLayer(variant) },
        frontLayerContent = { FrontLayer(backdropState, model.value) },
    )
}

@Composable
fun rememberSearchUiModel(
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): MutableState<SearchUiModel> {
    val scope = rememberCoroutineScope()
    val repository: IdolColorsRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }
    val state = remember(koinApp) { mutableStateOf(SearchUiModel()) }

    SideEffect {
        val job = scope.launch {
            runCatching { repository.rand(10) }
                .onSuccess { state.value = SearchUiModel(LoadState.Loaded(it)) }
                .onFailure { state.value = SearchUiModel(LoadState.Error(it)) }
        }

        state.value = SearchUiModel(LoadState.Loading)
        job.start()
    }

    return state
}
