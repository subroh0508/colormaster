package page

import androidx.compose.runtime.*
import components.atoms.backdrop.Backdrop
import components.atoms.backdrop.BackdropValues
import components.atoms.backdrop.rememberBackdropState
import components.templates.search.BackLayer
import components.templates.search.frontlayer.FrontLayer
import kotlinx.coroutines.launch
import net.subroh0508.colormaster.presentation.common.LoadState
import net.subroh0508.colormaster.presentation.search.model.SearchParams
import net.subroh0508.colormaster.repository.IdolColorsRepository
import org.koin.core.KoinApplication
import utilities.CurrentLocalKoinApp

@Composable
fun SearchIdolPage(
    variant: String,
    appBar: @Composable (String) -> Unit,
) {
    val backdropState = rememberBackdropState(BackdropValues.Concealed)
    val (params, setParams) = remember { mutableStateOf<SearchParams?>(null) }
    val idolLoadState by rememberSearchIdolsUseCase(params)

    Backdrop(
        backdropState,
        appBar = { appBar(variant) },
        backLayerContent = {
            BackLayer(variant, setParams)
        },
        frontLayerContent = {
            FrontLayer(backdropState, params, idolLoadState)
        },
    )
}

@Composable
fun rememberSearchIdolsUseCase(
    params: SearchParams?,
    koinApp: KoinApplication = CurrentLocalKoinApp(),
): State<LoadState> {
    val scope = rememberCoroutineScope()
    val repository: IdolColorsRepository by remember(koinApp) { mutableStateOf(koinApp.koin.get()) }

    return produceState<LoadState>(
        initialValue = LoadState.Initialize,
        params,
    ) {
        val job = scope.launch {
            runCatching {
                when (params) {
                    is SearchParams.ByName -> params.takeUnless { it.isEmpty() }?.let {
                        repository.search(it.idolName, it.brands, it.types)
                    } ?: repository.rand(10)
                    is SearchParams.ByLive -> params.liveName?.let {
                        repository.search(it)
                    } ?: listOf()
                    else -> listOf()
                }
            }
                .onSuccess { value = LoadState.Loaded(it) }
                .onFailure { value = LoadState.Error(it) }
        }

        value = LoadState.Loading
        job.start()
    }
}
